package com.example.demo.Dtos;

import com.example.demo.Model.User;
import com.example.demo.Enums.Gender;
import com.example.demo.Enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto
{
    @Size(min=3,max=25)
    @NotNull
    @NotBlank
    private String fname;

    @Size(min=3,max=25)
    @NotNull
    @NotBlank
    private String lname;

    @Size(min = 8,max = 15)
    @NotBlank
    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Size(min=11) @Email(message = "Must follow this pattern 'dfshghf@hdfgf.com'",
            regexp = "[a-z]{2,10}@[a-z]{2,10}\\.[a-z]{2,10}")
    private String email;

    @NotNull
    @NotBlank
    @Size(min=10,max=10)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.THRIFTER;

    public User getUser ()
    {
        User user = new User();
        user.setEmail(this.email);
        user.setFname(this.fname);
        user.setLname(this.lname);
        user.setRole(this.role);
        user.setPassword(this.password);
        user.setGender(this.gender);
        user.setPhone(this.phone);

        return user;
    }
}
