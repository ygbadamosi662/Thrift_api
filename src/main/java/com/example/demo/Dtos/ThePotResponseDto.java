package com.example.demo.Dtos;

import com.example.demo.Model.ThePot;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.Transaction;
import com.example.demo.Model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ThePotResponseDto extends ResponseDto
{

    private long id;

    private String more_info;

    private UserResponseDto collector;

    private ThriftResponseDto thrift;

    private long collectionIndex;

    private String collection_date;

    private TransactionResponseDto transaction;

    public ThePotResponseDto(){}

    public ThePotResponseDto(ThePot pot)
    {
        this.id = pot.getId();
        this.collectionIndex = pot.getCollectionIndex();
    }

    public void setThrift(Thrift thrift)
    {
        if(thrift != null)
        {
            this.thrift = new ThriftResponseDto(thrift);
        }
    }

    public void setCollector(User collector)
    {
        if(collector != null)
        {
            this.collector = new UserResponseDto(collector);
        }
    }

    public void setTransaction(Transaction trans)
    {
        if(trans != null)
        {
            this.transaction = new TransactionResponseDto(trans);
        }
    }

    public void setCollection_date(LocalDate date)
    {
        this.collection_date = this.getStringDate(date);
    }

    public void setAll(ThePot pot)
    {
        this.setCollection_date(pot.getCollection_date());
        this.setTransaction(pot.getTransaction());
        this.setCollector(pot.getCollector());
        this.setThrift(pot.getThrift());
    }
}
