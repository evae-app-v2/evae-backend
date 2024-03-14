package com.example.demo.controllers;

import com.example.demo.DTO.RubriqueDTO;
import com.example.demo.constants.EvaeBackendConstants;
import com.example.demo.models.Rubrique;
import com.example.demo.services.RubriqueService;
import com.example.demo.utils.BackendUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rubriques")
public class RubriqueController {
    @Autowired
    private RubriqueService rubriqueService;

    @GetMapping
    public List<RubriqueDTO> getAllRubriques() {
        return rubriqueService.getAllRubriques();
    }

    @GetMapping("/{id}")
    public Rubrique getRubriqueById(@PathVariable Integer id) {
        return rubriqueService.getRubriqueById(id);
    }
    @GetMapping("/designation/{designation}")
    public ResponseEntity<Rubrique> getRubriqueByDesignation(@PathVariable String designation) {
        Optional<Rubrique> rubrique = rubriqueService.findRubriqueByDesignation(designation);
        return rubrique.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/update-ordre")
    public ResponseEntity<String> updateOrdre(@RequestBody List<Rubrique> updatedRubriquesData) {
        try {
            rubriqueService.updateOrdre(updatedRubriquesData);
            return ResponseEntity.ok("Ordre modifié avec succès");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping
    public ResponseEntity<Rubrique> createRubrique(@RequestBody Rubrique rubrique) {
        Rubrique createdRubrique = rubriqueService.createRubrique(rubrique);
        return new ResponseEntity<>(createdRubrique, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public Rubrique updateRubrique(@PathVariable Integer id, @RequestBody RubriqueDTO rubriqueDTO) {
        return rubriqueService.updateRubrique(id, rubriqueDTO);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRubrique(@PathVariable Integer id) {
        rubriqueService.deleteRubrique(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/rubriqueIsUsedInEvaluation/{id}")
    public boolean questionIsUsedInRubriqueOrEvaluation(@PathVariable Integer id){
        return rubriqueService.isRubriqueUsedInEvaluation(rubriqueService.getRubriqueById(id));
    }

}
