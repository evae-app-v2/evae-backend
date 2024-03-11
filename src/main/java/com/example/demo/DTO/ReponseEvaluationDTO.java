package com.example.demo.DTO;

import com.example.demo.models.ReponseQuestion;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReponseEvaluationDTO {

    private int idEvaluation;
    //private String noEtudiant;
    private String commentaire;
/*    private String nom;
    private String prenom;*/
    //private List<ReponseRubriqueDTO> reponseRubriqueDTOs ;
    private List<ReponseQuestionDTO> reponseQuestionDTOs;


}
