package com.example.demo.Model;

import com.example.demo.Enums.Account_type;
import com.example.demo.Enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
public class Generated_account
{
    @Id
    @GeneratedValue
    @Column(name="generated_account_id")
    private long id;

    private String bank;

    private String acc_num;

    private String acc_name;

    @Enumerated(EnumType.STRING)
    private Account_type account_type;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne
    @JoinColumn(name = "thrift_id")
    private Thrift thrift;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;
}
