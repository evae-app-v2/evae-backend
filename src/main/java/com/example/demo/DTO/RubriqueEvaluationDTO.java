package com.example.demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RubriqueEvaluationDTO {

    private int idEvaluation;
    private int idRubrique;
    private int ordre;
    private List<QuestionEvaluationDTO> questionEvaluations;
}
