package com.example.demo.repositories;

import com.example.demo.models.Enseignant;
import com.example.demo.models.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

	@Query("SELECT ev FROM Evaluation ev WHERE ev.noEnseignant = :noEnseignant")
	List<Evaluation> findByNoEnseignant(@Param("noEnseignant") Enseignant noEnseignant);

	Evaluation findByNoEvaluation(Short noEvaluation);

	@Query("SELECT ev FROM Evaluation ev WHERE ev.promotion.id.codeFormation = :codeFormation AND ev.promotion.id.anneeUniversitaire = :anneeUniversitaire AND (ev.etat = 'DIS' OR ev.etat = 'CLO')")
	List<Evaluation> findByCodeFormationAnneeUnivAndEtat(@Param("codeFormation") String codeFormation, @Param("anneeUniversitaire") String anneeUniversitaire);




}
