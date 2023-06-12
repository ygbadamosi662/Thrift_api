package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class JwtBlacklist
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, length = 500)
    private String jwt;

    @CreationTimestamp
    private LocalDateTime blacklisted_on;

    public JwtBlacklist(){}

    public JwtBlacklist(String jwt)
    {
        this.jwt = jwt;
    }

}
