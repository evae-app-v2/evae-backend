package com.example.demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EvaluationUpdateDTO {

    private String noEvaluation;
    private String codeFormation;
    private String codeEC;
    private String codeUE;
    private String promotion;
    private String debutReponse;
    private String finReponse;
    private String designation;
    private String periode;

    private List<RubriqueEvaluationDTO> rubriques;
}
