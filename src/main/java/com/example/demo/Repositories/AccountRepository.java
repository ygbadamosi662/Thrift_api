package com.example.demo.Repositories;


import com.example.demo.Enums.Side;
import com.example.demo.Model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account,Long>
{

    Page<Account> findBySide(Side side, Pageable pageable);

    List<Account> findBySide(Side side);

    Long countBySide(Side side);
}
