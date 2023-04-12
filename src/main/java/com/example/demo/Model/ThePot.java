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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="collection_id")
    private long id;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User collector;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "thrift_id")
    private Thrift thrift;


    private long collectionIndex;

    @CreationTimestamp
    private LocalDate collection_date;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id")
    private Transaction transaction;
}
