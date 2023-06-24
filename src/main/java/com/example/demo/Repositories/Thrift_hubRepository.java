package com.example.demo.Repositories;

import com.example.demo.Model.ThePot;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.Thrift_hub;
import com.example.demo.Model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Thrift_hubRepository extends JpaRepository<Thrift_hub,Long>
{

    List<Thrift_hub> findByThrift(Thrift thrift);

    List<Thrift_hub> findByThriftAndThriftIndex(Thrift thrift, long index);

    List<Thrift_hub> findByThriftAndUser(Thrift thrift, User user);

    Slice<Thrift_hub> findByThriftAndUser(Thrift thrift, User user, Pageable page);

    Slice<Thrift_hub> findByThrift(Thrift thrift, Pageable page);

    Slice<Thrift_hub> findByThriftAndThriftIndex(Thrift thrift, Long index, Pageable page);
}
