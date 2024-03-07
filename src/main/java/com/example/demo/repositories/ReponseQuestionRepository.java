package com.example.demo.repositories;

import com.example.demo.models.ReponseQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReponseQuestionRepository extends JpaRepository<ReponseQuestion, Integer> {
    @Query("SELECT rq FROM ReponseQuestion rq WHERE rq.idQuestionEvaluation.id = (SELECT qe.id FROM QuestionEvaluation qe WHERE ( qe.idQuestion.id = :questionId AND qe.idRubriqueEvaluation.id =:rubriqueevaluation ))")
    ReponseQuestion findByQuestionIdQuestionRubriqueEva(Integer questionId, Integer rubriqueevaluation);

}
