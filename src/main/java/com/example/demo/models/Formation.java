package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.lang.Character;


@Getter
@Setter
@Entity
@Table(name = "FORMATION")
public class Formation {
    @Id
    @SequenceGenerator(name = "FORMATION_id_gen", sequenceName = "AUT_SEQ", allocationSize = 1)
    @Column(name = "CODE_FORMATION", nullable = false, length = 8)
    private String codeFormation;

    @Column(name = "DIPLOME", nullable = false, length = 3)
    private String diplome;

    @JsonIgnore
    @Column(name = "N0_ANNEE", nullable = false)
    private Integer n0Annee ;

    @Column(name = "NOM_FORMATION", nullable = false, length = 64)
    private String nomFormation;


    @JsonIgnore
    @Column(name = "DOUBLE_DIPLOME", nullable = false)
    private Character doubleDiplome = 'N';

    @Column(name = "DEBUT_ACCREDITATION")
    private LocalDate debutAccreditation;

    @Column(name = "FIN_ACCREDITATION")
    private LocalDate finAccreditation;

    @JsonIgnore
    @OneToMany(mappedBy = "codeFormation")
    private Set<Promotion> promotions = new LinkedHashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "codeFormation")
    private Set<UniteEnseignement> uniteEnseignements = new LinkedHashSet<>();

}