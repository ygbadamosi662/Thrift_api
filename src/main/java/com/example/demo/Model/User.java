package com.example.demo.Model;

import com.example.demo.Enums.Gender;
import com.example.demo.Enums.Role;
import com.example.demo.JustClasses.Beneficiary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Entity
public class User extends Beneficiary implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( name="user_id")
    private long id;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String fname;

    private String lname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @OneToMany(mappedBy = "organizer")
    private List<Thrift> thrift;

    @OneToMany(mappedBy = "collector")
    private List<Thrift> collector;

    @OneToMany(mappedBy = "user")
    private List<ThrifterHistory> thriftersHistoryList;


    @OneToMany(mappedBy = "collector")
    private List<ThePot> collectorPot;

    @OneToMany(mappedBy = "user")
    private List<Thrift_hub> thrift_hub;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "acc_id")
    private Account userAccount;

    @CreationTimestamp
    private LocalDateTime created_on;

    @UpdateTimestamp
    private LocalDateTime updated_on;

    public User(){}

    public User(User user)
    {
//        this.setsAccount();
        this.setsClassName();
    }


    public void setsClassName()
    {
        this.setClassName(User.class.getSimpleName());
    }

    @Override
    public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }

    @Override
    public String getUsername()
    {
        return email;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
