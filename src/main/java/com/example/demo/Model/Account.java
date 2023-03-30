package com.example.demo.Model;

import com.example.demo.Enums.Account_type;
import com.example.demo.Enums.Side;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

//    public Account(){}
    public Account()
    {
        this.setsBen();
        this.setsClassName();
    }
    public void setsClassName()
    {
        this.setClassName(Account.class.getSimpleName());
    }

    public void setsBen()
    {
        Optional<User> byEmail = this.getUserRepo().findByEmail(this.benUnique);
        if(byEmail.isPresent())
        {
            this.setBen(byEmail.get());
        }
        else
        {
            Optional<Thrift> byTicket = this.getThriftRepo().findByTicket(this.benUnique);
            if(byTicket.isPresent())
            {
                this.setBen(byTicket.get());
            }
        }
    }
}
