package com.example.demo.services;

import com.example.demo.DTO.RubriqueDTO;
import com.example.demo.exception.DuplicateEntityException;
import com.example.demo.exception.NotFoundEntityException;
import com.example.demo.exception.UsedEntityException;
import com.example.demo.models.Rubrique;
import com.example.demo.repositories.RubriqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RubriqueServiceImpl implements RubriqueService{
    @Autowired
    private RubriqueRepository rubriqueRepository;
    @Override
    public List<RubriqueDTO> getAllRubriques() {
        List<Rubrique> rubriques = rubriqueRepository.findAll();
        List<RubriqueDTO> RubriqueDTOs = new ArrayList<>();
        for (Rubrique rubrique : rubriques) {
            RubriqueDTO RubriqueDTO = new RubriqueDTO();
            RubriqueDTO.setDesignation(rubrique.getDesignation());
            RubriqueDTO.setOrdre(rubrique.getOrdre());
            RubriqueDTO.setType(rubrique.getType());
            RubriqueDTO.setId(rubrique.getId());
            RubriqueDTOs.add(RubriqueDTO);
        }
        return RubriqueDTOs;
    }
    @Override
    public Rubrique getRubriqueById(Integer id) {
        return rubriqueRepository.findById(id).orElse(null);
    }

    @Override
    public Rubrique createRubrique(Rubrique rubrique) {
        if (rubriqueRepository.existsByDesignation(rubrique.getDesignation())) {
            throw new DuplicateEntityException("rubrique");
        }
        Long maxOrdre = rubriqueRepository.findMaxOrdre();
        rubrique.setOrdre((maxOrdre != null) ? maxOrdre + 1 : 1);
        return rubriqueRepository.save(rubrique);
    }

    @Override
    public Rubrique updateRubrique(Integer id, RubriqueDTO rubriqueDTO) {
        if (rubriqueRepository.existsById(id)) {
            Rubrique existingRubrique = rubriqueRepository.findById(id).orElse(null);
            if (isRubriqueUsedInEvaluation(existingRubrique)) {
                throw new UsedEntityException("rubrique");
            }

            if (rubriqueRepository.existsByDesignationAndIdNot(rubriqueDTO.getDesignation(), id)) {
                throw new DuplicateEntityException("rubrique");
            }
            existingRubrique.setDesignation(rubriqueDTO.getDesignation());
            return rubriqueRepository.save(existingRubrique);

        } else {
            throw new NotFoundEntityException("rubrique");
        }
    }

    @Override
    public void deleteRubrique(Integer id) {
        if (rubriqueRepository.existsById(id)) {
            Rubrique existingRubrique = rubriqueRepository.findById(id).orElse(null);
            if (isRubriqueUsedInEvaluation(existingRubrique)) {
                throw new UsedEntityException("rubrique");
            }
            rubriqueRepository.deleteById(id);
        } else {
            throw new NotFoundEntityException("rubrique");
        }
    }

    @Override
    public boolean isRubriqueUsedInEvaluation(Rubrique rubrique) {
        if (rubrique == null) {
            return false;
        }
        return rubrique.getRubriqueEvaluations() != null && !rubrique.getRubriqueEvaluations().isEmpty() ;
//                || rubrique.getRubriqueQuestions() != null && !rubrique.getRubriqueQuestions().isEmpty();
    }

    @Override
    public Optional<Rubrique> findRubriqueByDesignation(String designation) {
        return rubriqueRepository.findByDesignation(designation);
    }

    @Override
    public void updateOrdre(List<Rubrique> updatedRubriquesData) {
        rubriqueRepository.saveAll(updatedRubriquesData);
    }
}
