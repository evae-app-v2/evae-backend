package com.example.demo.controllers;

import com.example.demo.models.Qualificatif;

import com.example.demo.services.QualificatifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
public class QualificatifController {


    @Autowired
    private QualificatifService qualificatifService;

    @PostMapping("/addQualificatif")
    public Qualificatif addQualificatif(@RequestBody Qualificatif qualificatif){
        return qualificatifService.saveQualificatif(qualificatif);
    }

    @GetMapping("/qualificatifs")
    public List<Qualificatif> findAllQualificatifs(){
        return qualificatifService.getQualificatifs();

    }

    @GetMapping("/qualificatif/{id}")
    public Qualificatif findQualificatifById(@PathVariable int id){
        return qualificatifService.getQualificatifById(id);
    }

    @PostMapping("/update")
    public Qualificatif updateQualificatif(@RequestBody Qualificatif qualificatif){
        return qualificatifService.updateQualificatif(qualificatif);
    }

    @GetMapping("/delete/{id}")
    public String deleteQualificatif(@PathVariable int id){
        return qualificatifService.deleteQualificatif(id);
    }


    @GetMapping("/qualificatifIsUsedInQuestion/{id}")
    public boolean qualificatifIsUsedInQuestion(@PathVariable int id){
        return qualificatifService.qualificatifIsUsedInQuestion(id);
    }
}