package com.example.demo.services;

import com.example.demo.DTO.RubriqueQuestionDTO;
import com.example.demo.DTO.RubriqueQuestionDTOO;
import com.example.demo.exception.RubriqueNotFoundException;
import com.example.demo.exception.RubriqueQuestionNotFoundException;
import com.example.demo.models.Question;
import com.example.demo.models.Rubrique;
import com.example.demo.models.RubriqueQuestion;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RubriqueQuestionService {
    List<RubriqueQuestionDTOO> getAll();
    public String deleteRubriqueQuestionByIds(Integer rubriqueId, Integer questionId) throws RubriqueQuestionNotFoundException;
    public String deleteRubriqueQuestionsByRubriqueId(Integer rubriqueId) throws RubriqueNotFoundException;
    public RubriqueQuestion createRubriqueQuestion(RubriqueQuestionDTO rubriqueQuestionDTO);

    List<RubriqueQuestion> createRubriqueQuestionsForRubrique(Integer idRubrique, List<Integer> idQuestions);

    Set<Question> getQuestionsNotInRubrique(Rubrique rubrique);

    public Set<Question> getQuestionsByRubrique(Rubrique rubrique);

    List<RubriqueQuestion> updateOrdreRubriqueQuestions(List<RubriqueQuestionDTO> rubriqueQuestionDTOs);

}
