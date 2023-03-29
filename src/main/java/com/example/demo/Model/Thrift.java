package com.example.demo.Model;

import com.example.demo.Enums.Lifecycle;
import com.example.demo.Enums.Term;
import com.example.demo.Repositories.ThrifterHistoryRepository;
import com.example.demo.Repositories.ThriftsRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Thrift
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="thrift_id")
    private long id;

    private String thriftName;

    private String ticket;

    @Enumerated(EnumType.STRING)
    public Term term;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @OneToMany(mappedBy = "thrift")
    private List<Thrift_hub> thrift_hub;

    @OneToMany(mappedBy = "thrift")
    private List<ThrifterHistory> thrifter_history;

    @OneToMany(mappedBy = "thrift")
    private List<ThePot> collection;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "collector_id")
    private User collector;

    @Nullable
    private long per_term_amnt;

    private long collection_amount;

    @Column(nullable = true)
    private long no_of_thrifters;

    @OneToOne(mappedBy = "thrift")
    private Generated_account generated_account;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

    private LocalDate thrift_start;

    private LocalDate thrift_end;

//    write a setter for this property
    @Column(nullable = true)
    private long collection_index;

    @Column(nullable = true)
    private LocalDate next_thrift_date;

    @Column(nullable = true)
    private long collection_available;

    @Enumerated(EnumType.STRING)
    private Lifecycle cycle;

}
