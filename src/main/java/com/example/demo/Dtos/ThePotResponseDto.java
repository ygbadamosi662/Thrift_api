package com.example.demo.Dtos;

import com.example.demo.Model.ThePot;

public class ThePotResponseDto extends ResponseDto
{

    private long id;

    private long collection_amount;

    private UserResponseDto collector;


    private ThriftResponseDto thrift;


    private long collection_index;

    private String collection_date;

    private TransactionResponseDto transaction;

    public ThePotResponseDto(){}

    public ThePotResponseDto(ThePot pot)
    {
        this.id = pot.getId();
        this.collection_amount = pot.getCollection_amount();
        this.collector = new UserResponseDto(pot.getCollector());
        this.thrift = new ThriftResponseDto(pot.getThrift());
        this.collection_index = pot.getCollection_index();
        this.collection_date = this.getStringDate(pot.getCollection_date());
        this.transaction = new TransactionResponseDto(pot.getTransaction());
    }
}
