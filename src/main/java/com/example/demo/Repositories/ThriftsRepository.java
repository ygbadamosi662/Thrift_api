package com.example.demo.Repositories;

import com.example.demo.Model.Account;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThriftsRepository extends JpaRepository<Thrift,Long>
{
    Optional<Thrift> findById(long id);

    Optional<Thrift> findByTicket(String ticket);

    long countByOrganizer(User organizer);

    List<Thrift> findByOrganizer(User organizer);

    List<Thrift> findByAccount(Account account);

    List<Thrift> findByAccount(Account account, Pageable pageable);

//    @Query(value = "SELECT t.name, t.ticket, t.organizer.email FROM Thrift t WHERE t.ticket LIKE CONCAT('%', :pattern, '%')", nativeQuery = true)
//    List<Thrift> findByPattern(@Param("pattern") String pattern);

//    @Query("SELECT t FROM Thrift t JOIN t.organizer o " +
//            "WHERE t.name LIKE %:pattern% OR o.fname LIKE %:pattern% " +
//            "OR o.lname LIKE %:pattern% OR o.email LIKE %:pattern%")
    @Query(value = "SELECT t.* FROM Thrift t JOIN User o " +
        "ON t.organizer_id = o.user_id " +
        "WHERE t.thrift_name LIKE %:pattern% OR o.fname LIKE %:pattern% " +
        "OR o.lname LIKE %:pattern% OR o.email LIKE %:pattern%", nativeQuery = true)
    List<Thrift> findByPattern(@Param("pattern") String pattern);


}
