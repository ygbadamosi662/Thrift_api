package com.example.demo.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddThrifterDto
{
    private String email = "none";

    private String ticket;

    private int slot;

}
