package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class ThePot
{
    @Id
    @GeneratedValue
    @Column(name="collection_id")
    private long id;

    private long collection_amount;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User collector;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "thrift_id")
    private Thrift thrift;


    private long collection_index;

    @CreationTimestamp
    private LocalDate collection_date;

    private long transaction_id;

//    @OneToOne
//    @JoinColumn(name = "transaction_id")
//    private Transaction transaction;
}
