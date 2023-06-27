package com.example.demo.Model;

import com.example.demo.Enums.Stamp;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @Nullable
    private User collector;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "thrift_id")
    private Thrift thrift;

    private long collectionIndex;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

    @Nullable
    private LocalDateTime collection_date;

    @Enumerated(EnumType.STRING)
    private Stamp stamp;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id")
    @Nullable
    private Transaction transaction;

    public ThePot(){}

    public ThePot(Thrift thrift, Stamp stamp, long collectionIndex)
    {
        this.thrift = thrift;
        this.stamp = stamp;
        this.collectionIndex = collectionIndex;
    }
}
