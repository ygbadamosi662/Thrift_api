package com.example.demo.Controllers;

import com.example.demo.Dtos.*;
import com.example.demo.Enums.Role;
import com.example.demo.Enums.Side;
import com.example.demo.JustClasses.Jwt;
import com.example.demo.Model.Account;
import com.example.demo.Model.JwtBlacklist;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.User;
import com.example.demo.Repositories.AccountRepository;
import com.example.demo.Repositories.JwtBlacklistRepository;
import com.example.demo.Repositories.ThriftsRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.BankService;
import com.example.demo.Services.JwtService;
import com.example.demo.Utilities.Utility;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController
{

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final Jwt jwtObj;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserRepository userRepository;

    private final JwtBlacklistRepository jwtBlacklistRepo;

    private final AccountRepository accRepo;

    private final BankService bankServe;

    private final Utility util;

    @PostMapping("/home")
    public ResponseEntity<?> mean ()
    {
        return ResponseEntity.ok("Welcome to Thrift_api, whats up?");
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto user)
    {
        System.out.println("hey hey");
        Optional<User> chk_chk = userRepository.findByEmail( user.getEmail());

        if(chk_chk.isPresent())
        {
            return new ResponseEntity<>("user already exists", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User save = userRepository.save(user.getUser());
        System.out.println(save);
        Map <String, Object> extraClaims = setExtraClaims(save);
        String jwt = jwtService.generateJwt(save,extraClaims);

//        Map <String, Object> accessToken = new HashMap<>();
//        accessToken.put("access token", jwt);
//        accessToken.put("message","Registration succesful");

        UserResponseDto resDto = new UserResponseDto(save);
        resDto.setJwt(jwt);


        return ResponseEntity.ok(resDto);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(@Valid HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        try
        {
            JwtBlacklist cancelled = new JwtBlacklist();
            cancelled.setJwt(jwt);
            jwtBlacklistRepo.save(cancelled);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User is successfully signed out", HttpStatus.OK);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto login)
    {
        Optional<User> chk_chk = userRepository.findByEmail(login.getEmail());

        if(!chk_chk.isPresent())
        {
            return new ResponseEntity<>("email or password incorrect", HttpStatus.BAD_REQUEST);
        }

        User user = chk_chk.get();

        UsernamePasswordAuthenticationToken token =new UsernamePasswordAuthenticationToken(
                login.getEmail(),
                login.getPassword(),
                user.getAuthorities()
        );

        authenticationManager.authenticate(token);
        Map <String,Object> xtraClaims = setExtraClaims(user);
        String jwt = jwtService.generateJwt(user,xtraClaims);

//        HttpHeaders header = new HttpHeaders();
//        header.add("Authorization", "Bearer " + jwt);

        UserResponseDto dto = new UserResponseDto(user);
        dto.setJwt(jwt);
        dto.setsAccount(user.getAccount());
        Map<String, Map<String, Double>> all = bankServe.ActiveToInactiveInfo();

        if(!(all == null))
        {
            if(user.getRole().equals(Role.ADMIN) &&
                    (all.get("inactive").get("percentage") < 50))
            {
                dto.setMore_info("Available accounts is low");
            }
        }


        return ResponseEntity.ok(dto);
    }

    @PostMapping("addAcc")
    public ResponseEntity<?> addAcc(@Valid @RequestBody AddAccDto dto, HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }

        User user = jwtObj.giveUser();

        Account acc = new Account(dto);
        acc.setSide(Side.USER);
        accRepo.save(acc);
        System.out.println(acc.getId());

        if(user.getAccount() != null)
        {
            Account formerAcc = user.getAccount();
            accRepo.delete(accRepo.findById(formerAcc.getId()).get());
        }
        user.setAccount(acc);

        acc.setsBen();
        AccountResponseDto resDto = new AccountResponseDto();
        resDto.setsBen(acc);

        return ResponseEntity.ok(resDto);
    }

    private static Map<String, Object> setExtraClaims(User user)
    {
        Map <String, Object> extraClaims = new HashMap<>();
        extraClaims.put("fname",user.getFname());
        extraClaims.put("lname",user.getLname());
        extraClaims.put("email",user.getEmail());

        return extraClaims;
    }
}
