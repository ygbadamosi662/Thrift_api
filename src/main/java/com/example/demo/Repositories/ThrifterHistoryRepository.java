package com.example.demo.Repositories;

import com.example.demo.Model.Thrift;
import com.example.demo.Model.ThrifterHistory;
import com.example.demo.Model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ThrifterHistoryRepository extends JpaRepository<ThrifterHistory,Long>
{

    List<ThrifterHistory> findByThrift(Thrift thrift);

    List<ThrifterHistory> findByUser(User user);

    Optional<ThrifterHistory> findByThriftAndUser(Thrift thrift, User user);

    Slice<ThrifterHistory> findByThrift(Thrift thrift, Pageable page);
}
