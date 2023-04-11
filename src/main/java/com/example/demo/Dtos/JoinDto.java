package com.example.demo.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinDto
{
    private String jwt;

    private String ticket;

    private int slot;
}
