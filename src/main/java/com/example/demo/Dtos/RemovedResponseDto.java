package com.example.demo.Dtos;

import com.example.demo.Model.ThrifterHistory;

public class RemovedResponseDto extends ResponseDto
{
    private String removed;

    private String from;

    private int slot;

    public RemovedResponseDto(ThrifterHistory isto)
    {
        this.removed = isto.getUser().getEmail();
        this.from = isto.getThrift().getTicket();
        this.slot = isto.getSlot();
    }
}
