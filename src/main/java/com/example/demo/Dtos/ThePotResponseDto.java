package com.example.demo.Dtos;

import com.example.demo.Model.Generated_account;
import com.example.demo.Model.ThePot;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

public class ThePotResponseDto extends ResponseDto
{

    private long id;

    private long collection_amount;

    private UserResponseDto collector;


    private ThriftResponseDto thrift;


    private long collection_index;

    private String collection_date;

    private long transaction_id;

    public ThePotResponseDto(){}

    public ThePotResponseDto(ThePot pot)
    {
        this.id = pot.getId();
        this.collection_amount = pot.getCollection_amount();
        this.collector = new UserResponseDto(pot.getCollector());
        this.thrift = new ThriftResponseDto(pot.getThrift());
        this.collection_index = pot.getCollection_index();
        this.collection_date = this.getStringDate(pot.getCollection_date());
        this.transaction_id = pot.getTransaction_id();
    }
}
