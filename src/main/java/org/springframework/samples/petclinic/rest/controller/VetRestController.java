/*
 * Copyright 2016-2018 the original author or authors.
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
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.protobuf.*;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
public class VetRestController {

    private final ClinicService clinicService;

    public VetRestController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping("vets")
    public ResponseEntity<ProtoVets> listVets() {

        List<Vet> vets = new ArrayList<>(this.clinicService.findAllVets());

        if (vets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ProtoVet> collection = new ArrayList<>();

        for (Vet vet : vets) {

            List<ProtoSpecialty> collection2 = new ArrayList<>();

            for (Specialty s : vet.getSpecialties()) {
                ProtoSpecialty vProto = ProtoSpecialty.newBuilder().setId(s.getId()).setName(s.getName()).build();
                collection2.add(vProto);
            }

            ProtoVet vetProto = ProtoVet.newBuilder().setId(vet.getId()).setFirstName(vet.getFirstName()).setLastName(vet.getLastName())
                .addAllSpecialties(collection2).build();

            collection.add(vetProto);

        }

        ProtoVets protoVets = ProtoVets.newBuilder().addAllVets(collection).build();

        return new ResponseEntity<>(protoVets, HttpStatus.OK);

    }

    @GetMapping("vets/{vetId}")
    public ResponseEntity<ProtoVet> getVet(@PathVariable ("vetId") Integer vetId)  {

        Vet vet = this.clinicService.findVetById(vetId);
        if (vet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ProtoSpecialty> collection = new ArrayList<>();

        for (Specialty s : vet.getSpecialties()) {
            ProtoSpecialty vProto = ProtoSpecialty.newBuilder().setId(s.getId()).setName(s.getName()).build();
            collection.add(vProto);
        }

        ProtoVet vetProto = ProtoVet.newBuilder().setId(vet.getId()).setFirstName(vet.getFirstName()).setLastName(vet.getLastName())
            .addAllSpecialties(collection).build();

        return new ResponseEntity<>(vetProto, HttpStatus.OK);

    }

//    @PostMapping (value = "addVet", consumes = "application/x-protobuf", produces = "application/x-protobuf")
//    public ResponseEntity<ProtoVet> addVet(@RequestBody ProtoVetAdd vetProtoAdd) {
//
//        Vet vet = new Vet();
//        vet.setFirstName(vetProtoAdd.getFirstName());
//        vet.setLastName(vetProtoAdd.getLastName());
//
//        for (ProtoSpecialty ps : vetProtoAdd.getSpecialtiesList()) {
//
//            for(Specialty s: this.clinicService.findAllSpecialties()) {
//
//                if(this.clinicService.findSpec
//
//            }
//            Specialty spec = new Specialty();
//            spec.setName(s.getName());
//            vet.addSpecialty(spec);
//
//        }
//
//        List<ProtoSpecialty> collection = new ArrayList<>();
//
//        for (Specialty s : vet.getSpecialties()) {
//            ProtoSpecialty vProto = ProtoSpecialty.newBuilder().setId(s.getId()).setName(s.getName()).build();
//            collection.add(vProto);
//        }
//
//        ProtoVet vetProto = ProtoVet.newBuilder().setId(vet.getId()).setFirstName(vet.getFirstName()).setLastName(vet.getLastName())
//            .addAllSpecialties(collection).build();
//
//        this.clinicService.saveVet(vet);
//        return new ResponseEntity<>(vetProto, HttpStatus.CREATED);
//    }

    @PutMapping("vets/{vetId}")
    public ResponseEntity<ProtoVet> updateVet(@PathVariable ("vetId") Integer vetId, @RequestBody ProtoVetAdd protoAdd)  {

        Vet currentVet = this.clinicService.findVetById(vetId);
        if (currentVet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        currentVet.setFirstName(protoAdd.getFirstName());
        currentVet.setLastName(protoAdd.getLastName());
        currentVet.clearSpecialties();

        for (ProtoSpecialty ps : protoAdd.getSpecialtiesList()) {

            for(Specialty s: this.clinicService.findAllSpecialties()) {

                if(s.getName().equals(ps.getName())){

                    currentVet.addSpecialty(s);

                }

            }

        }

        this.clinicService.saveVet(currentVet);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @DeleteMapping("vets/{vetId}")
    public ResponseEntity<VetDto> deleteVet(@PathVariable ("vetId") Integer vetId) {
        Vet vet = this.clinicService.findVetById(vetId);
        if (vet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deleteVet(vet);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
