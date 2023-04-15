package com.example.demo.Dtos;

import com.example.demo.Enums.Lifecycle;
import com.example.demo.Enums.Term;
import com.example.demo.Model.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CreateThriftDto
{
    @NotNull
    @NotBlank
    private String jwt;

    @NotNull
    @NotBlank
    @Size(min = 5,max = 25)
    private String thriftName;

    public String term;

    @NotNull
    private long per_term_amnt;

    private String thrift_start;

    private Lifecycle cycle = Lifecycle.AWAITING;




    public Thrift getThrift ()
    {
        Thrift thrift = new Thrift();
        thrift.setThriftName(this.thriftName);
        thrift.setTerm(Term.valueOf(this.term));
        thrift.setPer_term_amnt(this.per_term_amnt);
        thrift.setThrift_start(this.setting_start());
        thrift.setNext_thrift_date(this.setting_start());

        return thrift;
    }

    public LocalDate setting_start()
    {
        LocalDate start_date = LocalDate.parse(this.thrift_start);

        return start_date;
    }

}
