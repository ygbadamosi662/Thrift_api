package com.example.demo.Dtos;

import com.example.demo.Enums.Account_type;
import com.example.demo.Enums.Side;
import com.example.demo.Model.Account;
import com.example.demo.Model.Beneficiary;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Getter
public class AccountResponseDto extends ResponseDto
{
    private long id;

    private String bank;

    private String acc_num;

    private String acc_name;

    @Enumerated(EnumType.STRING)
    private Account_type account_type;

    @Enumerated(EnumType.STRING)
    private Side side;

    private ResponseDto ben;

    public AccountResponseDto(){}
    public AccountResponseDto(Account account)
    {
        this.id = account.getId();
        this.bank = account.getBank();
        this.acc_num = account.getAcc_num();
        this.acc_name = account.getAcc_name();
        this.account_type = account.getAccount_type();
        this.side = account.getSide();
    }


    public void setsBen(Account account)
    {
        if(account.getBen().getClassName().equals("User"))
        {
            User user = (User) account.getBen();
            this.ben = new UserResponseDto(user);
        }
        else if(account.getBen().getClassName().equals("User"))
        {
            Thrift thrift = (Thrift) account.getBen();
            this.ben = new ThriftResponseDto(thrift);
        }
    }
}
