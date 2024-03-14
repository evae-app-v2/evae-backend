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


	@Query("select re from RubriqueEvaluation re where re.idEvaluation.id = :idEvaluation and re.idRubrique.id = :idRubrique")
	RubriqueEvaluation getRubriqueEvaluationByEvaluationAndRubrique(@Param("idEvaluation") int idEvaluation, @Param("idRubrique") int idRubrique);





	@Query("select re from RubriqueEvaluation re where re.idEvaluation.noEvaluation = :noEvaluation ")
	List<RubriqueEvaluation> getRubriqueEvaluationByEvaluation(@Param("noEvaluation") short noEvaluation);
}
