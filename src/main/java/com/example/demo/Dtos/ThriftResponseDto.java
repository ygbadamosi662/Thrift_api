package com.example.demo.Dtos;

import com.example.demo.Enums.Lifecycle;
import com.example.demo.Enums.Term;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
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

    private GenAccResponseDto generatedAccount;

    @Enumerated(EnumType.STRING)
    public Term term;

    private UserResponseDto organizer;

    private UserResponseDto collector;

    private long per_term_amnt;

    private long collection_amount;

    private long no_of_thrifters;

    private String next_thrift_date;

    private long collection_available;


    public ThriftResponseDto(){}

    public ThriftResponseDto(Thrift thrift)
    {
        this.collection_amount = thrift.getCollection_amount();
        this.collection_available = thrift.getCollection_available();
        this.collection_index = thrift.getCollection_index();
        this.collector = new UserResponseDto(thrift.getCollector());
        this.no_of_thrifters = thrift.getNo_of_thrifters();
        this.cycle = thrift.getCycle();
        this.organizer = new UserResponseDto(thrift.getOrganizer());
        this.per_term_amnt = thrift.getPer_term_amnt();
        this.generatedAccount = new GenAccResponseDto(thrift.getGenerated_account());
        this.term = thrift.getTerm();
        this.name = thrift.getThriftName();
        this.ticket = thrift.getTicket();
        this.next_thrift_date = this.getStringDate(thrift.getNext_thrift_date());
        this.thrift_end = this.getStringDate(thrift.getThrift_end());
        this.thrift_start = this.getStringDate(thrift.getThrift_start());
        this.id = thrift.getId();
    }

}


