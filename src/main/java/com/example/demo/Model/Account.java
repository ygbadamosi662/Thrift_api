package com.example.demo.Model;

import com.example.demo.Dtos.AddAccDto;
import com.example.demo.Enums.Account_type;
import com.example.demo.Enums.Side;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Account extends Bank
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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


    @OneToOne(mappedBy = "userAccount")
    private User userBen;

    @OneToOne(mappedBy = "thriftAccount")
    private Thrift thriftBen;

    public Account(){}

    public void setsBen()
    {
        if(this.getUserBen() == null)
        {
            try
            {
                this.setBen(this.getThriftBen());
            }
            catch (NullPointerException e)
            {
                System.out.println("thriftBen is null");
            }
        }
        else if(this.getThriftBen() == null)
        {
            try
            {
                this.setBen(this.getUserBen());
            }
            catch (NullPointerException e)
            {
                System.out.println("userBen is null");
            }
        }
        this.setsClassName();
    }

    public Account(AddAccDto dto)
    {
        this.setAccount_type(dto.getType());
        this.setAcc_name(dto.getAccName());
        this.setBank(dto.getBank());
        this.setAcc_num(dto.getAccNum());
    }
    public void setsClassName()
    {
        this.setClassName(Account.class.getSimpleName());
    }

}
