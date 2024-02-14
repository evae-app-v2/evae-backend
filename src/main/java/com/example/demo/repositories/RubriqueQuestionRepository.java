package com.example.demo.repositories;

import com.example.demo.models.Question;
import com.example.demo.models.RubriqueQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RubriqueQuestionRepository extends JpaRepository<RubriqueQuestion, Integer>{
	
	@Query("select rq from RubriqueQuestion rq where rq.idQuestion = :question")
	RubriqueQuestion findByQuestion(@Param("question") Question question);

}