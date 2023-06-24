package com.example.demo.Dtos;

import com.example.demo.Enums.Lifecycle;
import com.example.demo.Enums.Term;
import com.example.demo.Model.Thrift;
import com.example.demo.Utilities.Utility;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoneMemberThriftResponseDto extends ResponseDto
{
    private String ticket;

    private String name;

    @Enumerated(EnumType.STRING)
    private Lifecycle cycle;

    private String thrift_start;

    private String thrift_end;

    @Enumerated(EnumType.STRING)
    public Term term;

    private String org_email;

    private String org_phone;

    private long per_term_amnt;

    private long collection_amount;

    private String how_full_in_perecentage;

    private int slots;



    public NoneMemberThriftResponseDto(Thrift thrift)
    {
        this.collection_amount = thrift.getCollection_amount();
        this.slots = thrift.getSlots();
        this.cycle = thrift.getCycle();
        this.per_term_amnt = thrift.getPer_term_amnt();
        this.term = thrift.getTerm();
        this.name = thrift.getThriftName();
        this.ticket = thrift.getTicket();
        this.org_email = thrift.getOrganizer().getEmail();
        this.org_phone = thrift.getOrganizer().getPhone();
    }

    public void setThrift_dates(Thrift thrift)
    {
        if(thrift != null)
        {
            this.thrift_start = this.getStringDate(thrift.getThrift_start());
            this.thrift_end = this.getStringDate(thrift.getThrift_end());
        }
    }

    public void setHow_full_in_perecentage(Thrift thrift)
    {
        Utility util = new Utility();
        this.how_full_in_perecentage = String.format("%.2f", util.capacityInPercntage(thrift));
    }

    public void setAllWeirdAssClasses(Thrift thrift)
    {
        this.setThrift_dates(thrift);
        this.setHow_full_in_perecentage(thrift);
    }
}
