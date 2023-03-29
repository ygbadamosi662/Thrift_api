package com.example.demo.Dtos;

import com.example.demo.Enums.Gender;
import com.example.demo.Enums.Role;
import com.example.demo.Model.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class UserResponseDto extends ResponseDto
{

    private String thrift_list;

    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    private String fname;

    private String lname;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String email;

    private String phone;

    private User_Acc_DetailsResponseDto user_acc_details;

    public UserResponseDto(){}

    public UserResponseDto(User user)
    {
        this.thrift_list = user.getThrift_list();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.fname = user.getFname();
        this.lname = user.getLname();
        this.gender = user.getGender();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.user_acc_details = new User_Acc_DetailsResponseDto(user.getUser_acc_details());
    }
}
