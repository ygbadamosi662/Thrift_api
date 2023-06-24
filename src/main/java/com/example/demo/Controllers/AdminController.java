package com.example.demo.Controllers;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.example.demo.Dtos.*;
import com.example.demo.Enums.Consent;
import com.example.demo.Enums.Lifecycle;
import com.example.demo.Enums.Role;
import com.example.demo.Enums.Side;
import com.example.demo.JustClasses.Jwt;
import com.example.demo.Model.*;
import com.example.demo.Repositories.*;
import com.example.demo.Services.BankService;
import com.example.demo.Services.JwtService;
import com.example.demo.Services.SomeService;
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
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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


    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final ThriftsRepository thriftsRepository;

    private final SomeService serve;

    private final BankService bankServe;

    @Autowired
    private Jwt jwtObj;


    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@Valid @RequestParam int page,@RequestParam String role,
                                         HttpServletRequest request)
    {
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        List<String> roles = new ArrayList<>();
        roles.add(Role.THRIFTER.name());
        roles.add(Role.ADMIN.name());
        if(!roles.contains(role))
        {
            return new ResponseEntity<>("No such user", HttpStatus.BAD_REQUEST);
        }

        Pageable pageable = PageRequest.of(page-1, 20);
        Slice<User> users = userRepository.findByRole(Role.valueOf(role), pageable);

        List<UserResponseDto> listDto = new ArrayList<>();
        if(users.isEmpty())
        {
            System.out.println("no mans land");
            return new ResponseEntity<>("No user found", HttpStatus.BAD_REQUEST);
        }

        users.forEach((one) ->{
            UserResponseDto dto = new UserResponseDto(one);
            dto.setsAccount(one.getAccount());
            listDto.add(dto);
        });
        System.out.println("got here");
        return new ResponseEntity<>(listDto, HttpStatus.OK);
    }

    @PostMapping("/thrifts")
    public ResponseEntity<?> getAllThrifts(@Valid @RequestParam int page, HttpServletRequest request)
    {
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

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

    @PostMapping("/thrift")
    public ResponseEntity<?> getThrift(@Valid @RequestParam String ticket, HttpServletRequest request)
    {
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(ticket);
        if (byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift does not exist", HttpStatus.BAD_REQUEST);
        }

        ThriftResponseDto resDto = new ThriftResponseDto(byTicket.get());
        resDto.setAllWeirdAssClasses(byTicket.get());

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @PostMapping("/paymenttHisory")
    public ResponseEntity<?> getPayHistory(@Valid @RequestParam String ticket,
                                              HttpServletRequest request)
    {
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(ticket);
        if (byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift does not exist", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        List<Thrift_hub> hubs = hubRepo.findByThrift(thrift);
        List<Thrift_hubResponseDto> dtos = new ArrayList<>();
        hubs.forEach((hub)-> {
            Thrift_hubResponseDto dto = new Thrift_hubResponseDto(hub);
            dto.setAll(hub);
            dtos.add(dto);
        });

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping("/potHistory")
    public ResponseEntity<?> getPotHistory(@Valid @RequestParam String ticket,
                                           HttpServletRequest request)
    {
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(ticket);
        if (byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift does not exist", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        List<ThePot> pots = potRepo.findByThrift(thrift);
        List<ThePotResponseDto> dtos = new ArrayList<>();
        pots.forEach((pot)-> {
            ThePotResponseDto dto = new ThePotResponseDto(pot);
            dto.setAll(pot);
            dtos.add(dto);
        });

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping("/organizers")
    public ResponseEntity<?> getAllOrgs(@Valid @RequestParam int page,
                                           HttpServletRequest request)
    {
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        Pageable pageable = PageRequest.of(page-1, 50);
        List<UserResponseDto> dtos = new ArrayList<>();
        serve.getAllOrganizers(pageable).forEach((org)-> {
            UserResponseDto dto = new UserResponseDto(org);
            dto.setsAccount(org.getAccount());
            dtos.add(dto);
        });

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping("user")
    public ResponseEntity<?> getUser(@Valid @RequestBody AllThriftDto dto, HttpServletRequest request)
    {
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        User reqUser = new User();
        UserResponseDto resDto = new UserResponseDto();

        if(dto.getEmail().equals("none"))
        {
            Optional<User> byId = userRepository.findById(dto.getId());
            if(byId.isEmpty())
            {
                return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
            }
            reqUser = byId.get();
        }

        if(dto.getId() == null)
        {
            Optional<User> byEmail = userRepository.findByEmail(dto.getEmail());
            if(byEmail.isEmpty())
            {
                return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
            }
            reqUser = byEmail.get();
        }

        try
        {
            resDto = new UserResponseDto(reqUser);
            resDto.setsAccount(reqUser.getAccount());
        }
        catch (NullPointerException e)
        {
            System.out.println("user is null, probably did not pass any parameter");
        }

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @PostMapping("accounts")
    public ResponseEntity<?> getAccounts(@Valid @RequestParam int page, String side,
                                         HttpServletRequest request)
    {
//        The String side param should be a selection html tag of options USER,ACTIVE,INACTIVE
//        on the frontend
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        List<String> sides = new ArrayList<>();
        sides.add(Side.ACTIVE.name());
        sides.add(Side.INACTIVE.name());
        sides.add(Side.USER.name());


        if(!sides.contains(side))
        {
            return new ResponseEntity<>("No such accounts", HttpStatus.BAD_REQUEST);
        }

        List<Account> accs = bankServe.getAccounts(page, Side.valueOf(side));
        List<AccountResponseDto> dtos = new ArrayList<>();
        accs.forEach((acc)-> {
            AccountResponseDto dto = new AccountResponseDto(acc);
            dto.setsBen(acc);
            dtos.add(dto);
        });

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping("accInfo")
    public ResponseEntity<?> getAccInfo(@Valid HttpServletRequest request)
    {
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        return new ResponseEntity<>(bankServe.ActiveToInactiveInfo(), HttpStatus.OK);
    }

    @PostMapping("logAcc")
    public ResponseEntity<?> logAcc(@Valid @RequestBody LogAccDto dto, HttpServletRequest request)
    {
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        AccountResponseDto resDto = new AccountResponseDto(bankServe.logAcc(dto));

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @PostMapping("accHistory")
    public ResponseEntity<?> accHistory(@Valid @RequestParam Long id, HttpServletRequest request)
    {
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        ResponseDto backupDto = new ResponseDto();
        List<ThriftResponseDto> resDtos = new ArrayList<>();

        try
        {
            bankServe.getBens(id).forEach((thrift)-> {
                ThriftResponseDto dto = new ThriftResponseDto(thrift);
                dto.setAllWeirdAssClasses(thrift);
                resDtos.add(dto);
            });
        }
        catch (NullPointerException e)
        {
            backupDto.setMore_info("Account does not exist");
        }
        finally
        {
            if(resDtos.isEmpty())
            {
                return new ResponseEntity<>(backupDto, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(resDtos, HttpStatus.OK);
        }
    }

    @PostMapping("allHouseAsign")
    public ResponseEntity<?> allHouseAssing(@Valid HttpServletRequest request)
    {
//        assigns account to thrifts with no account according to the availabilty of accounts
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        Map<String, List<Thrift>> all = bankServe.afterHours();
        if(all == null)
        {
            return new ResponseEntity<>("All Thrifts have an assigned account",
                    HttpStatus.BAD_REQUEST);
        }

        Map<String, List<ThriftResponseDto>> res = new HashMap<>();
        res.put("asigned", new ArrayList<>());
        res.put("unasigned", new ArrayList<>());

        if (!all.get("asigned").isEmpty())
        {
            all.get("asigned").forEach((thrift)-> {
                ThriftResponseDto dto = new ThriftResponseDto(thrift);
                dto.setAllWeirdAssClasses(thrift);
                res.get("asigned").add(dto);
            });
        }

        if (!all.get("unasigned").isEmpty())
        {
            all.get("unasigned").forEach((thrift)-> {
                ThriftResponseDto dto = new ThriftResponseDto(thrift);
                dto.setAllWeirdAssClasses(thrift);
                res.get("unasigned").add(dto);
            });
        }

        if (res.get("unasigned").isEmpty())
        {
            return new ResponseEntity<>("No more Thrifts with no account",
                    HttpStatus.BAD_REQUEST);
        }

        if (!res.containsKey("asigned"))
        {
            return new ResponseEntity<>("No accounts to assign",
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("getUnasignedThrifts")
    public ResponseEntity<?> getUnasignedThrifts(@Valid @RequestParam int page,
                                                 HttpServletRequest request)
    {
        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        List<ThriftResponseDto> dtos = new ArrayList<>();

        try
        {
            bankServe.getUnasigned(page, 50).forEach((thrift)-> {
                ThriftResponseDto dto = new ThriftResponseDto(thrift);
                dto.setAllWeirdAssClasses(thrift);
                dtos.add(dto);
            });
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return new ResponseEntity<>("All thrift has been asigned an account",
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

}
