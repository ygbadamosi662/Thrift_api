package com.example.demo.Model;

import com.example.demo.Enums.Consent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ThrifterHistory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="history_id")
    private long id;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "thrift_id")
    private Thrift thrift;

    @CreationTimestamp
    private LocalDateTime completed_on;
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Consent consent = Consent.YELLOW;

}
