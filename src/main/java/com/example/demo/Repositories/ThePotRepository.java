package com.example.demo.Repositories;

import com.example.demo.Enums.Howfar;
import com.example.demo.Enums.Permissions;
import com.example.demo.Enums.Stamp;
import com.example.demo.Model.Notification;
import com.example.demo.Model.ThePot;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThePotRepository extends JpaRepository<ThePot,Long>
{
    Optional<ThePot> findById(Long id);

    Optional<ThePot> findByThriftAndCollectionIndex(Thrift thrift, long index);

    List<ThePot> findByThrift(Thrift thrift);

    Page<ThePot> findByThrift(Thrift thrift, Pageable page);

    Optional<ThePot> findByThriftAndCollectionIndexAndStamp(Thrift thrift, long collectionIndex, Stamp stamp);

    @Query(value = "SELECT p FROM ThePot p WHERE p.thrift.thrift_index = p.collectionIndex " +
            "AND p.collector = :user " +
            "AND p.stamp = :stamp ")
    List<ThePot> findIfNext(@Param("user") User user, @Param("stamp") Stamp stamp);

    List<ThePot> findByThriftAndCollector(Thrift thrift, User collector);
}
