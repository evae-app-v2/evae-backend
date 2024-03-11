package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "RUBRIQUE_QUESTION")
public class RubriqueQuestion {
    @SequenceGenerator(name = "RUBRIQUE_QUESTION_id_gen", sequenceName = "AUT_SEQ", allocationSize = 1)
    @EmbeddedId
    private RubriqueQuestionId id;

    @MapsId("idRubrique")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_RUBRIQUE", nullable = false)
    private Rubrique idRubrique;

    @MapsId("idQuestion")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_QUESTION", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Question idQuestion;

    @Column(name = "ORDRE", nullable = false)
    private Long ordre;

}