package com.example.demo.Dtos;

import com.example.demo.Model.Thrift;
import com.example.demo.Model.Thrift_hub;
import com.example.demo.Model.Transaction;
import com.example.demo.Model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
            ThriftResponseDto dto = new ThriftResponseDto(thrift);
            dto.setAllWeirdAssClasses(thrift);
            this.thrift = dto;
        }
    }

    public void setUser(User user)
    {
        if(user != null)
        {
            UserResponseDto dto = new UserResponseDto(user);
            dto.setsAccount(user.getUserAccount());
            this.user = dto;
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
