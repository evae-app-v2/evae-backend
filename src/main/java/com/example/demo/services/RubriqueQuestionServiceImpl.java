package com.example.demo.services;

import com.example.demo.DTO.*;
import com.example.demo.exception.*;
import com.example.demo.models.*;
import com.example.demo.repositories.QuestionRepository;
import com.example.demo.repositories.RubriqueQuestionRepository;
import com.example.demo.repositories.RubriqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RubriqueQuestionServiceImpl implements RubriqueQuestionService{
    @Autowired
    private RubriqueQuestionRepository rubriqueQuestionRepository;
    @Autowired
    private RubriqueRepository rubriqueRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    QuestionService questionservice;
    //USED LIST ALl
    @Override
    public List<RubriqueQuestionDTOO> getAll() {
        Map<Integer, RubriqueQuestionDTOO> rubriqueMap = new HashMap<>();
        List<RubriqueQuestion> rubquestions = rubriqueQuestionRepository.findAll();
        for (RubriqueQuestion rubriquequestion : rubquestions) {
            RubriqueDTO rub = new RubriqueDTO();
            rub.setId(rubriquequestion.getIdRubrique().getId());
            rub.setType(rubriquequestion.getIdRubrique().getType());
            rub.setDesignation(rubriquequestion.getIdRubrique().getDesignation());
            rub.setOrdre(rubriquequestion.getIdRubrique().getOrdre());
            RubriqueQuestionDTOO rubriqueQuestionDTOO = rubriqueMap.getOrDefault(rub.getId(), new RubriqueQuestionDTOO());
            rubriqueQuestionDTOO.setRUBRIQUE(rub);
            if (rubriquequestion.getIdQuestion() != null) {
                QuestionDTO q = new QuestionDTO();
                q.setId(rubriquequestion.getIdQuestion().getId());
                q.setOrdre(rubriquequestion.getOrdre());
                q.setType(rubriquequestion.getIdQuestion().getType());
                q.setIntitule(rubriquequestion.getIdQuestion().getIntitule());
                QualificatifDTO qua = new QualificatifDTO();
                qua.setId(rubriquequestion.getIdQuestion().getIdQualificatif().getId());
                qua.setMaximal(rubriquequestion.getIdQuestion().getIdQualificatif().getMaximal());
                qua.setMinimal(rubriquequestion.getIdQuestion().getIdQualificatif().getMinimal());
                q.setIdQualificatif(qua);
                rubriqueQuestionDTOO.addQuestion(q);
            }
            rubriqueMap.put(rub.getId(), rubriqueQuestionDTOO);
        }
        List<RubriqueQuestionDTOO> rubriquesWithoutQuestions = getRubriquesWithoutQuestions();
        for (RubriqueQuestionDTOO rubriqueWithoutQuestions : rubriquesWithoutQuestions) {
            if (!rubriqueMap.containsKey(rubriqueWithoutQuestions.getRUBRIQUE().getId())) {
                rubriqueMap.put(rubriqueWithoutQuestions.getRUBRIQUE().getId(), rubriqueWithoutQuestions);
            }
        }
        return new ArrayList<>(rubriqueMap.values());
    }

    public List<RubriqueQuestionDTOO> getRubriquesWithoutQuestions() {
        List<Rubrique> rubriquesWithoutQuestions = rubriqueRepository.findRubriquesWithoutQuestions();
        List<RubriqueQuestionDTOO> result = new ArrayList<>();
        for (Rubrique rubriqueWithoutQuestions : rubriquesWithoutQuestions) {
            RubriqueDTO rub = new RubriqueDTO();
            rub.setId(rubriqueWithoutQuestions.getId());
            rub.setType(rubriqueWithoutQuestions.getType());
            rub.setDesignation(rubriqueWithoutQuestions.getDesignation());
            rub.setOrdre(rubriqueWithoutQuestions.getOrdre());
            RubriqueQuestionDTOO rubriqueQuestionDTOO = new RubriqueQuestionDTOO();
            rubriqueQuestionDTOO.setRUBRIQUE(rub);
            rubriqueQuestionDTOO.setQuestions(new ArrayList<>());
            result.add(rubriqueQuestionDTOO);
        }
        return result;
    }

    // USED DELETE QUESTION
    @Override
    public String deleteRubriqueQuestionByIds(Integer rubriqueId, Integer questionId) throws RubriqueQuestionNotFoundException {
        Optional<RubriqueQuestion> rubriqueQuestionOptional = rubriqueQuestionRepository.findById(new RubriqueQuestionId(rubriqueId, questionId));
        if (rubriqueQuestionOptional.isPresent()) {
            rubriqueQuestionRepository.deleteById(new RubriqueQuestionId(rubriqueId, questionId));
            return "Deletion successful.";
        } else {
            throw new RubriqueQuestionNotFoundException("RubriqueQuestion with rubriqueId " + rubriqueId + " and questionId " + questionId + " not found.");
        }
    }

    // USED DELETE RUBRIQUE
    @Override
    public String deleteRubriqueQuestionsByRubriqueId(Integer rubriqueId) throws RubriqueNotFoundException {
        Optional<Rubrique> rubriqueOptional = rubriqueRepository.findById(rubriqueId);
        if (rubriqueOptional.isPresent()) {
            rubriqueQuestionRepository.deleteByRubriqueId(rubriqueId);
            rubriqueRepository.delete(rubriqueOptional.get());
            return "Deletion successful.";
        } else {
            throw new RubriqueNotFoundException("Rubrique with ID " + rubriqueId + " not found.");
        }
    }

    // USED LIST QUESTION NOT IN RUBRIQUE
    @Override
    public Set<Question> getQuestionsNotInRubrique(Rubrique rubrique) {
        Set<Question> questionsInRubrique = getQuestionsByRubrique(rubrique);
        Set<Question> allQuestions = new HashSet<>(questionRepository.findAll());
        allQuestions.removeAll(questionsInRubrique);
        return allQuestions;
    }

    public Set<Question> getQuestionsByRubrique(Rubrique rubrique) {
        return rubriqueQuestionRepository.findQuestionsByRubrique(rubrique);
    }

    //USED CREATE MULTIPLE
    @Override
    public List<RubriqueQuestion> createRubriqueQuestionsForRubrique(Integer idRubrique, List<Integer> idQuestions) {
        Rubrique rubrique = rubriqueRepository.findById(idRubrique)
                .orElseThrow(() -> new NotFoundEntityException("rubrique"));
        Long maxOrdre = rubriqueQuestionRepository.findMaxOrdreByIdRubrique(idRubrique);
        List<RubriqueQuestion> rubriqueQuestions = new ArrayList<>();
        for (Integer idQuestion : idQuestions) {
            RubriqueQuestion rubriqueQuestion = new RubriqueQuestion();
            RubriqueQuestionId rubriqueQuestionId = new RubriqueQuestionId();
            rubriqueQuestionId.setIdRubrique(idRubrique);
            rubriqueQuestionId.setIdQuestion(idQuestion);
            rubriqueQuestion.setId(rubriqueQuestionId);
            rubriqueQuestion.setIdRubrique(rubrique);
            Question question = questionRepository.findById(idQuestion)
                    .orElseThrow(() -> new NotFoundEntityException("question"));
            rubriqueQuestion.setIdQuestion(question);
            rubriqueQuestion.setOrdre(++maxOrdre);
            rubriqueQuestions.add(rubriqueQuestion);
        }
        return rubriqueQuestionRepository.saveAll(rubriqueQuestions);
    }

    //CREATE ONE
    @Override
    public RubriqueQuestion createRubriqueQuestion(RubriqueQuestionDTO rubriqueQuestionDTO) {
        RubriqueQuestion rubriqueQuestion = new RubriqueQuestion();
        RubriqueQuestionId rubriqueQuestionId = new RubriqueQuestionId();
        rubriqueQuestionId.setIdRubrique(rubriqueQuestionDTO.getIdRubrique());
        rubriqueQuestionId.setIdQuestion(rubriqueQuestionDTO.getIdQuestion());
        rubriqueQuestion.setId(rubriqueQuestionId);
        Rubrique rubrique = rubriqueRepository.findById(rubriqueQuestionDTO.getIdRubrique())
                .orElseThrow(() -> new NotFoundEntityException("rubrique"));
        rubriqueQuestion.setIdRubrique(rubrique);
        Question question = questionRepository.findById(rubriqueQuestionDTO.getIdQuestion())
                .orElseThrow(() -> new NotFoundEntityException("question"));
        rubriqueQuestion.setIdQuestion(question);
        Long max = rubriqueQuestionRepository.findMaxOrdreByIdRubrique(rubriqueQuestionDTO.getIdRubrique());
        rubriqueQuestion.setOrdre(max+1);
        return rubriqueQuestionRepository.save(rubriqueQuestion);
    }

    // USED ORDER QUESTIONS
    @Override
    public List<RubriqueQuestion> updateOrdreRubriqueQuestions(List<RubriqueQuestionDTO> rubriqueQuestionDTOs) {
        List<RubriqueQuestion> updatedRubriqueQuestions = new ArrayList<>();
        for (RubriqueQuestionDTO dto : rubriqueQuestionDTOs) {
            RubriqueQuestionId rubriqueQuestionId = new RubriqueQuestionId(dto.getIdRubrique(), dto.getIdQuestion());
            Optional<RubriqueQuestion> optionalRubriqueQuestion = rubriqueQuestionRepository.findById(rubriqueQuestionId);
            if (optionalRubriqueQuestion.isPresent()) {
                RubriqueQuestion existingRubriqueQuestion = optionalRubriqueQuestion.get();
                existingRubriqueQuestion.setOrdre(dto.getOrdre());
                updatedRubriqueQuestions.add(rubriqueQuestionRepository.save(existingRubriqueQuestion));
            }
        }
        return updatedRubriqueQuestions;
    }
}
