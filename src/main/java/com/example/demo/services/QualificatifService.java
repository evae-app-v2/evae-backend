package com.example.demo.services;


import com.example.demo.exception.AlreadyUsedException;
import com.example.demo.exception.DuplicateEntityException;
import com.example.demo.exception.NotFoundEntityException;
import com.example.demo.models.Qualificatif;
import com.example.demo.repositories.QualificatifRepository;
import com.example.demo.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QualificatifService {

    @Autowired
    private QualificatifRepository qualificatifRepository;

    public Qualificatif saveQualificatif(Qualificatif qualificatif) {
        String maximal = qualificatif.getMaximal();
        String minimal = qualificatif.getMinimal();

        if (qualificatifRepository.existsByMaximalOrMinimal(maximal, minimal)) {
            throw new DuplicateEntityException("qualificatif");
        }

        return qualificatifRepository.save(qualificatif);
    }

    public List<Qualificatif> getQualificatifs() {
        return qualificatifRepository.findAll();
    }

    public Qualificatif getQualificatifById(int id) {
        return qualificatifRepository.findById(id).orElseThrow(() -> new NotFoundEntityException("qualificatif"));
    }

    public String deleteQualificatif(int id) {
        Qualificatif qualificatif = qualificatifRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("qualificatif"));

        boolean isUsedInQuestion = qualificatif.getQuestions().stream().anyMatch(question -> question.getIdQualificatif().equals(qualificatif));
        if (isUsedInQuestion) {
            throw new AlreadyUsedException("qualificatif");
        }

        qualificatifRepository.deleteById(id);
        return "Qualificatif supprimé ||" + id;
    }

    public Qualificatif updateQualificatif(Qualificatif qualificatif) {
        Qualificatif existingQualificatif = qualificatifRepository.findById(qualificatif.getId())
                .orElseThrow(() -> new NotFoundEntityException("Qualificatif"));

        Qualificatif qualificatifTmin = qualificatifRepository.findByMinmal(qualificatif.getMinimal());
        Qualificatif qualificatifTmax = qualificatifRepository.findByMaxmal(qualificatif.getMinimal());

        if (qualificatifTmin != null ) { if( qualificatifTmin.getId() != qualificatif.getId()) throw new AlreadyUsedException(" le nom choisie pour le qualitatif");};
        if (qualificatifTmax != null ) { if( qualificatifTmax.getId() != qualificatif.getId()) throw new AlreadyUsedException(" le nom choisie pour le qualitatif ");};

        boolean isUsedInQuestion = existingQualificatif.getQuestions().stream().anyMatch(question -> question.getIdQualificatif().equals(existingQualificatif));
        if (isUsedInQuestion) {
            throw new AlreadyUsedException("qualificatif");
        }
        existingQualificatif.setMaximal(qualificatif.getMaximal());
        existingQualificatif.setMinimal(qualificatif.getMinimal());
        return qualificatifRepository.save(existingQualificatif);
    }
    public boolean qualificatifIsUsedInQuestion(int id) {
        Qualificatif qualificatif = qualificatifRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("qualificatif"));

        return qualificatif.getQuestions().stream().anyMatch(question -> question.getIdQualificatif().equals(qualificatif));
    }
}