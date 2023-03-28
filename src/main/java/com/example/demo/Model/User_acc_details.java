package com.example.demo.Model;

import com.example.demo.Enums.Account_type;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class User_acc_details
{
    @Id
    @GeneratedValue
    @Column(name="user_acc_details_id")
    private long id;

    @OneToOne(mappedBy = "user_acc_details")
    private User user;

    private String bank;

    private String acc_num;

    private String acc_name;

    @Enumerated(EnumType.STRING)
    private Account_type account_type;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;
}
