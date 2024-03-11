package com.example.demo.repositories;

import com.example.demo.models.Question;
import com.example.demo.models.QuestionEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionEvaluationRepository extends JpaRepository<QuestionEvaluation,Integer> {
	
	@Query("select qe from QuestionEvaluation qe where qe.idQuestion = :question")
	QuestionEvaluation findByQuestion(@Param("question") Question question);

	@Query("select qe from QuestionEvaluation qe where qe.idQuestion = :question")
	List<QuestionEvaluation> findByQuestionList(@Param("question") Question question);

	@Modifying
	@Query("delete from QuestionEvaluation qe where qe.id = :id")
	void deleteQuestionEvaluation(@Param("id") int id);

	@Query("select qe from QuestionEvaluation qe where qe.idRubriqueEvaluation.id = :idRubriqueEvaluation")
	List<QuestionEvaluation> getQuestionEvaluationRE(@Param("idRubriqueEvaluation") Integer idRubriqueEvaluation);

}
