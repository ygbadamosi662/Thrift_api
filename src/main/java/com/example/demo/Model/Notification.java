package com.example.demo.Model;

import com.example.demo.Enums.Howfar;
import com.example.demo.Enums.Permissions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Notification
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="notification_id")
    private Long id;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    private String note;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "subject_id")
    private Thrift subject;

    @Enumerated(EnumType.STRING)
    private Permissions permit;

    @Enumerated(EnumType.STRING)
    private Howfar howfar;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

    public Notification(){}

    public Notification(User sender, User receiver, Thrift subject,
                        String note, Permissions permit, Howfar howfar)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.note = note;
        this.permit = permit;
        this.howfar = howfar;
    }
}
