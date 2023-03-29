package com.example.demo.Controllers;

import com.example.demo.Dtos.*;
import com.example.demo.Enums.Consent;
import com.example.demo.Enums.Lifecycle;
import com.example.demo.Model.ThrifterHistory;
import com.example.demo.Model.Thrift;
import com.example.demo.Model.User;
import com.example.demo.Repositories.ThrifterHistoryRepository;
import com.example.demo.Repositories.ThriftsRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.JwtService;
import com.example.demo.Utilities.Utility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;

@Setter
@Getter
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/thrift")
public class ThriftController
{
    private int multi;

    private User user;

//    private HashMap<String, Object> data_res;

    private final ThrifterHistoryRepository historyRepository;

    private List<User> Thrifters;

    @Autowired
    private Utility util;
    
    private long member_limit;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final ThriftsRepository thriftsRepository;


    @PostMapping("/create")
    public ResponseEntity<?> createThrift(@Valid @RequestBody CreateThriftDto create,HttpServletRequest request)
    {
        String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        System.out.println("jwt expires: " + jwtService.getExpiration(jwt));

        Optional<User> chk_chk = userRepository.findByEmail(jwtService.getSubject(jwt));
        User organizer = chk_chk.get();
        Map<String, Object> check = util.chk_user_limit(organizer);
        boolean good = (Boolean) check.get("good?");
        if(good == false)
        {
            Map<String, Integer> info = (HashMap) check.get("info");
            List<Thrift> more_info = (ArrayList) check.get("more_info");
            return new ResponseEntity<>("User at limit,info incoming", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = create.getThrift();

        if(!(util.duration_chk(thrift)))
        {
            return new ResponseEntity<>(
                    "Every Thrift must end within a yaer,too much members or too short term",
                    HttpStatus.BAD_REQUEST);
        }

        thrift.setThrift_end(util.get_thrift_end(thrift));
        thrift.setOrganizer(organizer);
        thrift.setCollection_amount(util.collectionCalc(thrift));
        thrift.setCollection_index(0);
        thrift.setCollection_available(0);
        Lifecycle cycle = Lifecycle.AWAITING;
        thrift.setCycle(cycle);
        thriftsRepository.save(thrift);

        Thrift update = thriftsRepository.findById(thrift.getId()).get();
        update.setTicket(thrift.getThriftName() + "." + ""+thrift.getId());

        update = thriftsRepository.save(update);

        organizer.settingThriftList(new ArrayList<>(Arrays.asList(thrift.getId())));
        organizer = userRepository.save(organizer);
        ThrifterHistory history = new ThrifterHistory();
        history.setThrift(thrift);
        history.setUser(organizer);
        Consent con = Consent.GREEN;
        history.setConsent(con);
        history = historyRepository.save(history);

        System.out.println(organizer.getThrift_list());


        return new ResponseEntity<>(new ThriftResponseDto(thrift), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addThrifter(@Valid @RequestBody AddThrifterDto add, HttpServletRequest req)
    {
        boolean exist_in = false;
        List<Long> longList = new ArrayList<>();


        Optional<User> byEmail = userRepository.findByEmail(add.getEmail());

        if(!(byEmail.isPresent()))
        {
            return new ResponseEntity<>("User must be registred to per-take in thrift", HttpStatus.BAD_REQUEST);
        }

        User thrifter = byEmail.get();

//        checking if thrifter has reached participation limit
        Map<String, Object> check = util.chk_user_limit(thrifter);
        boolean good = (Boolean) check.get("good?");
        if(good == false)
        {
            Map<String, Integer> info = (HashMap) check.get("info");
            List<Thrift> more_info = (ArrayList) check.get("more_info");
            return new ResponseEntity<>("User at limit,info incoming", HttpStatus.BAD_REQUEST);
        }

        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        Optional<User> organizer_byEmail =  userRepository.findByEmail(jwtService.getSubject(jwt));
        User organizer = organizer_byEmail.get();
        Thrift thrift = thriftsRepository
                .findByOrganizerAndThriftName(organizer, add.getThrift_name())
                .get();

//        checking if user is already part of thrift
        if(!(thrifter.getThrift_list() == null))
        {
            List <Thrift> thrift_list= util.get_thrifts(thrifter);


            for (int j = 0; j < thrift_list.size(); j++) {
                if(thrift.getId() == thrift_list.get(j).getId())
                {
                    System.out.println("we exist");
                    exist_in = true;
                }
            }
        }

        if(exist_in == false)
        {
            longList.add(thrift.getId());
            thrifter.settingThriftList(longList);
//            List<ThrifterHistory> historyList = util.history_dump(thrifter);
            ThrifterHistory history = new ThrifterHistory();
            history.setThrift(thrift);
            history.setUser(thrifter);
            Consent con = Consent.OYELLOW;
            history.setConsent(con);
            history = historyRepository.save(history);
        }
        else
        {
            return new ResponseEntity<>("Member already added", HttpStatus.BAD_REQUEST);
        }

        System.out.println("we end");
        return ResponseEntity.ok("thrifter added succesfully");
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinThrift(@Valid @RequestBody JoinDto joint, HttpServletRequest req)
    {
        boolean exist_in = false;
        List<Long> longList = new ArrayList<>();

        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());
        Optional<User> byEmail = userRepository.findByEmail(jwtService.getSubject(jwt));
        User thrifter = byEmail.get();

        //        checking if thrifter has reached participation limit
        Map<String, Object> check = util.chk_user_limit(thrifter);
        boolean good = (Boolean) check.get("good?");
        if(good == false)
        {
            Map<String, Integer> info = (HashMap) check.get("info");
            List<Thrift> more_info = (ArrayList) check.get("more_info");
            return new ResponseEntity<>("User at limit,info incoming", HttpStatus.BAD_REQUEST);
        }

        Thrift thrift = thriftsRepository.findByTicket(joint.getTicket()).get();

        if(!(thrifter.getThrift_list() == null))
        {
            List <Thrift> thrift_list = util.get_thrifts(thrifter);


            for (int j = 0; j < thrift_list.size(); j++) {
                if(thrift.getId() == thrift_list.get(j).getId())
                {
                    exist_in = true;
                }
            }
        }

        if(exist_in == false)
        {
            longList.add(thrift.getId());
            thrifter.settingThriftList(longList);
//            List<ThrifterHistory> historyList = util.history_dump(thrifter);
            userRepository.save(thrifter);
            ThrifterHistory history = new ThrifterHistory();
            history.setThrift(thrift);
            history.setUser(thrifter);
            Consent con = Consent.TYELLOW;
            history.setConsent(con);
            history = historyRepository.save(history);

            return ResponseEntity.ok("member added succesfully");

        }
        else
        {
            return new ResponseEntity<>("Member already added", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptThrift(@Valid @RequestBody AcceptDto ticket, HttpServletRequest req)
    {
        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());
        String info = "";

        Optional<User> chk_chk = userRepository.findByEmail(jwtService.getSubject(jwt));
        User thrifter= chk_chk.get();
        Optional<Thrift> byTicket = thriftsRepository.findByTicket(ticket.getTicket());

        if(!(byTicket.isPresent()))
        {
            return new ResponseEntity<>("thrift dont exist", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();
//        User oo = thrift.getOrganizer();

//        if organizer
        if(thrift.getOrganizer().equals(thrifter))
        {

            Optional<User> byMail = userRepository.findByEmail(ticket.getEmail());
            if(!(byMail.isPresent()))
            {
                return new ResponseEntity<>("user dont exist", HttpStatus.BAD_REQUEST);
            }

            User user = byMail.get();

            Optional<ThrifterHistory> byTwo = historyRepository.findByThriftAndUser(thrift, user);
            if(!(byTwo.isPresent()))
            {
                return new ResponseEntity<>("user have retracted their interest", HttpStatus.BAD_REQUEST);
            }

            Consent con = Consent.GREEN;
            ThrifterHistory istory = byTwo.get();
            if(istory.getConsent().name().equals("TYELLOW"))
            {
                istory.setConsent(con);
                istory = historyRepository.save(istory);

                info = "user request accepted";
                System.out.println("hey hey");
            }


        }
        else if (!(thrift.getOrganizer().equals(thrifter)))
        {
            Optional<ThrifterHistory> byTwo = historyRepository.findByThriftAndUser(thrift, thrifter);
            if(!(byTwo.isPresent()))
            {
                return new ResponseEntity<>("user have retracted their interest", HttpStatus.BAD_REQUEST);
            }

            Consent con = Consent.GREEN;
            ThrifterHistory istory = byTwo.get();
            if(istory.getConsent().name().equals("OYELLOW"))
            {
                istory.setConsent(con);
                istory = historyRepository.save(istory);

                info = "request accepted";
            }
        }

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @PostMapping("/set")
    public ResponseEntity<?> setNextCollector(@Valid @RequestBody CollectorDto set, HttpServletRequest req)
    {
        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        User organizer = userRepository.findByEmail(jwtService.getSubject(jwt)).get();
        Optional<User> byEmail = userRepository.findByEmail(set.getUserEmail());
        if(!(byEmail.isPresent()))
        {
            return new ResponseEntity<>("user does not exist", HttpStatus.BAD_REQUEST);
        }
        User thrifter = byEmail.get();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(set.getTicket());
        if(!(byTicket.isPresent()))
        {
            return new ResponseEntity<>("Thrift does not exist", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        thrift.setCollector(thrifter);
        thrift = thriftsRepository.save(thrift);

        return new ResponseEntity<>("Thrift collector set succesfully", HttpStatus.OK);
    }
}
