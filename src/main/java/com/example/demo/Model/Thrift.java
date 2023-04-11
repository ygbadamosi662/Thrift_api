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
import java.util.*;

@Getter
@Setter
//@RequiredArgsConstructor
@Entity
public class Thrift extends Beneficiary
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
    @Nullable
    private User collector;

    @Nullable
    private long per_term_amnt;

    private long collection_amount;

    private int slots = 0;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

    private LocalDate thrift_start;

    private LocalDate thrift_end;

    @Column(nullable = true)
    private long thrift_index;

    @Column(nullable = true)
    private LocalDate next_thrift_date;

    private long collection_available;

    @Enumerated(EnumType.STRING)
    private Lifecycle cycle;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "acc_id")
    private Account thriftAccount;

    public Thrift(){}

    public Thrift(Thrift thrift)
    {
        this.setsClassName();
    }

    public boolean update_next_thrift_date()
    {
//        updates this.next_thrift_date,returns true if an update happens
//        and returns false if no update happens
        LocalDate now = LocalDate.now();

        if(this.getNext_thrift_date().isBefore(now))
        {
            Map<String, Integer> termToLong = new HashMap<>();
            termToLong.put(Term.WEEKLY.name(), 1);
            termToLong.put(Term.BI_WEEKLY.name(), 2);
            termToLong.put(Term.MONTHLY.name(), 4);

            termToLong.forEach((key, val)-> {
                if(this.getTerm().name().equals(key))
                {
                    this.setNext_thrift_date(this.getNext_thrift_date().plusWeeks(val));
                }
            });

            return true;
        }
        else
        {
            return false;
        }
    }

    public void update_index()
    {
        try
        {
            this.setThrift_index(this.getThrift_index() + 1);
        }
        catch(NullPointerException e)
        {
            System.out.println("index is null");
        }

    }

    public void update()
    {
        if(this.update_next_thrift_date())
        {
            this.update_index();
        }
    }

    public void setsClassName()
    {
        this.setClassName(Thrift.class.getSimpleName());
    }

}
