package com.example.demo.Dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class HubPageDto extends PageResponseDto
{
    private List<Thrift_hubResponseDto> items;

    public HubPageDto(boolean hasNext, int pageSize, List<Thrift_hubResponseDto> items )
    {
        this.setHasNext(hasNext);
        this.setPageSize(pageSize);
        this.items = items;
    }
}
