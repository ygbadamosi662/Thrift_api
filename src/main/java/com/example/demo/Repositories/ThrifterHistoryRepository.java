package com.example.demo.Repositories;

import com.example.demo.Enums.Consent;
import com.example.demo.Enums.Lifecycle;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.ThrifterHistory;
import com.example.demo.Model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThrifterHistoryRepository extends JpaRepository<ThrifterHistory,Long>
{

    List<ThrifterHistory> findByThrift(Thrift thrift);

    List<ThrifterHistory> findByUser(User user);

    Optional<ThrifterHistory> findByThriftAndUser(Thrift thrift, User user);

//    Slice<ThrifterHistory> findByThrift(Thrift thrift, Pageable page);

    Slice<ThrifterHistory> findByThriftAndConsent(Thrift thrift, Consent consent, Pageable page);

    List<ThrifterHistory> findByThriftAndConsent(Thrift thrift, Consent consent);

    List<ThrifterHistory> findByUserAndConsent(User user, Consent consent);

    @Query(value = "SELECT h FROM ThrifterHistory h WHERE h.user = :user " +
            "AND h.consent = :oyellow " +
            "OR h.consent = :tyellow")
    List<ThrifterHistory> findByUserAndConsents(@Param("user") User user,
                                               @Param("oyellow") Consent conO,
                                               @Param("tyellow") Consent ConT);

    @Query(value = "SELECT h FROM ThrifterHistory h " +
            "WHERE h.user = :user AND h.thrift.cycle = :cycle " +
            "AND (h.consent = :oyellow OR h.consent = :tyellow)")
    List<ThrifterHistory> findByUserAndThriftCycleAndConsents(@Param("user") User user,
                                                 @Param("cycle") Lifecycle cycle,
                                                 @Param("oyellow") Consent conO,
                                                 @Param("tyellow") Consent conT);
}
