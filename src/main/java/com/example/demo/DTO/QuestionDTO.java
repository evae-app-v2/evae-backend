package com.example.demo.DTO;


import com.example.demo.models.Qualificatif;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class QuestionDTO {
	
    private Integer id;

    private String type;

    private QualificatifDTO idQualificatif;

    private String intitule;

    private Long ordre;

    private Long positionnements;



}
