package com.example.demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EvaluationQuestionDTO {
    private Integer idRubrique;
    private Integer idRubriqueEva;


    private Integer idQuestion;
    private Long ordre;
    private String Designation;
    private String type;
    List<QuestionDTO> questions;
}
