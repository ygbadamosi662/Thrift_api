package com.example.demo.Repositories;

import com.example.demo.Model.Thrift;
import com.example.demo.Model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction,Long>
{
    Optional<Thrift> findById(long id);
}
