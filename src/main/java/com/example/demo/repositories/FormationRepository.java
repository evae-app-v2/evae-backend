package com.example.demo.repositories;

import com.example.demo.models.Enseignant;
import com.example.demo.models.Formation;
import com.example.demo.models.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FormationRepository extends JpaRepository<Formation, String> {
    @Query("select f from Formation f ")
    List<Formation> getAllFormations();
    @Query("select f from Formation f")
    List<Formation> getFormations();
}