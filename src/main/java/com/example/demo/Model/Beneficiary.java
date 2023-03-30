package com.example.demo.Model;

import com.example.demo.Repositories.AccountRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class Beneficiary
{
    @Autowired
    private AccountRepository accRepo;

    private String className;

    private Account account;

}
