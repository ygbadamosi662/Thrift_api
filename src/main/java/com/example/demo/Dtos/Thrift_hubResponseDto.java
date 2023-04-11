package com.example.demo.Dtos;

import com.example.demo.Model.Thrift;
import com.example.demo.Model.Thrift_hub;
import com.example.demo.Model.Transaction;
import com.example.demo.Model.User;

import java.time.LocalDate;

public class Thrift_hubResponseDto extends ResponseDto
{
    private long id;

    private UserResponseDto user;

    private ThriftResponseDto thrift;

    private String date;

    private long index;

    private TransactionResponseDto transaction;

    public Thrift_hubResponseDto(){}

    public Thrift_hubResponseDto(Thrift_hub hub)
    {
        this.id = hub.getId();
        this.index = hub.getThriftIndex();
    }

    public void setThrift(Thrift thrift)
    {
        if(thrift != null)
        {
            this.thrift = new ThriftResponseDto(thrift);
        }
    }

    public void setUser(User user)
    {
        if(user != null)
        {
            this.user = new UserResponseDto(user);
        }
    }

    public void setDate(LocalDate date)
    {
        if(date != null)
        {
            this.date = this.getStringDate(date);
        }
    }

    public void setAll(Thrift_hub hub)
    {
        this.setDate(hub.getDate());
        this.setThrift(hub.getThrift());
        this.setUser(hub.getUser());
    }

}
