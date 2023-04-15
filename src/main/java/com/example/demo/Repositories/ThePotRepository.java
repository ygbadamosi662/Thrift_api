package com.example.demo.Repositories;

import com.example.demo.Model.ThePot;
import com.example.demo.Model.Thrift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ThePotRepository extends JpaRepository<ThePot,Long>
{
    Optional<ThePot> findById(Long id);

    Optional<ThePot> findByThriftAndCollectionIndex(Thrift thrift, long index);

    List<ThePot> findByThrift(Thrift thrift);
}
