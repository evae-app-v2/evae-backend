package com.example.demo.DTO;

import com.example.demo.models.Question;
import com.example.demo.models.Rubrique;
import com.example.demo.models.RubriqueQuestionId;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RubriqueQuestionDTO {
    private Integer idRubrique;
    private Integer idQuestion;
    private Long ordre;
}
