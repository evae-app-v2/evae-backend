package com.example.demo.repositories;

import com.example.demo.models.Question;
import com.example.demo.models.Rubrique;
import com.example.demo.models.RubriqueQuestion;
import com.example.demo.models.RubriqueQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;


public interface RubriqueQuestionRepository extends JpaRepository<RubriqueQuestion, RubriqueQuestionId>{

	@Query("SELECT rq FROM RubriqueQuestion rq WHERE rq.idQuestion = :questionId")
	List<RubriqueQuestion> findByQuestionId(@Param("questionId") Question questionId);
	@Query("select rq from RubriqueQuestion rq where rq.idQuestion = :question")
	RubriqueQuestion findByQuestion(@Param("question") Question question);

	@Query("SELECT rq.idQuestion FROM RubriqueQuestion rq WHERE rq.idRubrique = :rubrique")
	Set<Question> findQuestionsByRubrique(@Param("rubrique") Rubrique rubrique);

	@Query("select rq from RubriqueQuestion rq where rq.id.idRubrique = :idRubrique")
	List<RubriqueQuestion> findByIdRubrique(@Param("idRubrique") int idRubrique);

	@Query("SELECT COALESCE(MAX(rq.ordre), 0) " +
			"FROM RubriqueQuestion rq " +
			"WHERE rq.id.idRubrique = :rubriqueId")
	Long findMaxOrdreByIdRubrique(@Param("rubriqueId") Integer rubriqueId);
	boolean existsById_IdRubriqueAndId_IdQuestion(
			Integer rubriqueId,
			Integer questionId
	);

	@Modifying
	@Transactional
	@Query("DELETE FROM RubriqueQuestion rq WHERE rq.id.idRubrique = :rubriqueId")
	void deleteByRubriqueId(@Param("rubriqueId") Integer rubriqueId);

}
