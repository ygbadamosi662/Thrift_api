package com.example.demo.Dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PotPageDto extends ResponseDto
{
    private boolean hasNext;

    private int pageSize;

    private List<ThePotResponseDto> items;

    public PotPageDto(boolean hasNext, int pageSize, List<ThePotResponseDto> items )
    {
        this.hasNext = hasNext;
        this.pageSize = pageSize;
        this.items = items;
    }
}
