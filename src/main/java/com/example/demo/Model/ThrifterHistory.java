package com.example.demo.Model;

import com.example.demo.Dtos.ResponseDto;
import com.example.demo.Enums.Consent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ThrifterHistory extends ResponseDto
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="history_id")
    private long id;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "thrift_id")
    private Thrift thrift;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private int slot;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;



    @Enumerated(EnumType.STRING)
    private Consent consent = Consent.YELLOW;

}
