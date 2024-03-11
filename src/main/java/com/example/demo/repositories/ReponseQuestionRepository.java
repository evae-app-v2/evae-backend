package com.example.demo.repositories;

import com.example.demo.models.ReponseQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReponseQuestionRepository extends JpaRepository<ReponseQuestion, Integer> {

                @Query("SELECT rq FROM ReponseQuestion rq WHERE rq.idQuestionEvaluation.id = (SELECT qe.id FROM QuestionEvaluation qe WHERE qe.idQuestion.id = :questionId AND qe.idRubriqueEvaluation.id = :rubriqueevaluation AND qe.idRubriqueEvaluation.idEvaluation.id = :idevaluation)")
                    ReponseQuestion findByQuestionIdQuestionRubriqueEva(@Param("questionId") Integer questionId, @Param("rubriqueevaluation") Integer rubriqueevaluation, @Param("idevaluation") Integer idevaluation);
}
