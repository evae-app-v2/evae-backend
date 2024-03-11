package com.example.demo.repositories;

import com.example.demo.models.Droit;
import com.example.demo.models.DroitId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroitRepository extends JpaRepository<Droit, DroitId> {
}