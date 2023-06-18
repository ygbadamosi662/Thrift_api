package com.example.demo.Controllers;

import com.example.demo.Dtos.*;
import com.example.demo.Enums.Consent;
import com.example.demo.Enums.Lifecycle;
import com.example.demo.Enums.Role;
import com.example.demo.Enums.TypeOf;
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
    private final ThrifterHistoryRepository historyRepository;

    private final AccountRepository accRepo;

    private final TransactionRepository transRepo;

    private final Thrift_hubRepository hubRepo;

    private final ThePotRepository potRepo;

//    @Autowired
    private final Utility util;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final ThriftsRepository thriftsRepository;

    private final BankService bankServe;

    private final Jwt jwtObj;


    @PostMapping("/create")
    public ResponseEntity<?> createThrift(@Valid @RequestBody CreateThriftDto create,HttpServletRequest request)
    {

        String jwt = jwtObj.setJwt(request);

        if(jwtObj.is_cancelled(jwt))
        {
            System.out.println("im here");
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User organizer = jwtObj.giveUser();

        Map<String, Object> check = util.chk_user_limit(organizer);
        boolean good = (Boolean) check.get("good?");
        if(good == false)
        {
            Map<String, Integer> info = (HashMap) check.get("info");
            List<Thrift> more_info = (ArrayList) check.get("more_info");
            return new ResponseEntity<>("User at limit,info incoming", HttpStatus.BAD_REQUEST);
        }
        String more_info = "";
        Thrift thrift = create.getThrift();

        thrift.setOrganizer(organizer);
//        updates the thrift slots and collection_amnt property
        thrift = util.slotsManager(thrift, 1);
        thrift.setThrift_end(util.get_thrift_end(thrift));
        thrift.setThrift_index(1);
        thrift.setCollection_available(0);
        Lifecycle cycle = Lifecycle.AWAITING;
        thrift.setCycle(cycle);

        if(bankServe.ActiveToInactiveInfo().get("inactive").get("inactiveAcc") > 0)
        {
            thrift.setAccount(bankServe.assignAcc());
        }
        else
        {
            more_info = "Your thrift account will be set before thrift start";
        }

        thriftsRepository.save(thrift);

        Thrift update = thriftsRepository.findById(thrift.getId()).get();
        update.setTicket(thrift.getThriftName() + "." + ""+thrift.getId());

        update = thriftsRepository.save(update);

        organizer = userRepository.save(organizer);
        ThrifterHistory history = new ThrifterHistory();
        history.setThrift(thrift);
        history.setUser(organizer);
        history.setSlot(1);
        Consent con = Consent.GREEN;
        history.setConsent(con);
        historyRepository.save(history);

        ThriftResponseDto dto = new ThriftResponseDto(thrift);
        dto.setAllWeirdAssClasses(thrift);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addThrifter(@Valid @RequestBody AddThrifterDto add, HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

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

            List<Long> longList = new ArrayList<>();


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
            if(util.is_member(thrifter, thrift))
            {
                return new ResponseEntity<>("User already a member", HttpStatus.BAD_REQUEST);
            }
//            updates the thrift slots and collection_amnt property
//            and also checks if the thrift duration is not over a year
            if(util.slotsAssistantManager(util.slotsManager(thrift, add.getSlot())) == false)
            {
                return new ResponseEntity<>(
                        "Every Thrift must end within a yaer,too much members or too short term",
                        HttpStatus.BAD_REQUEST);
            }
            ThrifterHistory history = new ThrifterHistory();
            history.setThrift(thrift);
            history.setUser(thrifter);
            history.setSlot(add.getSlot());
            Consent con = Consent.OYELLOW;
            history.setConsent(con);
            history = historyRepository.save(history);

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
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User thrifter = jwtObj.giveUser();

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

        //        checking if thrifter has reached participation limit
        Map<String, Object> check = util.chk_user_limit(thrifter);
        boolean good = (Boolean) check.get("good?");
        if(good == false)
        {
            Map<String, Integer> info = (HashMap) check.get("info");
            List<Thrift> more_info = (ArrayList) check.get("more_info");
            return new ResponseEntity<>("User at limit", HttpStatus.BAD_REQUEST);
        }

        if(util.is_member(thrifter, thrift))
        {
            return new ResponseEntity<>("User already a member", HttpStatus.BAD_REQUEST);
        }

        //            updates the thrift slots and collection_amnt property
//            and also checks if the thrift duration is not over a year
        if(util.slotsAssistantManager(util.slotsManager(thrift, joint.getSlot())) == false)
        {
            return new ResponseEntity<>(
                    "Every Thrift must end within a yaer,too much members or too short term",
                    HttpStatus.BAD_REQUEST);
        }

        ThrifterHistory history = new ThrifterHistory();
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

    @PostMapping("/accept")
    public ResponseEntity<?> acceptThrift(@Valid @RequestBody AcceptDto ticket, HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

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
            if(ticket.getEmail().equals("none"))
            {
                return new ResponseEntity<>("No email set", HttpStatus.BAD_REQUEST);
            }

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
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(set.getTicket());
        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift does not exist", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        if(user.equals(thrift.getOrganizer()))
        {
            Optional<User> byEmail = userRepository.findByEmail(set.getEmail());
            if(byEmail.isEmpty())
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

    @PostMapping("/userthrifts")
    public ResponseEntity<?> getThrifts(@Valid @RequestBody AllThriftDto dto, HttpServletRequest req)
    {

        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }

        Map<String, Map<String, List<ThriftResponseDto>>> more_info = new HashMap<>();
        User user = jwtObj.giveUser();

        if(user.getRole().equals(Role.ADMIN) && !(dto.getEmail().equals("none")))
        {
            Optional<User> byEmail = userRepository.findByEmail(dto.getEmail());
            if(byEmail.isEmpty())
            {
                return new ResponseEntity<>("user does not exist",
                        HttpStatus.BAD_REQUEST);
            }

            more_info = util.get_thrifts(byEmail.get(), true);

            return new ResponseEntity<>(more_info, HttpStatus.OK);
        }

        more_info = util.get_thrifts(user, true);

        return new ResponseEntity<>(more_info, HttpStatus.OK);
    }

    @PostMapping("/members")
    public ResponseEntity<?> getMembers(@Valid @RequestBody JoinDto dto, HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(dto.getTicket());
        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift cannot be found", HttpStatus.BAD_REQUEST);
        }

        List<ThrifterHistory> members = util.get_membersInfo(byTicket.get());

        if(members.isEmpty())
        {
            return new ResponseEntity<>("Thrift member is unusually empty", HttpStatus.BAD_REQUEST);
        }

        List<MemberResponseDto> dtos = new ArrayList<>();
        members.forEach((member)-> {
            MemberResponseDto memberDto = new MemberResponseDto(member);
            memberDto.setMember(member);
            dtos.add(memberDto);
        });

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeMember(@Valid @RequestBody RemoveDto dto, HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

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

                return new ResponseEntity<>("Member deleted succesfully", HttpStatus.OK);
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

                return new ResponseEntity<>("Member deleted succesfully", HttpStatus.OK);
            }

            return new ResponseEntity<>("You are not a member of the thrift.",
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Only the thrift organizer can remove another member",
                    HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/slot")
    public ResponseEntity<?> manageSlot(@Valid @RequestBody AddThrifterDto dto, HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

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

                if(util.slotsAssistantManager(util.slotsManager(thrift, dto.getSlot())))
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

                if(util.slotsAssistantManager(util.slotsManager(thrift, dto.getSlot())))
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

    @PostMapping("/pay")
    public ResponseEntity<?> payThrift(@Valid @RequestBody PayDto dto, HttpServletRequest req)
    {
//        this a very very basic pay endpoint,resources not available to write a more sophisticated
//        and standard pay endpoint,will have to make do with this for now
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

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
            trans.setDebit_acc(user.getAccount());
            trans.setCredit_acc(thrift.getAccount());
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

    @PostMapping("/collect")
    public ResponseEntity<?> collectThrift(@Valid @RequestBody CollectDto dto, HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(dto.getTicket());
        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift cannot be found", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        if(!(user.equals(thrift.getOrganizer())))
        {
            return new ResponseEntity<>("Only the thrift organizer can pay out a thrift",
                    HttpStatus.BAD_REQUEST);
        }

        Optional<ThePot> byTwos = potRepo.findByThriftAndCollectionIndex(thrift, dto.getIndex());
        if(byTwos.isPresent())
        {
            ThePot pot = byTwos.get();
            ThePotResponseDto resDto = new ThePotResponseDto(pot);
            resDto.setAll(pot);
            resDto.setMore_info("Collection has already been paid out");

            return new ResponseEntity<>(resDto, HttpStatus.BAD_REQUEST);
        }

        if(thrift.getCollection_amount() != thrift.getCollection_available())
        {
            return new ResponseEntity<>("Collection is not complete,collection available is "+
                    thrift.getCollection_available()+"" + " but collection amount should be " +
                    thrift.getCollection_amount(),
                    HttpStatus.BAD_REQUEST);
        }

//        there is a big whole here that should handle actually sending the collection ammount
//        to the user account but for lack of resources that will have to wait

//        creating ThePot
        if(dto.getEmail().equals("none"))
        {
            if(thrift.getCollector() == null)
            {
                return new ResponseEntity<>("Thrift collector is not set,set thrift collector",
                        HttpStatus.BAD_REQUEST);
            }

            Transaction trans = new Transaction();
            trans.setCredit_acc(thrift.getCollector().getAccount());
            trans.setDebit_acc(thrift.getAccount());
            trans.setTypeOf(TypeOf.COLLECTION);
            trans.setAmount(thrift.getCollection_available());
            Transaction savedTrans = transRepo.save(trans);

            ThePot pot = new ThePot();
            pot.setCollector(thrift.getCollector());
            pot.setThrift(thrift);
            pot.setCollectionIndex(dto.getIndex());
            pot.setTransaction(savedTrans);
            ThePot savedPot = potRepo.save(pot);

            thrift.update();
            thriftsRepository.save(thrift);

            ThePotResponseDto resDto = new ThePotResponseDto(pot);
            resDto.setAll(pot);

            return new ResponseEntity<>(resDto, HttpStatus.OK);
        }
        else
        {
            Optional<User> byEmail = userRepository.findByEmail(dto.getEmail());
            if(byEmail.isEmpty())
            {
                return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
            }

            if(util.is_member(byEmail.get(), thrift))
            {
                thrift.setCollector(byEmail.get());

                Transaction trans = new Transaction();
                trans.setCredit_acc(thrift.getCollector().getAccount());
                trans.setDebit_acc(thrift.getAccount());
                trans.setTypeOf(TypeOf.COLLECTION);
                trans.setAmount(thrift.getCollection_available());
                Transaction savedTrans = transRepo.save(trans);

                ThePot pot = new ThePot();
                pot.setCollector(thrift.getCollector());
                pot.setThrift(thrift);
                pot.setCollectionIndex(dto.getIndex());
                pot.setTransaction(savedTrans);
                ThePot savedPot = potRepo.save(pot);

                thrift.update();
                thriftsRepository.save(thrift);

                ThePotResponseDto resDto = new ThePotResponseDto(pot);
                resDto.setAll(pot);

                return new ResponseEntity<>(resDto, HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity<>("User must be a member of the given thrift",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    @GetMapping("/payHistory")
    public ResponseEntity<?> payHistory(@Valid @RequestParam String ticket, HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(ticket);
        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift cannot be found", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        if(!thrift.getOrganizer().equals(user))
        {
            if(util.is_member(user, thrift))
            {
                List<Thrift_hub> hubs = hubRepo.findByThriftAndUser(thrift, user);
                List<Thrift_hubResponseDto> dtos = new ArrayList<>();
                hubs.forEach((hub)-> {
                    Thrift_hubResponseDto dto = new Thrift_hubResponseDto();
                    dto.setAll(hub);
                    dtos.add(dto);
                });

                return new ResponseEntity<>(dtos, HttpStatus.OK);
            }

            return new ResponseEntity<>("You are not a member of the provided thrift",
                    HttpStatus.BAD_REQUEST);
        }

        List<Thrift_hub> hubs = hubRepo.findByThrift(thrift);
        List<Thrift_hubResponseDto> dtos = new ArrayList<>();
        hubs.forEach((hub)-> {
            Thrift_hubResponseDto dto = new Thrift_hubResponseDto();
            dto.setAll(hub);
            dtos.add(dto);
        });

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/potHistory")
    public ResponseEntity<?> potHistory(@Valid @RequestParam String ticket, HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }
        User user = jwtObj.giveUser();

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(ticket);
        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("Thrift cannot be found", HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();

        List<ThePot> pots = potRepo.findByThrift(thrift);
        if(!thrift.getOrganizer().equals(user))
        {
            if(util.is_member(user, thrift))
            {
                return new ResponseEntity<>(pots.size(), HttpStatus.OK);
            }
            return new ResponseEntity<>("Not a member of this thrift", HttpStatus.BAD_REQUEST);
        }

        List<ThePotResponseDto> dtos = new ArrayList<>();
        pots.forEach((pot)-> {
            ThePotResponseDto dto = new ThePotResponseDto(pot);
            dto.setAll(pot);
            dtos.add(dto);
        });

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@Valid @RequestParam String ticket, HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }


        List<Thrift> result = thriftsRepository.findByPattern(ticket);

        if(result.isEmpty())
        {
            return new ResponseEntity<>("Nothing found", HttpStatus.BAD_REQUEST);
        }

        List<Map> response = new ArrayList<>();

        result.forEach((thri) ->{
            Map<String, String> res = new HashMap<>();
            res.put("name", thri.getThriftName());
            res.put("ticket", thri.getTicket());
            res.put("org_name", thri.getOrganizer().getFname() +" "+ thri.getOrganizer().getLname());
            res.put("org_email", thri.getOrganizer().getEmail());
            response.add(res);
        });

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getThrift")
    public ResponseEntity<?> getThrift(@Valid @RequestParam String ticket, HttpServletRequest req)
    {
        String jwt = jwtObj.setJwt(req);

        if(jwtObj.is_cancelled(jwt))
        {
            return new ResponseEntity<>("jwt blacklisted,user should login again",
                    HttpStatus.BAD_REQUEST);
        }

        Optional<Thrift> byTicket = thriftsRepository.findByTicket(ticket);

        if(byTicket.isEmpty())
        {
            return new ResponseEntity<>("No thrift found",
                    HttpStatus.BAD_REQUEST);
        }
        Thrift thrift = byTicket.get();
        ThriftResponseDto dto = new ThriftResponseDto(thrift);
        dto.setAllWeirdAssClasses(thrift);

        return ResponseEntity.ok(dto);
    }
}
