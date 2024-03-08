package com.example.demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class PromotionDTO {
	
	private String anneePro;
	private String siglePro;
    private Short nbEtuSouhaite;
    private String etatPreselection;
    private LocalDate dateRentree;
    private String lieuRentree;
    private Instant dateReponseLp;
    private String commentaire;
    private Instant dateReponseLalp;
    private String processusStage;
	private String codeFormation;

    
}
