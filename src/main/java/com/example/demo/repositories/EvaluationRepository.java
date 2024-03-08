package com.example.demo.repositories;

import com.example.demo.models.Enseignant;
import com.example.demo.models.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {



	@Query("select max(e.noEvaluation) from Evaluation e")
	short getMaxNoEvaluation();
	@Query("SELECT ev FROM Evaluation ev WHERE ev.noEnseignant = :noEnseignant")
	List<Evaluation> findByNoEnseignant(@Param("noEnseignant") Enseignant noEnseignant);

	Evaluation findByNoEvaluation(Short noEvaluation);

	@Query("SELECT ev FROM Evaluation ev WHERE ev.promotion.id.codeFormation = :codeFormation AND ev.promotion.id.anneeUniversitaire = :anneeUniversitaire AND (ev.etat = 'DIS' OR ev.etat = 'CLO')")
	List<Evaluation> findByCodeFormationAnneeUnivAndEtat(@Param("codeFormation") String codeFormation, @Param("anneeUniversitaire") String anneeUniversitaire);


	Evaluation findById(int idEvaluation);


	@Modifying
	@Query("UPDATE Evaluation ev SET ev.etat = :etat WHERE ev.id = :id")
	void updateEvaluationState(@Param("id") int id, @Param("etat") String etat);

	@Modifying
	@Query("UPDATE Evaluation e SET e.promotion.id.codeFormation = :codeFormation, e.promotion.id.anneeUniversitaire = :anneePro, e.debutReponse = :debutReponse, e.finReponse = :finReponse, e.designation = :designation, e.periode = :periode, e.elementConstitutif.id.codeEc = :codeEc, e.elementConstitutif.id.codeUe = :codeUe WHERE e.noEvaluation = :noEvaluation")
	void updateEvaluation(@Param("codeFormation") String codeFormation, @Param("anneePro") String anneePro, @Param("debutReponse") LocalDate debutReponse, @Param("finReponse") LocalDate finReponse, @Param("designation") String designation, @Param("codeEc") String codeEc, @Param("codeUe") String codeUe, @Param("noEvaluation") short noEvaluation, @Param("periode") String periode );

	@Modifying
	@Query("delete from Evaluation e where e.noEvaluation = :noEvaluation")
	void deleteEvaluation(@Param("noEvaluation") short noEValuation);



}
