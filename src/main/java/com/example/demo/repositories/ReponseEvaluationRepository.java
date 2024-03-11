package com.example.demo.repositories;

import com.example.demo.models.ReponseEvaluation;
import com.example.demo.models.ReponseQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReponseEvaluationRepository extends JpaRepository<ReponseEvaluation, Integer> {

    @Query("SELECT re FROM ReponseEvaluation re WHERE re.noEtudiant.noEtudiant =:noEtudiant AND re.idEvaluation.id =:idevaluation")
    ReponseEvaluation findByEtudiantEvaluation(@Param("noEtudiant") String noEtudiant, @Param("idevaluation") Integer idevaluation);


    @Query("select re from ReponseEvaluation re where re.noEtudiant.noEtudiant = :noEtudiant and re.idEvaluation.id = :idEvaluation")
    ReponseEvaluation findByEtudiantAndEvaluation(@Param("noEtudiant") String noEtudiant, @Param("idEvaluation") int idEvaluation);
}



