package com.example.demo.Dtos;

import com.example.demo.Enums.Consent;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.Thrift_hub;
import com.example.demo.Model.ThrifterHistory;
import com.example.demo.Model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Getter
@Setter
public class ThrifterHistoryResponseDto extends ResponseDto
{
    private long id;

    private ThriftResponseDto thrift;

    private UserResponseDto user;

    private int slot;

    @Enumerated(EnumType.STRING)
    private Consent consent;

    public ThrifterHistoryResponseDto(){}

    public ThrifterHistoryResponseDto(ThrifterHistory history)
    {
        this.id = history.getId();
        this.consent = history.getConsent();
        this.slot = history.getSlot();
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

    public void setUser(User user)
    {
        if(user != null)
        {
            UserResponseDto dto = new UserResponseDto(user);
            dto.setsAccount(user.getUserAccount());
            this.user = dto;
        }
    }

    public void setAll(ThrifterHistory history)
    {
        this.setThrift(history.getThrift());
        this.setUser(history.getUser());
    }
}
