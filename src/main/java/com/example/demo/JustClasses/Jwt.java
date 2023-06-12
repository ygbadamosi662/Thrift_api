package com.example.demo.JustClasses;

import com.example.demo.Model.JwtBlacklist;
import com.example.demo.Model.User;
import com.example.demo.Repositories.JwtBlacklistRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.JwtService;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@RequiredArgsConstructor
@Getter
public class Jwt
{
    private String jwt = "";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtBlacklistRepository jwtBlacklistRepo;


    public Jwt(String jwt)
    {
        this.jwt = jwt;
    }

    public String setJwt(HttpServletRequest req)
    {
//        also returns the jwt
        if(req != null)
        {
            this.jwt = req.getHeader("Authorization").substring("Bearer ".length());
        }

        return this.jwt;
    }

    public User giveUser()
    {
        return this.jwt.isEmpty()? null:
                userRepository.findByEmail(this.jwtService.getSubject(this.jwt)).get();
    }

    public boolean is_cancelled(String jw)
    {
//        returns true if jwt has been blacklisted;
        try
        {
            Optional<JwtBlacklist> cancelled = jwtBlacklistRepo.findByJwt(jw);
            if(cancelled.isPresent())
            {
                System.out.println(cancelled.get());
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (NoResultException e)
        {
            return false;
        }
    }
}
