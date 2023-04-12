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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="transaction_id")
    private long id;

    private long amount;

    @CreationTimestamp
    private LocalDateTime made_on;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "debit_acc_id")
    private Account debit_acc;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "credit_acc_id")
    private Account credit_acc;

    @OneToOne(mappedBy = "transaction")
    private Thrift_hub thriftHub;

    @OneToOne(mappedBy = "transaction")
    private ThePot pot;

    @Enumerated(EnumType.STRING)
    private TypeOf typeOf;
}
