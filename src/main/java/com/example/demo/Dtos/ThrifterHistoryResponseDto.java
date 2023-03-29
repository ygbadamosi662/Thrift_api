package com.example.demo.Dtos;

import com.example.demo.Enums.Consent;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.Thrift_hub;
import com.example.demo.Model.ThrifterHistory;
import com.example.demo.Model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class ThrifterHistoryResponseDto extends ResponseDto
{
    private long id;

    private ThriftResponseDto thrift;

    private UserResponseDto user;

    @Enumerated(EnumType.STRING)
    private Consent consent;

    public ThrifterHistoryResponseDto(){}

    public ThrifterHistoryResponseDto(ThrifterHistory history)
    {
        this.id = history.getId();
        this.thrift = new ThriftResponseDto(history.getThrift());
        this.user = new UserResponseDto(history.getUser());
        this.consent = history.getConsent();

    }
}
