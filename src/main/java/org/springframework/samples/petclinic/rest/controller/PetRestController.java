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
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.protobuf.*;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
public class PetRestController {

    private final ClinicService clinicService;

    public PetRestController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @RequestMapping("listPets")
    public ResponseEntity<ProtoPets> listPets() {
        List<Pet> pets = new ArrayList<>(this.clinicService.findAllPets());
        if (pets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ProtoPet> collection = new ArrayList<>();

        for (Pet pet : pets) {

            List<ProtoPetVisit> collection2 = new ArrayList<>();

            for (Visit v : pet.getVisits()) {
                ProtoPetVisit vProto = ProtoPetVisit.newBuilder().setId(v.getId()).setDate(v.getDate().toString()).setDescription(v.getDescription()).build();
                collection2.add(vProto);
            }

            ProtoPetOwner protoPetOwner = ProtoPetOwner.newBuilder().setFirstName(pet.getOwner().getFirstName()).setLastName(pet.getOwner().getLastName())
                .setAddress(pet.getOwner().getAddress()).setCity(pet.getOwner().getCity()).setTelephone(pet.getOwner().getTelephone()).build();

            ProtoPet petProto = ProtoPet.newBuilder().setId(pet.getId()).setName(pet.getName()).setBirthDate(pet.getBirthDate().toString()).
                setPetType(pet.getType().getName()).setOwner(protoPetOwner).addAllVisits(collection2).build();

            collection.add(petProto);

        }

        ProtoPets protoPets = ProtoPets.newBuilder().addAllPets(collection).build();

        return new ResponseEntity<>(protoPets, HttpStatus.OK);

    }

    @RequestMapping("getPet/{petId}")
    public ResponseEntity<ProtoPet> getPet(@PathVariable("petId")Integer petId) {

        Pet pet = this.clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ProtoPetVisit> collection = new ArrayList<>();

        for (Visit v : pet.getVisits()) {
            ProtoPetVisit vProto = ProtoPetVisit.newBuilder().setId(v.getId()).setDate(v.getDate().toString()).setDescription(v.getDescription()).build();
            collection.add(vProto);
        }

        ProtoPetOwner protoPetOwner = ProtoPetOwner.newBuilder().setFirstName(pet.getOwner().getFirstName()).setLastName(pet.getOwner().getLastName())
            .setAddress(pet.getOwner().getAddress()).setCity(pet.getOwner().getCity()).setTelephone(pet.getOwner().getTelephone()).build();

        ProtoPet petProto = ProtoPet.newBuilder().setId(pet.getId()).setName(pet.getName()).setBirthDate(pet.getBirthDate().toString()).
            setPetType(pet.getType().getName()).setOwner(protoPetOwner).addAllVisits(collection).build();

        return new ResponseEntity<>(petProto, HttpStatus.OK);

    }

    @PostMapping (value = "addPet", consumes = "application/x-protobuf", produces = "application/x-protobuf")
    public ResponseEntity<ProtoPet> addPet(@RequestBody ProtoPetAdd petProto) {

        Pet pet = new Pet();
        pet.setName(petProto.getName());
        pet.setBirthDate(LocalDate.parse(petProto.getBirthDate()));
        pet.setType(this.clinicService.findPetTypeById(petProto.getPetTypeId()));
        pet.setOwner(this.clinicService.findOwnerById(petProto.getOwnerId()));
        this.clinicService.savePet(pet);

        ProtoPetOwner protoPetOwner = ProtoPetOwner.newBuilder().setFirstName(pet.getOwner().getFirstName()).setLastName(pet.getOwner().getLastName())
            .setAddress(pet.getOwner().getAddress()).setCity(pet.getOwner().getCity()).setTelephone(pet.getOwner().getTelephone()).build();

        ProtoPet petProtoResponse = ProtoPet.newBuilder().setId(pet.getId()).setName(pet.getName())
            .setBirthDate(pet.getBirthDate().toString()).setPetType(pet.getType().getName())
            .setOwner(protoPetOwner).build();

        return new ResponseEntity<>(petProtoResponse, HttpStatus.OK);

    }

    @PutMapping("updatePet/{petId}")
    public ResponseEntity<ProtoPet> updatePet(@PathVariable("petId") Integer petId, @RequestBody ProtoPetAdd petProto) {
        Pet currentPet = this.clinicService.findPetById(petId);
        if (currentPet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        currentPet.setBirthDate(LocalDate.parse(petProto.getBirthDate()));
        currentPet.setName(petProto.getName());
        currentPet.setType(this.clinicService.findPetTypeById(petProto.getPetTypeId()));
        currentPet.setOwner(this.clinicService.findOwnerById(petProto.getOwnerId()));
        this.clinicService.savePet(currentPet);

        List<ProtoPetVisit> collection = new ArrayList<>();

        for (Visit v : currentPet.getVisits()) {
            ProtoPetVisit vProto = ProtoPetVisit.newBuilder().setId(v.getId()).setDate(v.getDate().toString()).setDescription(v.getDescription()).build();
            collection.add(vProto);
        }

        ProtoPetOwner protoPetOwner = ProtoPetOwner.newBuilder().setFirstName(currentPet.getOwner().getFirstName()).setLastName(currentPet.getOwner().getLastName())
            .setAddress(currentPet.getOwner().getAddress()).setCity(currentPet.getOwner().getCity()).setTelephone(currentPet.getOwner().getTelephone()).build();

        ProtoPet petProtoResponse = ProtoPet.newBuilder().setId(currentPet.getId()).setName(currentPet.getName())
            .setBirthDate(currentPet.getBirthDate().toString()).setPetType(currentPet.getType().getName()).
            setOwner(protoPetOwner).addAllVisits(collection).build();

        return new ResponseEntity<>(petProtoResponse, HttpStatus.OK);

    }

    @DeleteMapping("deletePet/{petId}")
    public ResponseEntity<ProtoPet> deletePet(@PathVariable("petId") Integer petId) {
        Pet pet = this.clinicService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deletePet(pet);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
