package com.example.demo.Dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ResponseDto
{
    private String className;

    private String dateFormat = "MM dd, YYYY";

    public String getStringDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd, YYYY");
        return date.format(formatter);
    }
}
