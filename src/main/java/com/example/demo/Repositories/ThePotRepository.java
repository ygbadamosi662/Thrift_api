package com.example.demo.Repositories;

import com.example.demo.Model.ThePot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThePotRepository extends JpaRepository<ThePot,Long>
{
    Optional<ThePot> findById(Long id);
}
