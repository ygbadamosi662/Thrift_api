package com.example.demo.Dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageResponseDto
{
    private boolean hasNext;

    private int pageSize;

    private boolean hasPrevious;
}
