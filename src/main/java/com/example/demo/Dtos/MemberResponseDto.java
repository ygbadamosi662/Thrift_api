package com.example.demo.Dtos;

import com.example.demo.Model.ThrifterHistory;
import lombok.Getter;


@Getter
public class MemberResponseDto extends ResponseDto
{
    private UserResponseDto member;

    private int slot;

    public MemberResponseDto(ThrifterHistory thrifterHistory)
    {
        this.slot = thrifterHistory.getSlot();
    }

    public void setMember(ThrifterHistory thrifterHistory)
    {
        this.member = new UserResponseDto(thrifterHistory.getUser());
        this.member.setsAccount(thrifterHistory.getUser().getAccount());
    }
}
