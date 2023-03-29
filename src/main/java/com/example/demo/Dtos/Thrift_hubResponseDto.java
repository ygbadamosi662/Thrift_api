package com.example.demo.Dtos;

import com.example.demo.Model.Generated_account;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.Thrift_hub;
import com.example.demo.Model.User;
import jakarta.persistence.*;

public class Thrift_hubResponseDto
{
    private long id;

    private UserResponseDto user;

    private ThriftResponseDto thrift;

    private long transaction_id;

    public Thrift_hubResponseDto(){}

    public Thrift_hubResponseDto(Thrift_hub hub)
    {
        this.id = hub.getId();
        this.user = new UserResponseDto(hub.getUser());
        this.thrift = new ThriftResponseDto(hub.getThrift());
        this.transaction_id = hub.getTransaction_id();
    }
}
