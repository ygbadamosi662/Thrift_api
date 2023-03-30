package com.example.demo.Dtos;

import com.example.demo.Enums.TypeOf;
import com.example.demo.Model.Account;
import com.example.demo.Model.Transaction;
import jakarta.persistence.*;

public class TransactionResponseDto extends ResponseDto
{
    private long id;

    private String paid_on;

    private long amount;

    private Account debit_acc;

    private Account credit_acc;

    @Enumerated(EnumType.STRING)
    private TypeOf typeOf;

    public TransactionResponseDto(){}

    public TransactionResponseDto(Transaction transaction)
    {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.debit_acc = transaction.getDebit_acc();
        this.credit_acc = transaction.getCredit_acc();
        this.typeOf = transaction.getTypeOf();
    }
}
