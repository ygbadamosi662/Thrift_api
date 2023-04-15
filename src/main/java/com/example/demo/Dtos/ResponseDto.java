package com.example.demo.Dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ResponseDto
{
    private String dateFormat = "MM dd, YYYY";

    private String dateTimeFormat = "MM dd, YYYY HH:mm:ss a";

    private String jwt;

    private String more_info;
    public String getStringDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd, YYYY");
        return date.format(formatter);
    }

    public String getStringDateTime(LocalDateTime dateTime)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yMM dd, YYYY HH:mm:ss a");
        return dateTime.format(formatter);
    }
}
