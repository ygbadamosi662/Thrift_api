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
        this.consent = history.getConsent();
    }

    public void setThrift(Thrift thrift)
    {
        if(thrift != null)
        {
            this.thrift = new ThriftResponseDto(thrift);
        }
    }

    public void setUser(User user)
    {
        if(user != null)
        {
            this.user = new UserResponseDto(user);
        }
    }

    public void setAll(ThrifterHistory history)
    {
        this.setThrift(history.getThrift());
        this.setUser(history.getUser());
    }
}
