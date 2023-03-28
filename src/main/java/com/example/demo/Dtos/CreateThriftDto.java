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

    @Enumerated(EnumType.STRING)
    public Term term;

    @NotNull
    private long per_term_amnt;

    @NotNull
    private long no_of_thrifters;

//    @JsonDeserialize(using = DateHandler.class)
    private String thrift_start;

    private Lifecycle cycle = Lifecycle.AWAITING;




    public Thrift getThrift ()
    {
        Thrift thrift = new Thrift();
        thrift.setThriftName(this.thriftName);
        thrift.setTerm(this.term);
        thrift.setPer_term_amnt(this.per_term_amnt);
        thrift.setNo_of_thrifters(this.no_of_thrifters);
        thrift.setThrift_start(this.setting_start());
//        thrift.setCycle(this.);
//        thrift.setThrift_start(this.thrift_start);

        return thrift;
    }

    public LocalDate setting_start()
    {
        LocalDate start_date = LocalDate.parse(this.thrift_start);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        return start_date;
    }

}
