package com.example.demo.Dtos;

import com.example.demo.Enums.Lifecycle;
import com.example.demo.Enums.Term;
import com.example.demo.Model.Thrift;
import com.example.demo.Utilities.Utility;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;


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

    private int slots;

    private String next_thrift_date;

    private long collection_available;

    private String how_full_in_perecentage;


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

    public void setOrganizer(Thrift thrift)
    {
        if(thrift.getOrganizer() != null)
        {
            UserResponseDto dto = new UserResponseDto(thrift.getOrganizer());
            dto.setsAccount(thrift.getOrganizer().getAccount());
            this.organizer = dto;
        }
    }

    public void setCollector(Thrift thrift)
    {
        if(thrift.getCollector() != null)
        {
            UserResponseDto dto = new UserResponseDto(thrift.getCollector());
            dto.setsAccount(thrift.getCollector().getAccount());
            this.collector = dto;
        }
    }

    public void setAccount(Thrift thrift)
    {
        if(thrift.getAccount() != null)
        {
            this.account = new AccountResponseDto(thrift.getAccount());
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

    public void setHow_full_in_perecentage(Thrift thrift)
    {
        Utility util = new Utility();
        this.how_full_in_perecentage = String.format("%.2f", util.capacityInPercntage(thrift));
    }

    public void setAllWeirdAssClasses(Thrift thrift)
    {
        this.setAccount(thrift);
        this.setOrganizer(thrift);
        this.setCollector(thrift);
        this.setThrift_dates(thrift);
        this.setHow_full_in_perecentage(thrift);
    }

}


