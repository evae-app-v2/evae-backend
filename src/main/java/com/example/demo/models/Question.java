package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "QUESTION")
@JsonIgnoreProperties({"rubriqueQuestions"})
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QUESTION_id_gen")
    @SequenceGenerator(name = "QUESTION_id_gen", sequenceName = "QUE_SEQ", allocationSize = 1)
    @Column(name = "ID_QUESTION", nullable = false)
    private Integer id;

    @Column(name = "\"TYPE\"", nullable = false, length = 10)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NO_ENSEIGNANT")
    @JsonIgnore
    private Enseignant noEnseignant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_QUALIFICATIF", nullable = false)
    @JsonIgnore
    private Qualificatif idQualificatif;

    @Column(name = "INTITULE", nullable = false, length = 64)
    private String intitule;

    @OneToMany(mappedBy = "idQuestion")
    @JsonBackReference
    private Set<QuestionEvaluation> questionEvaluations = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idQuestion")
    @JsonBackReference
    private Set<RubriqueQuestion> rubriqueQuestions = new LinkedHashSet<>();

}