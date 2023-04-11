package com.example.demo.Dtos;

import com.example.demo.Enums.Account_type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AddAccDto
{
    @NotBlank
    @NotNull
    private String bank;

    @NotBlank
    @NotNull
    @Size(min=10,max=10)
    private String accNum;

    @NotBlank
    @NotNull
    private String accName;

    @Enumerated(EnumType.STRING)
    private Account_type type;

}
