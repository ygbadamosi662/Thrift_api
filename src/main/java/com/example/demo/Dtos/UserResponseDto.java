package com.example.demo.Dtos;

import com.example.demo.Enums.Gender;
import com.example.demo.Enums.Role;
import com.example.demo.Model.*;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
public class UserResponseDto extends ResponseDto
{
    @Enumerated(EnumType.STRING)
    private Role role;

    private String fname;

    private String lname;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String email;

    private String phone;

    private AccountResponseDto account;

    public UserResponseDto(){}

    public UserResponseDto(User user)
    {
        this.role = user.getRole();
        this.fname = user.getFname();
        this.lname = user.getLname();
        this.gender = user.getGender();
        this.email = user.getEmail();
        this.phone = user.getPhone();
    }

    public void setsAccount(Account acc)
    {
        if(!(acc == null))
        {
            this.account = new AccountResponseDto(acc);
        }

    }
}
