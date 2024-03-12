package com.example.demo.repositories;

import com.example.demo.models.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.Promotion;
import com.example.demo.models.PromotionId;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, PromotionId>{

    @Query("select p from Promotion p where p.id = :idPro")
    Promotion findByPromotionId(@Param("idPro") PromotionId idPro);
	
	/*@Query("select p from Promotion where p.id = :idPro")
	Promotion findByPromotionId(@Param("idPro") PromotionId idPro);*/

    @Query("select p from Promotion p where p.noEnseignant = :noEnseignant")
    List<Promotion> getPromotionsByNoEnseignant(@Param("noEnseignant") Enseignant noEnseignant);

    @Query("select p from Promotion p where p.id.codeFormation = :anneePro")
    List<Promotion> getPromotionsByAnneeProAndNoEnseignant(@Param("anneePro") String anneePro);

}
