package com.example.demo.repositories;

import com.example.demo.models.Rubrique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface RubriqueRepository extends JpaRepository<Rubrique, Integer> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Rubrique r WHERE r.designation = :designation")
    boolean existsByDesignation(@Param("designation") String designation);
    @Query("SELECT MAX(r.ordre) FROM Rubrique r")
    Long findMaxOrdre();

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Rubrique r WHERE r.designation = :designation AND r.id <> :id")
    boolean existsByDesignationAndIdNot(@Param("designation") String designation, @Param("id") Integer id);

    @Query("SELECT r FROM Rubrique r WHERE r.id NOT IN (SELECT rq.idRubrique.id FROM RubriqueQuestion rq)")
    List<Rubrique> findRubriquesWithoutQuestions();

    Optional<Rubrique> findByDesignation(String designation);
    void deleteByDesignation(String designation);
}
