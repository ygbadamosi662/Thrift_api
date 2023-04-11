package com.example.demo.Controllers;

import com.example.demo.Dtos.*;
import com.example.demo.Enums.Consent;
import com.example.demo.Enums.Lifecycle;
import com.example.demo.Enums.TypeOf;
import com.example.demo.Model.*;
import com.example.demo.Repositories.*;
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
import org.springframework.web.bind.annotation.*;

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

    private final ThrifterHistoryRepository historyRepository;

    private final AccountRepository accRepo;

    private final TransactionRepository transRepo;

    private final Thrift_hubRepository hubRepo;

    private final ThePotRepository potRepo;

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
        thrift.setThrift_end(util.get_thrift_end(thrift));
        thrift.setOrganizer(organizer);
//        updates the thrift slots and collection_amnt property
        util.slotsManager(thrift, 1);

        thrift.setThrift_index(1);
        thrift.setCollection_available(0);
        Lifecycle cycle = Lifecycle.AWAITING;
        thrift.setCycle(cycle);
        Account acc = util.generateAcc(thrift);
        accRepo.save(acc);
        thrift.setThriftAccount(acc);
        thriftsRepository.save(thrift);

        Thrift update = thriftsRepository.findById(thrift.getId()).get();
        update.setTicket(thrift.getThriftName() + "." + ""+thrift.getId());

        update = thriftsRepository.save(update);

        organizer.settingThriftList(new ArrayList<>(Arrays.asList(thrift.getId())));
        organizer = userRepository.save(organizer);
        ThrifterHistory history = new ThrifterHistory();
        history.setThrift(thrift);
        history.setUser(organizer);
        history.setSlot(1);
        Consent con = Consent.GREEN;
        history.setConsent(con);
        history = historyRepository.save(history);

        ThriftResponseDto dto = new ThriftResponseDto(thrift);
        dto.setAllWeirdAssClasses(thrift);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addThrifter(@Valid @RequestBody AddThrifterDto add, HttpServletRequest req)
    {
        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        User user =  userRepository.findByEmail(jwtService.getSubject(jwt)).get();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(add.getTicket());
        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("thrift dooes not exist", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        LocalDate now = LocalDate.now();
        if(thrift.getThrift_start().isBefore(now))
        {
            return new ResponseEntity<>("thrift has started,cant add anymore members",
                    HttpStatus.BAD_REQUEST);
        }

        if(user.equals(thrift.getOrganizer()))
        {
            boolean exist_in = false;
            List<Long> longList = new ArrayList<>();
            ThrifterHistory history = new ThrifterHistory();


            Optional<User> byEmail = userRepository.findByEmail(add.getEmail());

            if(byEmail.isEmpty())
            {
                return new ResponseEntity<>("User must be registred to partake in thrift", HttpStatus.BAD_REQUEST);
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


//        checking if user is already part of thrift
            if(!(thrifter.getThrift_list() == null))
            {
                List <Thrift> thrift_list= util.get_thrifts(thrifter);


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

//            updates the thrift slots and collection_amnt property
//            and also checks if the thrift duration is not over a year
                if(util.slotsManager(thrift, add.getSlot()) == false)
                {
                    return new ResponseEntity<>(
                            "Every Thrift must end within a yaer,too much members or too short term",
                            HttpStatus.BAD_REQUEST);
                }

                history.setThrift(thrift);
                history.setUser(thrifter);
                history.setSlot(add.getSlot());
                Consent con = Consent.OYELLOW;
                history.setConsent(con);
                history = historyRepository.save(history);
            }
            else
            {
                return new ResponseEntity<>("Member already added", HttpStatus.BAD_REQUEST);
            }

            ThrifterHistoryResponseDto dto = new ThrifterHistoryResponseDto(history);
            dto.setAll(history);

            return ResponseEntity.ok(dto);
        }
        else
        {
            return new ResponseEntity<>("Only the thrift organizer can add other members",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinThrift(@Valid @RequestBody JoinDto joint, HttpServletRequest req)
    {
        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());
        User thrifter = userRepository.findByEmail(jwtService.getSubject(jwt)).get();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(joint.getTicket());
        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("thrift does not exist",
                    HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        LocalDate now = LocalDate.now();
        if(thrift.getThrift_start().isBefore(now))
        {
            return new ResponseEntity<>("too late,thrift already started",
                    HttpStatus.BAD_REQUEST);
        }

        boolean exist_in = false;
        List<Long> longList = new ArrayList<>();
        ThrifterHistory history = new ThrifterHistory();



        //        checking if thrifter has reached participation limit
        Map<String, Object> check = util.chk_user_limit(thrifter);
        boolean good = (Boolean) check.get("good?");
        if(good == false)
        {
            Map<String, Integer> info = (HashMap) check.get("info");
            List<Thrift> more_info = (ArrayList) check.get("more_info");
            return new ResponseEntity<>("User at limit", HttpStatus.BAD_REQUEST);
        }

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
            userRepository.save(thrifter);

//            updates the thrift slots and collection_amnt property
//            and also checks if the thrift duration is not over a year
            if(util.slotsManager(thrift, joint.getSlot()) == false)
            {
                return new ResponseEntity<>(
                        "Every Thrift must end within a yaer,too much members or too short term",
                        HttpStatus.BAD_REQUEST);
            }

            history.setThrift(thrift);
            history.setUser(thrifter);
            history.setSlot(joint.getSlot());
            Consent con = Consent.TYELLOW;
            history.setConsent(con);
            history = historyRepository.save(history);

            ThrifterHistoryResponseDto dto = new ThrifterHistoryResponseDto(history);
            dto.setAll(history);

            return ResponseEntity.ok(dto);
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
        User user = userRepository.findByEmail(jwtService.getSubject(jwt)).get();

        ThrifterHistory istory = new ThrifterHistory();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(ticket.getTicket());

        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("thrift dont exist", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        LocalDate now = LocalDate.now();
        if(thrift.getThrift_start().isBefore(now))
        {
            return new ResponseEntity<>("too late,thrift already started", HttpStatus.BAD_REQUEST);
        }

//        if organizer
        if(thrift.getOrganizer().equals(user))
        {

            Optional<User> byMail = userRepository.findByEmail(ticket.getEmail());
            if(byMail.isEmpty())
            {
                return new ResponseEntity<>("user does not exist", HttpStatus.BAD_REQUEST);
            }

            User member = byMail.get();

            Optional<ThrifterHistory> byTwo = historyRepository.findByThriftAndUser(thrift, member);
            if(byTwo.isEmpty())
            {
                return new ResponseEntity<>("user have retracted their interest", HttpStatus.BAD_REQUEST);
            }

            Consent con = Consent.GREEN;
            istory = byTwo.get();
            if(istory.getConsent().name().equals("TYELLOW"))
            {
                istory.setConsent(con);
                istory = historyRepository.save(istory);
            }


        }
        else if (!(thrift.getOrganizer().equals(user)))
        {
            Optional<ThrifterHistory> byTwo = historyRepository.findByThriftAndUser(thrift, user);
            if(byTwo.isEmpty())
            {
                return new ResponseEntity<>("organizer have retracted their offer",
                        HttpStatus.BAD_REQUEST);
            }

            Consent con = Consent.GREEN;
            istory = byTwo.get();
            if(istory.getConsent().name().equals("OYELLOW"))
            {
                istory.setConsent(con);
                istory = historyRepository.save(istory);
            }
        }

        ThrifterHistoryResponseDto dto = new ThrifterHistoryResponseDto(istory);
        dto.setAll(istory);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/set")
    public ResponseEntity<?> setNextCollector(@Valid @RequestBody CollectorDto set, HttpServletRequest req)
    {
        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        User user = userRepository.findByEmail(jwtService.getSubject(jwt)).get();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(set.getTicket());
        if(!(byTicket.isPresent()))
        {
            return new ResponseEntity<>("Thrift does not exist", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        if(user.equals(thrift.getOrganizer()))
        {
            Optional<User> byEmail = userRepository.findByEmail(set.getUserEmail());
            if(!(byEmail.isPresent()))
            {
                return new ResponseEntity<>("user does not exist", HttpStatus.BAD_REQUEST);
            }
            User thrifter = byEmail.get();

            thrift.setCollector(thrifter);
            thrift = thriftsRepository.save(thrift);

            ThriftResponseDto dto = new ThriftResponseDto(thrift);
            dto.setAllWeirdAssClasses(thrift);

            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>("Only the thrift organizer can set collector",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("thrifts")
    public ResponseEntity<?> getThrifts(@Valid HttpServletRequest req)
    {
        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        User user = userRepository.findByEmail(jwtService.getSubject(jwt)).get();

        Map<String, Map<String, List<ThriftResponseDto>>> more_info =
                util.get_thrifts(user, true);

        return new ResponseEntity<>(more_info, HttpStatus.OK);
    }

    @PostMapping("members")
    public ResponseEntity<?> getMembers(@Valid @RequestBody JoinDto dto, HttpServletRequest req)
    {
        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        User user = userRepository.findByEmail(jwtService.getSubject(jwt)).get();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(dto.getTicket());
        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift cannot be found", HttpStatus.BAD_REQUEST);
        }

        List<User> members = util.get_members(byTicket.get());
        List<UserResponseDto> dtos = new ArrayList<>();
        members.forEach((member)-> {
            dtos.add(new UserResponseDto(member));
        });

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping("remove")
    public ResponseEntity<?> removeMember(@Valid @RequestBody RemoveDto dto, HttpServletRequest req)
    {
        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        User user = userRepository.findByEmail(jwtService.getSubject(jwt)).get();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(dto.getTicket());
        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift cannot be found", HttpStatus.BAD_REQUEST);
        }

        LocalDate now = LocalDate.now();
        if(byTicket.get().getThrift_start().isBefore(now))
        {
            return new ResponseEntity<>("too late,thrift already started",
                    HttpStatus.BAD_REQUEST);
        }

        if(dto.getMemberEmail().equals("none"))
        {
            if(util.is_member(user, byTicket.get()))
            {
                ThrifterHistory istory = util.removeMember(byTicket.get(), user);
                ThrifterHistoryResponseDto resDto = new ThrifterHistoryResponseDto(istory);
                resDto.setAll(istory);

                return new ResponseEntity<>(resDto, HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity<>("user is not a member of the given thrift",
                        HttpStatus.BAD_REQUEST);
            }
        }

        if(user.equals(byTicket.get().getOrganizer()))
        {
            Optional<User> byEmail = userRepository.findByEmail(dto.getMemberEmail());
            if(byEmail.isEmpty())
            {
                return new ResponseEntity<>("member does not exist",
                        HttpStatus.BAD_REQUEST);
            }

            if(util.is_member(byEmail.get(), byTicket.get()))
            {
                ThrifterHistory istory = util.removeMember(byTicket.get(), byEmail.get());
                ThrifterHistoryResponseDto resDto = new ThrifterHistoryResponseDto(istory);
                resDto.setAll(istory);

                return new ResponseEntity<>(resDto, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Only the thrift organizer can remove another member",
                    HttpStatus.BAD_REQUEST);
    }

    @PostMapping("slot")
    public ResponseEntity<?> manageSlot(@Valid @RequestBody AddThrifterDto dto, HttpServletRequest req)
    {
        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        User user = userRepository.findByEmail(jwtService.getSubject(jwt)).get();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(dto.getTicket());
        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift cannot be found", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        LocalDate now = LocalDate.now();
        if(thrift.getThrift_start().isBefore(now))
        {
            return new ResponseEntity<>("too late,thrift already started",
                    HttpStatus.BAD_REQUEST);
        }

        if(dto.getEmail().equals("none"))
        {
            if(util.is_member(user, thrift))
            {
                ThrifterHistory isto = historyRepository.findByThriftAndUser(thrift, user).get();
                util.minus_slot(thrift, isto.getSlot());

                if(util.slotsManager(thrift, dto.getSlot()))
                {
                    isto.setSlot(dto.getSlot());
                    historyRepository.save(isto);

                    ThrifterHistoryResponseDto resDto = new ThrifterHistoryResponseDto(isto);
                    resDto.setAll(isto);

                    return new ResponseEntity<>(resDto,
                            HttpStatus.OK);
                }
                else
                {
                    return new ResponseEntity<>("Thrift cannot go on for over a year," +
                            "term too longh or members too much",
                            HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                return new ResponseEntity<>("User is not a member of the given thrift",
                        HttpStatus.BAD_REQUEST);
            }


        }

        if(user.equals(thrift.getOrganizer()))
        {
            Optional<User> byEmail = userRepository.findByEmail(dto.getEmail());
            if(byEmail.isEmpty())
            {
                return new ResponseEntity<>("user does not exist",
                        HttpStatus.BAD_REQUEST);
            }

            User member = byEmail.get();

            if(util.is_member(member, thrift))
            {
                ThrifterHistory isto = historyRepository.findByThriftAndUser(thrift, member).get();
                util.minus_slot(thrift, isto.getSlot());

                if(util.slotsManager(thrift, dto.getSlot()))
                {
                    isto.setSlot(dto.getSlot());
                    historyRepository.save(isto);

                    ThrifterHistoryResponseDto resDto = new ThrifterHistoryResponseDto(isto);
                    resDto.setAll(isto);

                    return new ResponseEntity<>(resDto,
                            HttpStatus.OK);
                }
                else
                {
                    return new ResponseEntity<>("Thrift cannot go on for over a year," +
                            "term too longh or members too much",
                            HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                return new ResponseEntity<>("User is not a member of the given thrift",
                        HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            return new ResponseEntity<>("Only the thrift organizer can add slots",
                    HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("pay")
    public ResponseEntity<?> payThrift(@Valid @RequestBody PayDto dto, HttpServletRequest req)
    {
//        this a very very basic pay endpoint,resources not available to write a more sophisticated
//        and standard pay endpoint,will have to make do with this for now
        String authHeader = req.getHeader("Authorization");
        String jwt = authHeader.substring("Bearer ".length());

        User user = userRepository.findByEmail(jwtService.getSubject(jwt)).get();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(dto.getTicket());
        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift cannot be found", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        if(thrift.getCycle().equals(Lifecycle.COMPLETED))
        {
            return new ResponseEntity<>("Thrift has been completed,cant take anymore payments",
                    HttpStatus.BAD_REQUEST);
        }

        Optional<ThrifterHistory> byT_U = historyRepository.findByThriftAndUser(thrift, user);
        if(byT_U.isEmpty())
        {
            return new ResponseEntity<>("User not a member of the given thrift",
                    HttpStatus.BAD_REQUEST);
        }
        ThrifterHistory isto = byT_U.get();

        if(util.thePriceIsRight(isto, dto.getAmnt()))
        {
//            creating transaction
            Transaction trans = new Transaction();
            trans.setDebit_acc(user.getUserAccount());
            trans.setCredit_acc(thrift.getThriftAccount());
            trans.setAmount(dto.getAmnt());
            trans.setTypeOf(TypeOf.THRIFT);
            Transaction savedTrans = transRepo.save(trans);

//            creating thrift_hub
            Thrift_hub hub = new Thrift_hub();
            hub.setThrift(thrift);
            hub.setSlot(dto.getSlot());
            hub.setUser(user);
            hub.setThriftIndex(thrift.getThrift_index());
            hub.setTransaction(savedTrans);
            Thrift_hub savedHub = hubRepo.save(hub);

            thrift.update();
            thrift.setCollection_available(util.availablePotCalc(thrift, thrift.getThrift_index()));
            if(thrift.getCycle().equals(Lifecycle.AWAITING))
            {
                thrift.setCycle(Lifecycle.RUNNING);
            }
            thriftsRepository.save(thrift);

            Thrift_hubResponseDto resDto = new Thrift_hubResponseDto(savedHub);
            resDto.setAll(hub);

            return new ResponseEntity<>(resDto, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>("Expecting " + isto.getSlot()*thrift.getPer_term_amnt()
                    + " Naira,got " + dto.getAmnt() ,
                    HttpStatus.BAD_REQUEST);
        }
    }


}
