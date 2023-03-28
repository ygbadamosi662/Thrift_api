package com.example.demo.Repositories;

import com.example.demo.Model.Thrift;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ThriftsRepository extends JpaRepository<Thrift,Long>
{
    Optional<Thrift> findById(long id);

    Optional<Thrift> findByOrganizerAndThriftName(User organizer, String name);

    Optional<Thrift> findByTicket(String ticket);

    long countByOrganizer(User organizer);

    List<Thrift> findByOrganizer(User organizer);
}
