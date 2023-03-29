package com.example.demo.Dtos;

import com.example.demo.Enums.Account_type;
import com.example.demo.Enums.Status;
import com.example.demo.Model.Generated_account;
import com.example.demo.Model.Thrift;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

public class GenAccResponseDto extends ResponseDto
{

    private long id;

    private String bank;

    private String acc_num;

    private String acc_name;

    @Enumerated(EnumType.STRING)
    private Account_type account_type;

    @Enumerated(EnumType.STRING)
    private Status status;

    private ThriftResponseDto thrift;

    public GenAccResponseDto(){}
    public GenAccResponseDto(Generated_account genAcc)
    {
        this.id = genAcc.getId();
        this.bank = genAcc.getBank();
        this.acc_num = genAcc.getAcc_num();
        this.acc_name = genAcc.getAcc_name();
        this.account_type = genAcc.getAccount_type();
        this.status = genAcc.getStatus();
        this.thrift = new ThriftResponseDto(genAcc.getThrift());
    }
}
