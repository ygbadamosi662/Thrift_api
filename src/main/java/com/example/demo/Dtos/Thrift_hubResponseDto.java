package com.example.demo.Dtos;

import com.example.demo.Model.Thrift_hub;
import com.example.demo.Model.Transaction;

public class Thrift_hubResponseDto extends ResponseDto
{
    private long id;

    private UserResponseDto user;

    private ThriftResponseDto thrift;

    private TransactionResponseDto transaction;

    public Thrift_hubResponseDto(){}

    public Thrift_hubResponseDto(Thrift_hub hub)
    {
        this.id = hub.getId();
        this.user = new UserResponseDto(hub.getUser());
        this.thrift = new ThriftResponseDto(hub.getThrift());
        this.transaction = new TransactionResponseDto(hub.getTransaction());
    }
}
