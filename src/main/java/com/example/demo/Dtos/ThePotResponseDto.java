package com.example.demo.Dtos;

import com.example.demo.Enums.Stamp;
import com.example.demo.Model.ThePot;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.Transaction;
import com.example.demo.Model.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private Stamp stamp;

    private TransactionResponseDto transaction;

    public ThePotResponseDto(){}

    public ThePotResponseDto(ThePot pot)
    {
        this.id = pot.getId();
        this.collectionIndex = pot.getCollectionIndex();
        this.stamp = pot.getStamp();
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

    public void setCollector(User collector)
    {
        if(collector != null)
        {
            UserResponseDto dto = new UserResponseDto(collector);
            dto.setsAccount(collector.getAccount());
            this.collector = dto;
        }
    }

    public void setTransaction(Transaction trans)
    {
        if(trans != null)
        {
            TransactionResponseDto dto = new TransactionResponseDto(trans);
            dto.setAllWeirdAssClasses(trans);
            this.transaction = dto;
        }
    }

    public void setCollection_date(LocalDateTime date)
    {
        this.collection_date = this.getStringDateTime(date);
    }

    public void setAll(ThePot pot)
    {
        this.setCollection_date(pot.getCollection_date());
        this.setTransaction(pot.getTransaction());
        this.setCollector(pot.getCollector());
        this.setThrift(pot.getThrift());
    }
}
