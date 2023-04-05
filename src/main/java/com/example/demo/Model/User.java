package com.example.demo.Model;

import com.example.demo.Enums.Gender;
import com.example.demo.Enums.Role;
import com.example.demo.Repositories.AccountRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Getter
@Setter
//@RequiredArgsConstructor
@Entity
public class User extends Beneficiary implements UserDetails
{
//    get and set thrift_list with gettingThrift_list() and settimgThrift_list() respectively
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name="user_id")
    private long id;

    @Column(nullable = true)
    private String thrift_list;

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
    
//    private long acc_id = 0;

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

    public void settingThriftList (List<Long> longList)
    {
        if(this.thrift_list == null)
        {
            this.thrift_list = longList.get(0)+"";
            if(longList.size() > 1)
            {
                longList.remove(0);
                this.settingThriftList(longList);
            }
        }
        else
        {
            for (int i = 0; i < longList.size(); i++)
            {
                this.thrift_list = this.thrift_list + "_" + longList.get(i)+"";
            }
//
        }
    }

    public List<Long> gettingThrift_list ()
    {
        List<Long> list = new ArrayList<>();
        int str_len = this.thrift_list.length();
        char[] thrifts_in_char = this.thrift_list.toCharArray();
        String str = "";
        char deli = '_';

        for (int i = 0; i < str_len; i++)
        {
            if( thrifts_in_char[i] == deli  )
            {
                list.add(Long.valueOf(str));
                str = "";
            }
            else
            {
                str = str + Character.toString(thrifts_in_char[i]);

                if(str_len == i + 1)
                {
                    list.add(Long.valueOf(str));
                    str = "";
                }
            }
        }

        return list;
    }

    public void setsClassName()
    {
        this.setClassName(User.class.getSimpleName());
    }

//    public Map<String, String> getHash()
//    {
//
//    }

    @Override
    public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
