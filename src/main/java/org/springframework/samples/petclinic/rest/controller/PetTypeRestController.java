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
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.protobuf.*;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
public class PetTypeRestController {

    private final ClinicService clinicService;

    public PetTypeRestController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @RequestMapping("listPetTypes")
    public ResponseEntity<ProtoPetTypes> listPetTypes() {
        List<PetType> petTypes = new ArrayList<>(this.clinicService.findAllPetTypes());
        if (petTypes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ProtoPetType> collection = new ArrayList<>();

        for (PetType petType : petTypes) {
            ProtoPetType petTypeProto = ProtoPetType.newBuilder().setId(petType.getId()).setName(petType.getName()).build();
            collection.add(petTypeProto);
        }

        ProtoPetTypes lp = ProtoPetTypes.newBuilder().addAllPetTypes(collection).build();

        return new ResponseEntity<>(lp, HttpStatus.OK);

    }

    @RequestMapping("getPetType/{petTypeId}")
    public ResponseEntity<ProtoPetType> getPetType(@PathVariable("petTypeId") Integer petTypeId) {
        PetType petType = this.clinicService.findPetTypeById(petTypeId);
        if (petType == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ProtoPetType petTypeProto = ProtoPetType.newBuilder().setId(petType.getId()).setName(petType.getName()).build();

        return new ResponseEntity<>(petTypeProto, HttpStatus.OK);

    }

    @PostMapping (value = "addPetType")
    public ResponseEntity<ProtoPetType> addPetType(@RequestBody ProtoPetTypeAdd petTypeProto) {

        PetType petType = new PetType();
        petType.setName(petTypeProto.getName());

        this.clinicService.savePetType(petType);

        ProtoPetType petTypeProtoResponse = ProtoPetType.newBuilder().setId(petType.getId()).setName(petType.getName()).build();

        return new ResponseEntity<>(petTypeProtoResponse, HttpStatus.OK);

    }

    @PutMapping("updatePetType/{petTypeId}")
    public ResponseEntity<ProtoPetType> updatePetType(@PathVariable("petTypeId") Integer petTypeId, @RequestBody ProtoPetTypeAdd petTypeProto) {

        PetType currentPetType = this.clinicService.findPetTypeById(petTypeId);
        if (currentPetType == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        currentPetType.setName(petTypeProto.getName());
        this.clinicService.savePetType(currentPetType);

        ProtoPetType petTypeProtoResponse = ProtoPetType.newBuilder().setId(currentPetType.getId()).setName(currentPetType.getName()).build();

        return new ResponseEntity<>(petTypeProtoResponse, HttpStatus.OK);

    }

    @DeleteMapping("deletePetType/{petTypeId}")
    public ResponseEntity<ProtoPetType> deletePetType(@PathVariable("petTypeId") Integer petTypeId) {

        PetType petType = this.clinicService.findPetTypeById(petTypeId);
        if (petType == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deletePetType(petType);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
