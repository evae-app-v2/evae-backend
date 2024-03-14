package com.example.demo.services;

import com.example.demo.DTO.RubriqueDTO;
import com.example.demo.models.Rubrique;

import java.util.List;
import java.util.Optional;

public interface RubriqueService {
    List<RubriqueDTO> getAllRubriques();
    Rubrique getRubriqueById(Integer id);
    Rubrique createRubrique(Rubrique rubrique);
    Rubrique updateRubrique(Integer id, RubriqueDTO rubriqueDTO);
    void deleteRubrique(Integer id);

    boolean isRubriqueUsedInEvaluation(Rubrique rubrique);

    Optional<Rubrique> findRubriqueByDesignation(String designation);
    void updateOrdre(List<Rubrique> updatedRubriquesData);
}
