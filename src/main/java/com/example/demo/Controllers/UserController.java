package com.example.demo.Controllers;

import com.example.demo.Dtos.LoginDto;
import com.example.demo.Dtos.UserDto;
import com.example.demo.Dtos.UserResponseDto;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.User;
import com.example.demo.Repositories.ThriftsRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController
{
//    create a virtual bank account generator,name of account being thrift string id
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PostMapping("/home")
    public String mean ()
    {
        return "I got a whole new chick,Mean!";
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto user)
    {
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

        Map <String, Object> accessToken = new HashMap<>();
        accessToken.put("access token", jwt);
        accessToken.put("message","Registration succesful");

        return ResponseEntity.ok(accessToken);
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
        Map <String,Object> accessToken = new HashMap<>();
        accessToken.put("jwt",jwt);
        accessToken.put("msg","Login succesful");
        UserResponseDto dto = new UserResponseDto(user);
        dto.setJwt(jwt);
        dto.setAccount(user.getUserAccount());
        return ResponseEntity.ok(dto);
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
