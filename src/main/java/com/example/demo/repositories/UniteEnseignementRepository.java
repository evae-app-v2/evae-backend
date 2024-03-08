package com.example.demo.repositories;


import com.example.demo.models.Enseignant;
import com.example.demo.models.UniteEnseignement;
import com.example.demo.models.UniteEnseignementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UniteEnseignementRepository extends JpaRepository<UniteEnseignement, UniteEnseignementId> {

    @Query("select ue from UniteEnseignement ue where ue.id = :idUe")
    UniteEnseignement findByUniteEnseignementId(@Param("idUe") UniteEnseignementId idUe);

    @Query("select ue from UniteEnseignement ue where ue.id.codeFormation = :codeFormation and ue.noEnseignant = :noEnseignant")
    List<UniteEnseignement> getUniteEnseignementByCodeFormationAndNoEnseignant(@Param("noEnseignant") Enseignant noEnseignant, @Param("codeFormation") String codeFormation);
}