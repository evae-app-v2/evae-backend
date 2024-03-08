package com.example.demo.repositories;

import com.example.demo.models.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.ElementConstitutif;
import com.example.demo.models.ElementConstitutifId;

import java.util.List;

public interface ElementConstitutifRepository extends JpaRepository<ElementConstitutif,ElementConstitutifId>{
	
	@Query("select ec from ElementConstitutif ec where ec.id = :id")
	ElementConstitutif findByElementConstitutifId(@Param("id") ElementConstitutifId id);

	@Query("select ec from ElementConstitutif ec where ec.id.codeFormation = :codeFormation and ec.id.codeUe = :codeUe and ec.noEnseignant = :noEnseignant")
	List<ElementConstitutif> getElementConstitutifByNoEnseignantAndCodeFormationAndCodeUe(@Param("noEnseignant") Enseignant noEnseignant, @Param("codeFormation") String codeFormation, @Param("codeUe") String codeUe);

}
