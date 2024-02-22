package com.example.demo.services;


import com.example.demo.exception.AlreadyUsedException;
import com.example.demo.exception.DuplicateEntityException;
import com.example.demo.exception.NotFoundEntityException;
import com.example.demo.exception.QualificatifAlreadyExistsException;
import com.example.demo.models.Qualificatif;
import com.example.demo.models.Question;
import com.example.demo.repositories.QualificatifRepository;
import com.example.demo.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QualificatifService {

    @Autowired
    private QualificatifRepository qualificatifRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public Qualificatif saveQualificatif(Qualificatif qualificatif) {
        String maximal = qualificatif.getMaximal();
        String minimal = qualificatif.getMinimal();

        if (qualificatifRepository.existsByMaximalOrMinimal(maximal, minimal)) {
            throw new DuplicateEntityException("qualificatif");
        }

        return qualificatifRepository.save(qualificatif);
    }

    public List<Qualificatif> getQualificatifs(){
        return qualificatifRepository.findAll();
    }

    /*
    public Qualificatif getQualificatifById(int id){
        return qualificatifRepository.findById(id).orElse(null);
    }  */

    public Qualificatif getQualificatifById(int id) {
        return qualificatifRepository.findById(id).orElseThrow(() -> new NotFoundEntityException("qualificatif"));
    }


    public String deleteQualificatif(int id){
        Qualificatif qualificatif = qualificatifRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("qualificatif") );

        // Check if any question is using this qualificatif
        boolean isUsedInQuestion = qualificatif.getQuestions().stream().anyMatch(question -> question.getIdQualificatif().equals(qualificatif));
        if (isUsedInQuestion) {
            throw new AlreadyUsedException("qualificatif");
        }

        qualificatifRepository.deleteById(id);
        return "Qualificatif supprimé ||"+id;
    }


    /*
    public String deleteQualificatif(int id){
        Qualificatif existingQualificatif = qualificatifRepository.findById(id).orElseThrow(() -> new QualificatifNotFoundException(id));

        // Check if the Qualificatif is used in any Question
        List<Question> questionsWithQualificatif = questionRepository.findByQualificatif(existingQualificatif);
        if (!questionsWithQualificatif.isEmpty()) {
            throw new QualificatifInUseException(id);
        }


        qualificatifRepository.delete(existingQualificatif);

        return "Qualificatif deleted || " + id;
    }
    */

    public Qualificatif updateQualificatif(Qualificatif qualificatif){
        Qualificatif existingQualificatif = qualificatifRepository.findById(qualificatif.getId())
                .orElseThrow(() -> new NotFoundEntityException("Qualificatif"));

        Qualificatif qualificatifTmin = qualificatifRepository.findByMinmal(qualificatif.getMinimal());
        Qualificatif qualificatifTmax = qualificatifRepository.findByMaxmal(qualificatif.getMinimal());

        if(qualificatifTmin !=null) throw new AlreadyUsedException("qualificatif");
        if(qualificatifTmax !=null) throw new AlreadyUsedException("qualificatif");

        boolean isUsedInQuestion = existingQualificatif.getQuestions().stream().anyMatch(question -> question.getIdQualificatif().equals(existingQualificatif));
        if (isUsedInQuestion) {
            throw new AlreadyUsedException("qualificatif");
        }

        existingQualificatif.setMaximal(qualificatif.getMaximal());
        existingQualificatif.setMinimal(qualificatif.getMinimal());
        return qualificatifRepository.save(existingQualificatif);
    }


    /*
    public Qualificatif updateQualificatif(Qualificatif qualificatif){
        Qualificatif existingQualificatif = qualificatifRepository.findById(qualificatif.getId())
                .orElseThrow(() -> new NotFoundEntityException("Qualificatif"));

        boolean isUsedInQuestion = existingQualificatif.getQuestions().stream().anyMatch(question -> question.getIdQualificatif().equals(existingQualificatif));

        if (isUsedInQuestion) {
            throw new AlreadyUsedException("qualificatif");
        }

        // Check if the new maximal value is unique (except itself)
        if (qualificatif.getMaximal() != null && !qualificatif.getMaximal().equals(existingQualificatif.getMaximal())) {
            if (qualificatifRepository.existsByMaximalAndIdNot(qualificatif.getMaximal(), qualificatif.getId())) {
                throw new QualificatifAlreadyExistsException(qualificatif.getMaximal(), null);
            }
        }

        // Check if the new minimal value is unique (except itself)
        if (qualificatif.getMinimal() != null && !qualificatif.getMinimal().equals(existingQualificatif.getMinimal())) {
            if (qualificatifRepository.existsByMinimalAndIdNot(qualificatif.getMinimal(), qualificatif.getId())) {
                throw new QualificatifAlreadyExistsException(null, qualificatif.getMinimal());
            }
        }

        // Update the qualificatif values
        existingQualificatif.setMaximal(qualificatif.getMaximal());
        existingQualificatif.setMinimal(qualificatif.getMinimal());
        return qualificatifRepository.save(existingQualificatif);
    } */

}