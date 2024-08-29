/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.protobuf.*;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
public class OwnerRestController {

    private final ClinicService clinicService;

    public OwnerRestController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping("owners")
    public ResponseEntity<ProtoOwners> listOwners() {

        Collection<Owner> owners;

        owners = this.clinicService.findAllOwners();

        List<ProtoOwner> collection = new ArrayList<>();

        for (Owner owner : owners) {

            List<ProtoPet> collection2 = new ArrayList<>();

            for (Pet p : owner.getPets()) {

                ProtoPetType protoPetType = ProtoPetType.newBuilder().setId(p.getType().getId()).setName(p.getType().getName()).build();

                List<ProtoPetVisit> collection3 = new ArrayList<>();

                for (Visit v: p.getVisits()) {
                    ProtoPetVisit protoVisit = ProtoPetVisit.newBuilder().setId(v.getId()).setDate(v.getDate().toString()).
                        setDescription(v.getDescription()).build();

                    collection3.add(protoVisit);
                }

                ProtoPet protoPet = ProtoPet.newBuilder().setId(p.getId()).setName(p.getName()).setType(protoPetType).
                    setBirthDate((p.getBirthDate().toString())).setOwnerId(p.getOwner().getId()).addAllVisits(collection3).build();

                collection2.add(protoPet);

            }

            ProtoOwner pOwner = ProtoOwner.newBuilder().setId(owner.getId()).setFirstName(owner.getFirstName()).
                setLastName(owner.getLastName()).setAddress(owner.getAddress()).setCity(owner.getCity()).setTelephone(owner.getTelephone()).
                addAllPets(collection2).build();

            collection.add(pOwner);
        }

        ProtoOwners protoOwners = ProtoOwners.newBuilder().addAllOwners(collection).build();

        return new ResponseEntity<>(protoOwners, HttpStatus.OK);

    }

    @GetMapping("owners/{ownerId}")
    public ResponseEntity<ProtoOwner> getOwner(@PathVariable("ownerId") Integer ownerId) {

        Owner owner = this.clinicService.findOwnerById(ownerId);
        if (owner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ProtoPet> collection = new ArrayList<>();

        for (Pet p : owner.getPets()) {

            ProtoPetType protoPetType = ProtoPetType.newBuilder().setId(p.getType().getId()).setName(p.getType().getName()).build();

            List<ProtoPetVisit> collection2 = new ArrayList<>();

            for (Visit v: p.getVisits()) {
                ProtoPetVisit protoVisit = ProtoPetVisit.newBuilder().setId(v.getId()).setDate(v.getDate().toString()).
                    setDescription(v.getDescription()).build();

                collection2.add(protoVisit);
            }

            ProtoPet protoPet = ProtoPet.newBuilder().setId(p.getId()).setName(p.getName()).setType(protoPetType).
                setBirthDate((p.getBirthDate().toString())).setOwnerId(p.getOwner().getId()).addAllVisits(collection2).build();

            collection.add(protoPet);

        }

        ProtoOwner pOwner = ProtoOwner.newBuilder().setId(owner.getId()).setFirstName(owner.getFirstName()).
            setLastName(owner.getLastName()).setAddress(owner.getAddress()).setCity(owner.getCity()).setTelephone(owner.getTelephone()).
            addAllPets(collection).build();

        return new ResponseEntity<>(pOwner, HttpStatus.OK);

    }

    @PostMapping ("owners")
    public ResponseEntity<ProtoOwner> addOwner(@RequestBody ProtoOwner protoOwner) {

        Owner owner = new Owner();

        owner.setFirstName(protoOwner.getFirstName());
        owner.setLastName(protoOwner.getLastName());
        owner.setAddress(protoOwner.getAddress());
        owner.setCity(protoOwner.getCity());
        owner.setTelephone(protoOwner.getTelephone());

        this.clinicService.saveOwner(owner);

        ProtoOwner pOwner = ProtoOwner.newBuilder().setId(owner.getId()).setFirstName(owner.getFirstName()).
            setLastName(owner.getLastName()).setAddress(owner.getAddress()).setCity(owner.getCity()).setTelephone(owner.getTelephone()).build();

        return new ResponseEntity<>(pOwner, HttpStatus.CREATED);

    }

    @PutMapping("owners/{ownerId}")
    public ResponseEntity<ProtoOwner> updateOwner(@PathVariable("ownerId") Integer ownerId, @RequestBody ProtoOwnerAdd protoAddOwner) {

        Owner currentOwner = this.clinicService.findOwnerById(ownerId);
        if (currentOwner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentOwner.setAddress(protoAddOwner.getAddress());
        currentOwner.setCity(protoAddOwner.getCity());
        currentOwner.setFirstName(protoAddOwner.getFirstName());
        currentOwner.setLastName(protoAddOwner.getLastName());
        currentOwner.setTelephone(protoAddOwner.getTelephone());
        this.clinicService.saveOwner(currentOwner);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @DeleteMapping("owners/{ownerId}")
    public ResponseEntity<ProtoOwner> deleteOwner(@PathVariable("ownerId") Integer ownerId) {
        Owner owner = this.clinicService.findOwnerById(ownerId);
        if (owner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deleteOwner(owner);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping ("owners/{ownerId}/pets")
    public ResponseEntity<ProtoOwnerPet> addPetToOwner(@PathVariable("ownerId") Integer ownerId, @RequestBody ProtoOwnerAddPet petProto) {

        Pet pet = new Pet();
        pet.setName(petProto.getName());
        pet.setBirthDate(LocalDate.parse(petProto.getBirthDate()));
        pet.setType(this.clinicService.findPetTypeById(petProto.getType().getId()));
        pet.setOwner(this.clinicService.findOwnerById(ownerId));
        this.clinicService.savePet(pet);

        ProtoOwnerPet petProtoResponse = ProtoOwnerPet.newBuilder().setId(pet.getId()).setName(pet.getName())
            .setBirthDate(pet.getBirthDate().toString()).setPetType(pet.getType().getName()).build();

        return new ResponseEntity<>(petProtoResponse, HttpStatus.CREATED);

    }

    @PostMapping ("owners/{ownerId}/pets/{petId}/visits")
    public ResponseEntity<ProtoPetVisit> addVisitToOwner(@PathVariable("petId") Integer petId, @RequestBody ProtoOwnerAddVisit protoOwnerAddVisit) {

        Visit visit = new Visit();
        visit.setDescription(protoOwnerAddVisit.getVisitDescription());
        visit.setDate(LocalDate.parse(protoOwnerAddVisit.getVisitDate()));
        visit.setPet(this.clinicService.findPetById(petId));

        this.clinicService.saveVisit(visit);
        ProtoPetVisit vProto = ProtoPetVisit.newBuilder().setId(visit.getId()).setDate(visit.getDate().toString()).setDescription(visit.getDescription()).
        setPetId(petId).build();

        return new ResponseEntity<>(vProto, HttpStatus.CREATED);

    }

    @GetMapping("owners/{ownerId}/{petId}")
    public ResponseEntity<ProtoOwnerPet> getOwnersPet(@PathVariable("ownerId") Integer ownerId, @PathVariable("petId") Integer petId) {

        Owner owner = this.clinicService.findOwnerById(ownerId);
        Pet p = this.clinicService.findPetById(petId);
        if (owner == null || p == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {

            ProtoOwnerPet protoOwnerPet = ProtoOwnerPet.newBuilder().setId(p.getId()).setName(p.getName()).setPetType(p.getType().getName()).
                setBirthDate((p.getBirthDate().toString())).build();

            return new ResponseEntity<>(protoOwnerPet, HttpStatus.OK);

        }

    }

}
