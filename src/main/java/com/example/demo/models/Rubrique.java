package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "RUBRIQUE")
@JsonIgnoreProperties({"rubriqueEvaluations", "rubriqueQuestions"})
public class Rubrique {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RUBRIQUE_id_gen")
    @SequenceGenerator(name = "RUBRIQUE_id_gen", sequenceName = "RUB_SEQ", allocationSize = 1)
    @Column(name = "ID_RUBRIQUE", nullable = false)
    private Integer id;

    @Column(name = "\"TYPE\"", nullable = false, length = 10)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NO_ENSEIGNANT")
    private Enseignant noEnseignant;

    @Column(name = "DESIGNATION", nullable = false, length = 32)
    private String designation;

    @Column(name = "ORDRE")
    private Long ordre;

    @OneToMany(mappedBy = "idRubrique")
    private Set<RubriqueEvaluation> rubriqueEvaluations = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idRubrique")
    private Set<RubriqueQuestion> rubriqueQuestions = new LinkedHashSet<>();

}