package com.example.demo.Model;

import com.example.demo.Enums.TypeOf;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Transaction
{
    @Id
    @GeneratedValue
    @Column(name="transaction_id")
    private long id;

    @CreationTimestamp
    private LocalDateTime paid_on;

    private long amount;

    private long debitted_acc_id;

    private long creditted_acc_id;

    private long info_id;

    @Enumerated(EnumType.STRING)
    private TypeOf typeOf;

}
