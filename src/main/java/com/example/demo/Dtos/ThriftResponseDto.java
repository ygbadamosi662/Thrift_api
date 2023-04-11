package com.example.demo.Dtos;

import com.example.demo.Enums.Lifecycle;
import com.example.demo.Enums.Term;
import com.example.demo.Model.Account;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class ThriftResponseDto extends ResponseDto
{

    private long id;
    private String ticket;

    private String name;

    @Enumerated(EnumType.STRING)
    private Lifecycle cycle;

    private String thrift_start;

    private String thrift_end;

    private long collection_index;

    private AccountResponseDto account;

    @Enumerated(EnumType.STRING)
    public Term term;

    private UserResponseDto organizer;

    private UserResponseDto collector;

    private long per_term_amnt;

    private long collection_amount;

    private long slots;

    private String next_thrift_date;

    private long collection_available;


    public ThriftResponseDto(){}

    public ThriftResponseDto(Thrift thrift)
    {
        this.collection_amount = thrift.getCollection_amount();
        this.collection_available = thrift.getCollection_available();
        this.collection_index = thrift.getThrift_index();
        this.slots = thrift.getSlots();
        this.cycle = thrift.getCycle();
        this.per_term_amnt = thrift.getPer_term_amnt();
        this.term = thrift.getTerm();
        this.name = thrift.getThriftName();
        this.ticket = thrift.getTicket();
        this.id = thrift.getId();
    }

    public void setOrganizer(User user)
    {
        System.out.println(user);
        if(user != null)
        {
            this.organizer = new UserResponseDto(user);
        }
    }

    public void setCollector(User user)
    {
        if(user != null)
        {
            this.collector = new UserResponseDto(user);
        }
    }

    public void setAccount(Account acc)
    {
        if(acc != null)
        {
            this.account = new AccountResponseDto(acc);
        }
    }

    public void setThrift_dates(Thrift thrift)
    {
        if(thrift != null)
        {
            this.thrift_start = this.getStringDate(thrift.getThrift_start());
            this.thrift_end = this.getStringDate(thrift.getThrift_end());
            if(thrift.getNext_thrift_date() != null)
            {
                this.next_thrift_date = this.getStringDate(thrift.getNext_thrift_date());
            }
        }
    }

    public void setAllWeirdAssClasses(Thrift thrift)
    {
        this.setAccount(thrift.getThriftAccount());
        this.setOrganizer(thrift.getOrganizer());
        this.setCollector(thrift.getCollector());
        this.setThrift_dates(thrift);
    }

}


