package com.example.demo.Controllers;

import com.example.demo.Dtos.CreateThriftDto;
import com.example.demo.Dtos.ThriftResponseDto;
import com.example.demo.Dtos.UserResponseDto;
import com.example.demo.Enums.Consent;
import com.example.demo.Enums.Lifecycle;
import com.example.demo.Model.Account;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.ThrifterHistory;
import com.example.demo.Model.User;
import com.example.demo.Repositories.*;
import com.example.demo.Services.JwtService;
import com.example.demo.Utilities.Utility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Setter
@Getter
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController
{
    private final ThrifterHistoryRepository historyRepository;

    private final AccountRepository accRepo;

    private final TransactionRepository transRepo;

    private final Thrift_hubRepository hubRepo;

    private final ThePotRepository potRepo;

    @Autowired
    private Utility util;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final ThriftsRepository thriftsRepository;


    @PostMapping("/users")
    public ResponseEntity<?> getAllUsers(@Valid @RequestParam int page, HttpServletRequest request)
    {
        String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        System.out.println("jwt expires: " + jwtService.getExpiration(jwt));

        Optional<User> byEmail = userRepository.findByEmail(jwtService.getSubject(jwt));
        User user = byEmail.get();

        Pageable pages = PageRequest.of(page, 50);

        Page<User> users = userRepository.findAll(pages);
//        List<User> all = users.toList();
        List<UserResponseDto> listDto = new ArrayList<>();
        if(users.isEmpty())
        {
            return new ResponseEntity<>(listDto, HttpStatus.OK);
        }

        users.forEach((one) ->{
            UserResponseDto dto = new UserResponseDto(one);
            dto.setsAccount(one.getUserAccount());
            listDto.add(dto);
        });

        return new ResponseEntity<>(listDto, HttpStatus.OK);
    }

    @PostMapping("/Thrifts")
    public ResponseEntity<?> getAllThrifts(@Valid @RequestParam int page, HttpServletRequest request)
    {
        String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        System.out.println("jwt expires: " + jwtService.getExpiration(jwt));

        Optional<User> byEmail = userRepository.findByEmail(jwtService.getSubject(jwt));
        User user = byEmail.get();

        Pageable pages = PageRequest.of(page, 50);

        Page<Thrift> thrifts = thriftsRepository.findAll(pages);
//        List<User> all = users.toList();
        List<ThriftResponseDto> listDto = new ArrayList<>();
        if(thrifts.isEmpty())
        {
            return new ResponseEntity<>(listDto, HttpStatus.OK);
        }

        thrifts.forEach((one) ->{
            ThriftResponseDto dto = new ThriftResponseDto(one);
            dto.setAllWeirdAssClasses(one);
            listDto.add(dto);
        });

        return new ResponseEntity<>(listDto, HttpStatus.OK);
    }

}
