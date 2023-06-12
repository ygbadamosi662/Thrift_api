package com.example.demo.Repositories;

import com.example.demo.Model.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist,Long>
{

    Optional<JwtBlacklist> findByJwt(String jwt);
}
