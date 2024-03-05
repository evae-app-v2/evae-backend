package com.example.demo.repositories;


import com.example.demo.models.UniteEnseignement;
import com.example.demo.models.UniteEnseignementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UniteEnseignementRepository extends JpaRepository<UniteEnseignement, UniteEnseignementId> {

    @Query("select ue from UniteEnseignement ue where ue.id = :idUe")
    UniteEnseignement findByUniteEnseignementId(@Param("idUe") UniteEnseignementId idUe);
}