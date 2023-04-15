package com.example.demo.Dtos;

import com.example.demo.Enums.TypeOf;
import com.example.demo.Model.Account;
import com.example.demo.Model.Transaction;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TransactionResponseDto extends ResponseDto
{
    private long id;

    private long amount;

    private AccountResponseDto debit_acc;

    private AccountResponseDto credit_acc;

    private String dateTime;

    @Enumerated(EnumType.STRING)
    private TypeOf typeOf;

    public TransactionResponseDto(){}

    public TransactionResponseDto(Transaction transaction)
    {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.typeOf = transaction.getTypeOf();
    }

    public void setDebit_acc(Account acc)
    {
        if(acc != null)
        {
            AccountResponseDto dto= new AccountResponseDto(acc);
            dto.setsBen(acc);
            this.debit_acc = dto;
        }
    }

    public void setCredit_acc(Account acc)
    {
        if(acc != null)
        {
            AccountResponseDto dto= new AccountResponseDto(acc);
            dto.setsBen(acc);
            this.credit_acc = dto;
        }
    }

    public void setDateTime(LocalDateTime dateTime)
    {
        if(dateTime != null)
        {
            this.dateTime = this.getStringDateTime(dateTime);
        }
    }

    public void setAllWeirdAssClasses(Transaction trans)
    {
        this.setCredit_acc(trans.getCredit_acc());
        this.setDebit_acc(trans.getDebit_acc());
        this.setDateTime(trans.getMade_on());
    }

}
