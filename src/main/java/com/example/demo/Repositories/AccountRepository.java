package com.example.demo.Repositories;


import com.example.demo.Model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long>
{

}
