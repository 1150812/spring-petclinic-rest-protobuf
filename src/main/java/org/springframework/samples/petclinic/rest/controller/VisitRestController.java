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
public class VisitRestController {

    private final ClinicService clinicService;

    public VisitRestController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping("visits")
    public ResponseEntity<ProtoVisits> listVisits() {
        List<Visit> visits = new ArrayList<>(this.clinicService.findAllVisits());
        if (visits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ProtoVisit> collection = new ArrayList<>();

        for (Visit v: visits) {
            ProtoVisit protoVisit = ProtoVisit.newBuilder().setId(v.getId()).setDate(v.getDate().toString()).
                setDescription(v.getDescription()).setPetId(v.getPet().getId()).build();

            collection.add(protoVisit);
        }

        ProtoVisits protoVisits = ProtoVisits.newBuilder().addAllVisits(collection).build();

        return new ResponseEntity<>(protoVisits, HttpStatus.OK);

    }

    @GetMapping("visits/{visitId}")
    public ResponseEntity<ProtoVisit> getVisit(@PathVariable ("visitId") Integer visitId) {
        Visit v = this.clinicService.findVisitById(visitId);
        if (v == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ProtoVisit protoVisit = ProtoVisit.newBuilder().setId(v.getId()).setDate(v.getDate().toString()).
            setDescription(v.getDescription()).setPetId(v.getPet().getId()).build();

        return new ResponseEntity<>(protoVisit, HttpStatus.OK);

    }

//    @PostMapping ("visits")
//    public ResponseEntity<ProtoVisit> addVisit(@RequestBody ProtoVisitAdd protoVisitAdd) {
//
//        Visit visit = new Visit();
//
//        visit.setDate(LocalDate.parse(protoVisitAdd.getDate()));
//        visit.setDescription(protoVisitAdd.getDescription());
//        visit.setPet(this.clinicService.findPetById(protoVisitAdd.getPetId()));
//        this.clinicService.saveVisit(visit);
//
//        ProtoVisit protoVisit = ProtoVisit.newBuilder().setId(visit.getId()).setDate(visit.getDate().toString()).
//            setDescription(visit.getDescription()).setPetName(visit.getPet().getName()).build();
//
//        return new ResponseEntity<>(protoVisit, HttpStatus.CREATED);
//
//    }

    @PutMapping ("visits/{visitId}")
    public ResponseEntity<ProtoVisit> updateVisit(@PathVariable("visitId") Integer visitId, @RequestBody ProtoVisitAdd protoVisitAdd) {

        Visit currentVisit = this.clinicService.findVisitById(visitId);
        if (currentVisit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentVisit.setDate(LocalDate.parse(protoVisitAdd.getDate()));
        currentVisit.setDescription(protoVisitAdd.getDescription());
        this.clinicService.saveVisit(currentVisit);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @DeleteMapping("visits/{visitId}")
    public ResponseEntity<ProtoVisit> deleteVisit(@PathVariable ("visitId") Integer visitId) {

        Visit visit = this.clinicService.findVisitById(visitId);
        if (visit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deleteVisit(visit);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}
