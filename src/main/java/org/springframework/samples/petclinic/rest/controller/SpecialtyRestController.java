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
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.protobuf.*;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
public class SpecialtyRestController {

    private final ClinicService clinicService;


    public SpecialtyRestController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @RequestMapping("listSpecialties")
    public ResponseEntity<ProtoSpecialties> listSpecialties() {

        List<Specialty> specialties = new ArrayList<>(this.clinicService.findAllSpecialties());

        if (specialties.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ProtoSpecialty> collection = new ArrayList<>();

        for (Specialty specialty : specialties) {
            ProtoSpecialty specialtyProto = ProtoSpecialty.newBuilder().setId(specialty.getId()).setName(specialty.getName()).build();
            collection.add(specialtyProto);
        }

        ProtoSpecialties ls = ProtoSpecialties.newBuilder().addAllSpecialties(collection).build();

        return new ResponseEntity<>(ls, HttpStatus.OK);

    }

    @RequestMapping("getSpecialty/{specialtyId}")
    public ResponseEntity<ProtoSpecialty> getSpecialty(@PathVariable("specialtyId") Integer specialtyId) {
        Specialty specialty = this.clinicService.findSpecialtyById(specialtyId);

        if (specialty == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ProtoSpecialty specialtyProto = ProtoSpecialty.newBuilder().setId(specialty.getId()).setName(specialty.getName()).build();

        return new ResponseEntity<>(specialtyProto, HttpStatus.OK);

    }

    @PostMapping (value = "addSpecialty")
    public ResponseEntity<ProtoSpecialty> addSpecialty(@RequestBody ProtoSpecialtyAdd specialtyProtoAdd) {

        Specialty specialty = new Specialty();
        specialty.setName(specialtyProtoAdd.getName());
        this.clinicService.saveSpecialty(specialty);

        ProtoSpecialty specialtyProto = ProtoSpecialty.newBuilder().setId(specialty.getId()).setName(specialty.getName()).build();

        return new ResponseEntity<>(specialtyProto, HttpStatus.CREATED);
    }

    @PutMapping("updateSpecialty/{specialtyId}")
    public ResponseEntity<ProtoSpecialty> updateSpecialty(@PathVariable ("specialtyId") Integer specialtyId, @RequestBody ProtoSpecialtyAdd protoAdd) {

        Specialty currentSpecialty = this.clinicService.findSpecialtyById(specialtyId);
        if (currentSpecialty == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        currentSpecialty.setName(protoAdd.getName());
        this.clinicService.saveSpecialty(currentSpecialty);

        ProtoSpecialty specialtyProto = ProtoSpecialty.newBuilder().setId(currentSpecialty.getId()).setName(currentSpecialty.getName()).build();

        return new ResponseEntity<>(specialtyProto, HttpStatus.OK);

    }

    @DeleteMapping("deleteSpecialty/{specialtyId}")
    public ResponseEntity<ProtoSpecialty> deleteSpecialty(@PathVariable("specialtyId") Integer specialtyId) {
        Specialty specialty = this.clinicService.findSpecialtyById(specialtyId);
        if (specialty == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deleteSpecialty(specialty);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
