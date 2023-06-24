package com.example.demo.Dtos;

import com.example.demo.Model.ThrifterHistory;
import lombok.Getter;


@Getter
public class MemberResponseDto extends ResponseDto
{
    private String fname;

    private String lname;

    private String email;

    private String joinedOn;

    private int slot;

    public MemberResponseDto(ThrifterHistory thrifterHistory)
    {
        this.slot = thrifterHistory.getSlot();
        this.fname = thrifterHistory.getUser().getFname();
        this.lname = thrifterHistory.getUser().getLname();
        this.email = thrifterHistory.getUser().getEmail();
        this.joinedOn = this.getStringDateTime(thrifterHistory.getCreated_on());
    }
}
