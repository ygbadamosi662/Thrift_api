package com.example.demo.Model;

import com.example.demo.Enums.Account_type;
import com.example.demo.Enums.Side;
import com.example.demo.Repositories.ThriftsRepository;
import com.example.demo.Repositories.UserRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@Entity
public class Account extends Bank
{
    @Id
    @GeneratedValue
    @Column(name="acc_id")
    private long id;

    private String bank;

    private String acc_num;

    private String acc_name;

    @Enumerated(EnumType.STRING)
    private Account_type account_type;

    @Enumerated(EnumType.STRING)
    private Side side;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

    @Nullable
    private String benUnique;

    @OneToOne(mappedBy = "userAccount")
    private User userBen;

    @OneToOne(mappedBy = "thriftAccount")
    private Thrift thriftBen;

    public Account(){}

    public Account(User user, Thrift thrift)
    {
        user = this.userBen;
        thrift = this.thriftBen;

        if(user.equals(null))
        {
            this.setBen(thrift);
        }
        else if(thrift.equals(null))
        {
            this.setBen(user);
        }
        this.setsClassName();
    }
    public void setsClassName()
    {
        this.setClassName(Account.class.getSimpleName());
    }

}
