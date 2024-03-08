package com.example.demo.repositories;

import java.util.List;

import com.example.demo.models.Rubrique;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Evaluation;
import com.example.demo.models.RubriqueEvaluation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RubriqueEvaluationRepository extends JpaRepository<RubriqueEvaluation, Integer>{
	
	List<RubriqueEvaluation> findByIdEvaluation(Evaluation evae);

	List<RubriqueEvaluation> findByIdRubrique(Rubrique rubrique);

	@Modifying
	@Query("delete from RubriqueEvaluation re where re.id = :id")
	void deleteRubriqueEvaluation(@Param("id") int id);
}
