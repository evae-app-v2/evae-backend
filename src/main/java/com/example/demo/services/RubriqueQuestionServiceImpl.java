package com.example.demo.services;

import com.example.demo.DTO.*;
import com.example.demo.exception.*;
import com.example.demo.models.*;
import com.example.demo.repositories.QuestionRepository;
import com.example.demo.repositories.RubriqueEvaluationRepository;
import com.example.demo.repositories.RubriqueQuestionRepository;
import com.example.demo.repositories.RubriqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
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
    private RubriqueEvaluationRepository rubriqueEvaluationRepository ;

    @Autowired
    QuestionService questionservice;

    //USED List
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
            // Initialiser la liste de questions comme une liste vide
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


    // USED LIST QUESTION
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

    @Override
    public List<RubriqueQuestion> createRubriqueQuestionsForRubrique(Integer idRubrique, List<Integer> idQuestions) {
        // Récupérer la rubrique associée à l'idRubrique
        Rubrique rubrique = rubriqueRepository.findById(idRubrique)
                .orElseThrow(() -> new NotFoundEntityException("rubrique"));

        // Récupérer le dernier ordre existant pour cette rubrique
        Long maxOrdre = rubriqueQuestionRepository.findMaxOrdreByIdRubrique(idRubrique);

        // Créer la liste pour stocker les nouvelles instances de RubriqueQuestion
        List<RubriqueQuestion> rubriqueQuestions = new ArrayList<>();

        // Créer les instances de RubriqueQuestion pour chaque idQuestion
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

            // Incrémenter l'ordre pour chaque nouvelle instance
            rubriqueQuestion.setOrdre(++maxOrdre);

            rubriqueQuestions.add(rubriqueQuestion);
        }

        // Enregistrer toutes les nouvelles instances de RubriqueQuestion
        return rubriqueQuestionRepository.saveAll(rubriqueQuestions);
    }


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
            } else {
                // Gérer le cas où l'enregistrement n'a pas été trouvé
                // Vous pouvez choisir de lever une exception, de créer un nouvel enregistrement, ou d'ignorer cet élément selon votre logique métier.
            }
        }

        return updatedRubriqueQuestions;
    }



    // -------------------------- NOT USED ---------------------------------------
    public List<RubriqueQuestionDTO> getAllRubriqueQuestion() {
        List<RubriqueQuestion> rubriqueQuestions = rubriqueQuestionRepository.findAll();
        List<RubriqueQuestionDTO> rubriqueQuestionDTOs = new ArrayList<>();

        for (RubriqueQuestion rubriqueQuestion : rubriqueQuestions) {
            RubriqueQuestionDTO dto = new RubriqueQuestionDTO();
            dto.setIdRubrique(rubriqueQuestion.getIdRubrique().getId());
            dto.setIdQuestion(rubriqueQuestion.getIdQuestion().getId());
            dto.setOrdre(rubriqueQuestion.getOrdre());

            // Créez le QualificatifDTO associé
            Qualificatif qualificatif = rubriqueQuestion.getIdQuestion().getIdQualificatif();
            QualificatifDTO qualificatifDTO = new QualificatifDTO();
            qualificatifDTO.setId(qualificatif.getId());
            qualificatifDTO.setMaximal(qualificatif.getMaximal());
            qualificatifDTO.setMinimal(qualificatif.getMinimal());

            // Créez le QuestionDTO
            Question question = rubriqueQuestion.getIdQuestion();
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setId(question.getId());
            questionDTO.setType(question.getType());
            questionDTO.setIntitule(question.getIntitule());
            questionDTO.setIdQualificatif(qualificatifDTO);

            //dto.setQuestionDTO(questionDTO);

            Rubrique rubrique = rubriqueQuestion.getIdRubrique();
            RubriqueDTO rubriqueDTO = new RubriqueDTO();
            rubriqueDTO.setId(rubrique.getId());
            rubriqueDTO.setType(rubrique.getType());
            rubriqueDTO.setDesignation(rubrique.getDesignation());
            rubriqueDTO.setOrdre(rubrique.getOrdre());

            //dto.setRubriqueDTO(rubriqueDTO);

            rubriqueQuestionDTOs.add(dto);
        }

        return rubriqueQuestionDTOs;
    }


    //aprem
    public Map<Integer, List<RubriqueQuestionDTO>> getQuestionsGroupedByRubrique() {
        List<RubriqueQuestion> rubriqueQuestions = rubriqueQuestionRepository.findAll();
        Map<Integer, List<RubriqueQuestionDTO>> groupedQuestions = new HashMap<>();

        for (RubriqueQuestion rubriqueQuestion : rubriqueQuestions) {
            RubriqueQuestionDTO dto = new RubriqueQuestionDTO();
            dto.setIdRubrique(rubriqueQuestion.getIdRubrique().getId());
            dto.setIdQuestion(rubriqueQuestion.getIdQuestion().getId());
            dto.setOrdre(rubriqueQuestion.getOrdre());

            // Create the QualificatifDTO associated
            Qualificatif qualificatif = rubriqueQuestion.getIdQuestion().getIdQualificatif();
            QualificatifDTO qualificatifDTO = new QualificatifDTO();
            qualificatifDTO.setId(qualificatif.getId());
            qualificatifDTO.setMaximal(qualificatif.getMaximal());
            qualificatifDTO.setMinimal(qualificatif.getMinimal());

            // Create the QuestionDTO
            Question question = rubriqueQuestion.getIdQuestion();
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setId(question.getId());
            questionDTO.setType(question.getType());
            questionDTO.setIntitule(question.getIntitule());
            questionDTO.setIdQualificatif(qualificatifDTO);

            //dto.setQuestionDTO(questionDTO);

            Rubrique rubrique = rubriqueQuestion.getIdRubrique();
            RubriqueDTO rubriqueDTO = new RubriqueDTO();
            rubriqueDTO.setId(rubrique.getId());
            rubriqueDTO.setType(rubrique.getType());
            rubriqueDTO.setDesignation(rubrique.getDesignation());
            rubriqueDTO.setOrdre(rubrique.getOrdre());

            //dto.setRubriqueDTO(rubriqueDTO);

            // Group by idRubrique
            if (groupedQuestions.containsKey(rubrique.getId())) {
                groupedQuestions.get(rubrique.getId()).add(dto);
            } else {
                List<RubriqueQuestionDTO> list = new ArrayList<>();
                list.add(dto);
                groupedQuestions.put(rubrique.getId(), list);
            }
        }

        return groupedQuestions;
    }




    //aprem2
    public Map<Integer, List<RubriqueQuestionDTO>> getQuestionsGroupedByRubriqueOrderedByOrdre() {
        List<RubriqueQuestion> rubriqueQuestions = rubriqueQuestionRepository.findAll();
        Map<Integer, List<RubriqueQuestionDTO>> groupedQuestions = new HashMap<>();

        // Sort rubriqueQuestions by ordre
        rubriqueQuestions.sort(Comparator.comparing(RubriqueQuestion::getOrdre));

        for (RubriqueQuestion rubriqueQuestion : rubriqueQuestions) {
            RubriqueQuestionDTO dto = new RubriqueQuestionDTO();
            dto.setIdRubrique(rubriqueQuestion.getIdRubrique().getId());
            dto.setIdQuestion(rubriqueQuestion.getIdQuestion().getId());
            dto.setOrdre(rubriqueQuestion.getOrdre());

            // Create the QualificatifDTO associated
            Qualificatif qualificatif = rubriqueQuestion.getIdQuestion().getIdQualificatif();
            QualificatifDTO qualificatifDTO = new QualificatifDTO();
            qualificatifDTO.setId(qualificatif.getId());
            qualificatifDTO.setMaximal(qualificatif.getMaximal());
            qualificatifDTO.setMinimal(qualificatif.getMinimal());

            // Create the QuestionDTO
            Question question = rubriqueQuestion.getIdQuestion();
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setId(question.getId());
            questionDTO.setType(question.getType());
            questionDTO.setIntitule(question.getIntitule());
            questionDTO.setIdQualificatif(qualificatifDTO);

            //dto.setQuestionDTO(questionDTO);

            Rubrique rubrique = rubriqueQuestion.getIdRubrique();
            RubriqueDTO rubriqueDTO = new RubriqueDTO();
            rubriqueDTO.setId(rubrique.getId());
            rubriqueDTO.setType(rubrique.getType());
            rubriqueDTO.setDesignation(rubrique.getDesignation());
            rubriqueDTO.setOrdre(rubrique.getOrdre());

            //dto.setRubriqueDTO(rubriqueDTO);

            // Group by idRubrique
            if (groupedQuestions.containsKey(rubrique.getId())) {
                groupedQuestions.get(rubrique.getId()).add(dto);
            } else {
                List<RubriqueQuestionDTO> list = new ArrayList<>();
                list.add(dto);
                groupedQuestions.put(rubrique.getId(), list);
            }
        }

        return groupedQuestions;
    }



    @Override
    @Transactional
    public void swapOrdre(Integer idRubrique1, Integer idQuestion1, Integer idRubrique2, Integer idQuestion2) throws RubriqueQuestionNotFoundException {
        RubriqueQuestion rubriqueQuestion1 = rubriqueQuestionRepository.findById(new RubriqueQuestionId(idRubrique1, idQuestion1))
                .orElseThrow(() -> new RubriqueQuestionNotFoundException("RubriqueQuestion with idRubrique " + idRubrique1 + " and idQuestion " + idQuestion1 + " not found"));
        RubriqueQuestion rubriqueQuestion2 = rubriqueQuestionRepository.findById(new RubriqueQuestionId(idRubrique2, idQuestion2))
                .orElseThrow(() -> new RubriqueQuestionNotFoundException("RubriqueQuestion with idRubrique " + idRubrique2 + " and idQuestion " + idQuestion2 + " not found"));

        Long ordre1 = rubriqueQuestion1.getOrdre();
        Long ordre2 = rubriqueQuestion2.getOrdre();

        rubriqueQuestion1.setOrdre(ordre2);
        rubriqueQuestion2.setOrdre(ordre1);

        rubriqueQuestionRepository.save(rubriqueQuestion1);
        rubriqueQuestionRepository.save(rubriqueQuestion2);
    }







}
