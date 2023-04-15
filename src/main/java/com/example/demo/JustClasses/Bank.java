package com.example.demo.JustClasses;

import com.example.demo.Repositories.ThriftsRepository;
import com.example.demo.Repositories.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class Bank
{
    private String className;

    private Beneficiary ben;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ThriftsRepository thriftRepo;
}
