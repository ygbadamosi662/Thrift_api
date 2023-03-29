package com.example.demo.Dtos;

import com.example.demo.Enums.TypeOf;
import com.example.demo.Model.ThrifterHistory;
import com.example.demo.Model.Transaction;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class TransactionResponseDto extends ResponseDto
{
    private long id;

    private String paid_on;

    private long amount;

    private long debitted_acc_id;

    private long creditted_acc_id;

    private long info_id;

    @Enumerated(EnumType.STRING)
    private TypeOf typeOf;

    public TransactionResponseDto(Transaction transaction)
    {

    }
}
