package com.example.demo.Repositories;

import com.example.demo.Enums.Role;
import com.example.demo.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>
{
    Optional<User> findByEmail(String email);

    Slice<User> findByRole(Role role, Pageable pageable);
}
