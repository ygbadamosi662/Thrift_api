package com.example.demo.Utilities;

import com.example.demo.Dtos.ThriftResponseDto;
import com.example.demo.Enums.Consent;
import com.example.demo.Enums.Lifecycle;
import com.example.demo.Model.*;
import com.example.demo.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class Utility
{
    private int multi;

    private int longest = 52;

    @Autowired
    private ThrifterHistoryRepository historyRepository;

    @Autowired
    private ThriftsRepository thriftsRepository;

    @Autowired
    private Thrift_hubRepository hubRepo;

//    private final JwtBlacklistRepository jwtBlacklistRepo;

    public List<User> get_members(Thrift thrift, boolean all)
    {
//        returns a list of every thrifter that are participating in thrift
//        depends on all, if true ignores the consent if false considers the consent
        List<User> members = new ArrayList<>();

        if(all)
        {
            List<ThrifterHistory> byThrift = historyRepository.findByThrift(thrift);
            for (int i = 0; i < byThrift.size(); i++)
            {
                members.add(byThrift.get(i).getUser());
            }
        }
        else
        {
            List<ThrifterHistory> byThriftAndConsent = historyRepository.findByThriftAndConsent(thrift,
                    Consent.GREEN);
            for (int i = 0; i < byThriftAndConsent.size(); i++)
            {
                members.add(byThriftAndConsent.get(i).getUser());
            }
        }

        return members;
    }

    public List<User> get_members(Thrift thrift)
    {
        return this.get_members(thrift, false);
    }

    public Slice get_membersInfo(Thrift thrift, int page)
    {
        int pageSize = 10;
        Pageable pages = PageRequest.of(page, pageSize);

        Slice<ThrifterHistory> slice = historyRepository.findByThriftAndConsent(thrift, Consent.GREEN, pages);

        return slice;
    }

    public List<Thrift> removesDeleted(List<Thrift> thrifts)
    {
        if(thrifts.isEmpty() == false)
        {
            thrifts.forEach( thrift -> {
                if(thrift.getCycle().equals(Lifecycle.DELETED))
                {
                    thrifts.remove(thrift);
                }
            });
        }
        return thrifts;
    }


    public boolean is_member(User user, Thrift thrift, boolean all)
    {
        List<User> members = new ArrayList<>();
        boolean present = false;

        if(all)
        {
            members = this.get_members(thrift, true);
        }
        else
        {
            members = this.get_members(thrift);
        }

        for (User member: members)
        {
            if(member.equals(user))
            {
                present = true;
            }
        }

        return present;
    }

    public boolean is_member(User user, Thrift thrift)
    {
        return this.is_member(user, thrift, false);
    }

    public Thrift slotsManager(Thrift thrift, int slot)
    {
        thrift.setSlots(thrift.getSlots() + slot);

        if(this.duration_chk(thrift) == false)
        {
            System.out.println(this.duration_chk(thrift));
//            Thrift thrick = new Thrift();
            return null;
        }

        thrift.setCollection_amount(this.collectionCalc(thrift));
        System.out.println(thrift);
        return thrift;
    }

    public boolean slotsAssistantManager(Thrift thrift)
    {
        if(thrift == null)
        {
            return false;
        }

        return true;
    }

    public void minus_slot(Thrift thrift, int slot)
    {
        thrift.setSlots(thrift.getSlots() - slot);
        thriftsRepository.save(thrift);
    }
    public LocalDate get_thrift_end(Thrift patsy)
    {
//        should only be called after duration_check() is called
//        sets patsy.setThrift_end()
        Map<String,Integer> terms = new HashMap<>();
        terms.put("WEEKLY",1);
        terms.put("BI-WEEKLY",2);
        terms.put("MONTHLY",4);

        terms.forEach((term,weeks)-> {
            if(term.equals(patsy.getTerm().name()))
            {
                this.multi = weeks;
            }
        });
        System.out.println(patsy.getThrift_start());
        System.out.println(patsy.getSlots());
        LocalDate thrift_end = patsy.getThrift_start().plusWeeks(multi * patsy.getSlots());
        System.out.println(patsy.getThrift_start().plusWeeks(4));

        return thrift_end;
    }

    public boolean duration_chk(Thrift thrift)
    {
//        the longest a thrift should last is 52 weeks
//        this method makes sure thats adhered to
//        returns true if thrift duration is ok and false otherwise

        return longest >= duration(thrift);
    }

    public int duration(Thrift thrift)
    {
//        the longest a thrift should last is 52 weeks
//        this method makes sure thats adhered to
//        returns true if thrift duration is ok and false otherwise

        Map <String,Integer> terms = new HashMap<>();
        terms.put("WEEKLY",1);
        terms.put("BI-WEEKLY",2);
        terms.put("MONTHLY",4);

        terms.forEach((term,weeks)-> {
            if(term.equals(thrift.getTerm().name()))
            {
                multi = weeks;
            }
        });

        return multi * thrift.getSlots();
    }

    public double capacityInPercntage(Thrift thrift)
    {
        Long of = (long) duration(thrift);
        Long in = (long) longest;

        return percentage(of, in);
    }

    public long collectionCalc(Thrift thrift)
    {
        long amnt = thrift.getSlots() * thrift.getPer_term_amnt();

        return amnt;
    }

    public long availablePotCalc(Thrift thrift, long index)
    {
        List<Thrift_hub> hubs = hubRepo.findByThriftAndThriftIndex(thrift, index);
        long pot = 0;

        if(hubs.size() == 0)
        {
            return pot;
        }

        for (Thrift_hub hub: hubs)
        {
            pot = pot + hub.getTransaction().getAmount();
        }

        return pot;
    }

    public boolean thePriceIsRight(ThrifterHistory isto, int amnt)
    {
        return (isto.getSlot() * isto.getThrift().getPer_term_amnt()) == amnt;
    }

    public List<ThrifterHistory> consent_hr(User thri)
    {
//        to overload consent_hr(User thri, long id)
//        returns a list of ThrifterHistory
        return consent_hr(thri, 0);
    }

    public List<ThrifterHistory> consent_hr(User thri, long thrift_id)
    {
//        this functions is called to get the list of thrift that has not been given the green enum
//        if a second parameter long thrift_id is passed it gives the thrift a green light in thrifter history
//        it returns a list of ThrifterHistory
        List<ThrifterHistory> istory = new ArrayList<>();

        if(thrift_id == 0)
        {
            List<ThrifterHistory> byThr = historyRepository.findByUser(thri);

            for (int i = 0; i < byThr.size(); i++)
            {
                if(!(byThr.get(i).getConsent().name().equals("GREEN")) &&
                        !(byThr.get(i).getConsent().name().equals("RED")))
                {
                    Optional<Thrift> thrift = thriftsRepository.findById(byThr.get(i).getId());
                    LocalDate now = LocalDate.now();
                    if(thrift.get().getThrift_start().isBefore(now))
                    {
                        historyRepository.delete(byThr.get(i));
                    }
                    istory.add(byThr.get(i));
                }
            }
        }

        if(thrift_id > 0)
        {
            Consent con = Consent.GREEN;
            Thrift thrift = thriftsRepository.findById(thrift_id).get();
            Optional<ThrifterHistory> byTwos = historyRepository.findByThriftAndUser(thrift, thri);
            ThrifterHistory isto = byTwos.get();
            isto.setConsent(con);
            istory.add(isto);
        }

        return istory;
    }

    public Map<String, Object> chk_user_limit(User user)
    {
//        this method checks if d user has reached its limit in participating in anymore thrift
        int limit = 3;
        int countAwaiting = 0;
        int countRunning = 0;
        Map <String, Integer> info = new HashMap<>();
        Map <String, Object> returnee = new HashMap<>();


        Map<String, ? extends Object> hold = new HashMap<>();
        List<Thrift> all_thrift = new ArrayList<>();
        List<ThrifterHistory> th = historyRepository.findByUserAndConsent(user, Consent.GREEN);
        for (ThrifterHistory each : th)
        {
            Thrift thrift = each.getThrift();
            all_thrift.add(thrift);
        }

        for(Thrift each : all_thrift)
        {
            Lifecycle cycle = Lifecycle.COMPLETED;
            if(each.getCycle().equals(cycle))
            {
                all_thrift.remove(each);
            }

            if(each.getCycle().name() == "AWAITING")
            {
                countAwaiting = countAwaiting + 1;
            }

            if(each.getCycle().name() == "RUNNING")
            {
                countRunning = countRunning + 1;
            }

        }

        if(all_thrift.size() == limit)
        {
            info.put("awaiting", countAwaiting);
            info.put("running", countRunning);
            returnee.put("info", info);
            returnee.put("more_info", all_thrift);
            returnee.put("good?", false);

            return returnee;

        }
        else
        {
            returnee.put("good?", true);
            return returnee;
        }

    }

    public List<Thrift> get_thrifts(User user)
    {
        List<Thrift> thriftList = new ArrayList<>();

        List<ThrifterHistory> all = historyRepository.findByUser(user);
        if(!(all.isEmpty()))
        {
            for (ThrifterHistory istory: all)
            {
                thriftList.add(istory.getThrift());
            }
        }

        return this.removesDeleted(thriftList);
    }


    public  Map<String, Map <String, List<ThriftResponseDto> > >
    get_thrifts(User user, boolean more_info)
    {
        List<Thrift> thrifts= this.get_thrifts(user);
        Map <String, List<ThriftResponseDto> > hold = new HashMap<>();
        Map<String, Map <String, List<ThriftResponseDto> > > bigBoy = new HashMap<>();

        for (Thrift thrift: thrifts)
        {
            List<ThriftResponseDto> res = new ArrayList<>();

            if(thrift.getCycle().name().equals("COMPLETED"))
            {
                if(bigBoy.containsKey("Completed") != true)
                {
                    bigBoy.put("Completed", hold);
                }

                if(bigBoy.get("Completed").containsKey("thrifts") != true)
                {
                    List<ThriftResponseDto> dtos = new ArrayList<>();
                    bigBoy.get("Completed").put("thrifts", dtos);
                }
                ThriftResponseDto dto = new ThriftResponseDto(thrift);
                dto.setAllWeirdAssClasses(thrift);
                bigBoy.get("Completed").get("thrifts").add(dto);
            }
            else if(thrift.getCycle().name().equals("RUNNING"))
            {
                if(bigBoy.containsKey("Running") != true)
                {
                    bigBoy.put("Running", hold);
                }

                if(bigBoy.get("Running").containsKey("thrifts") != true)
                {
                    List<ThriftResponseDto> dtos = new ArrayList<>();
                    bigBoy.get("Running").put("thrifts", dtos);
                }
                ThriftResponseDto dto = new ThriftResponseDto(thrift);
                dto.setAllWeirdAssClasses(thrift);
                bigBoy.get("Running").get("thrifts").add(dto);

            }
            else if(thrift.getCycle().name().equals("AWAITING"))
            {
                if(bigBoy.containsKey("Awaiting") != true)
                {
                    bigBoy.put("Awaiting", hold);
                }

                if(bigBoy.get("Awaiting").containsKey("thrifts") != true)
                {
                    List<ThriftResponseDto> dtos = new ArrayList<>();
                    bigBoy.get("Awaiting").put("thrifts", dtos);
                }
                ThriftResponseDto dto = new ThriftResponseDto(thrift);
                dto.setAllWeirdAssClasses(thrift);
                bigBoy.get("Awaiting").get("thrifts").add(dto);

//
            }
        }

        return bigBoy;
    }

    public List<ThrifterHistory> getHistory(User user)
    {
        return historyRepository.findByUser(user);
    }

    public ThrifterHistory removeMember(Thrift thrift, User member)
    {
        Optional<ThrifterHistory> byThriftAndUser = historyRepository.findByThriftAndUser(thrift, member);
        if(byThriftAndUser.isEmpty())
        {
            System.out.println("user is not a member");
            return null;
        }

        ThrifterHistory isto = byThriftAndUser.get();
        Consent con = isto.getConsent();

        if(thrift.getOrganizer().equals(member))
        {

            List<ThrifterHistory> all = historyRepository.findByThrift(thrift);

            if(all.size() == 1)
            {
                ThrifterHistory histo = all.get(1);
                histo.setConsent(Consent.ORED);
                historyRepository.save(histo);

                thrift.setCycle(Lifecycle.DELETED);
                return isto;
            }

            if(all.size() > 1 && this.hasStarted(thrift) == false)
            {
                for (ThrifterHistory histo: all)
                {
                    histo.setConsent(Consent.ORED);
                    historyRepository.save(histo);
                }

                thrift.setCycle(Lifecycle.DELETED);
                return isto;
            }

            return null;
        }

        if(this.hasStarted(thrift))
        {
            return null;
        }

//        if rejected by thrifter
        if(con == Consent.TYELLOW)
        {
            isto.setConsent(Consent.TRED);
        }

//        if rejected by organizer
        if(con == Consent.OYELLOW)
        {
            isto.setConsent(Consent.ORED);
        }

        this.minus_slot(thrift, isto.getSlot());
        thrift.setCollection_amount(this.collectionCalc(thrift));
        thriftsRepository.save(thrift);

        return historyRepository.save(isto);
    }

    public boolean hasStarted(Thrift thrift)
    {
        LocalDate now = LocalDate.now();
        if(thrift.getThrift_start().equals(now) || thrift.getThrift_start().isBefore(now))
        {
            return true;
        }

        return false;
    }

    public void runThrift(Thrift thrift)
    {
        if(this.hasStarted(thrift) && thrift.getCycle().equals(Lifecycle.AWAITING))
        {
            thrift.setCycle(Lifecycle.RUNNING);
            thriftsRepository.save(thrift);
        }
    }

    public void thriftSweeper(User user)
    {
        List<ThrifterHistory> notGreens = historyRepository.findByUserAndConsents(user, Consent.OYELLOW,
                Consent.TYELLOW);
        List<ThrifterHistory> deletedThrifts = historyRepository.findByUserAndThriftCycleAndConsents(user,
                Lifecycle.DELETED, Consent.OYELLOW, Consent.TYELLOW);

        notGreens.forEach((notGreen) -> {
            if(hasStarted(notGreen.getThrift()))
            {
                notGreen.setConsent(Consent.GODRED);
                historyRepository.save(notGreen);
            }
        });

        deletedThrifts.forEach((deletedThrift) -> {
            deletedThrift.setConsent(Consent.GODRED);
            historyRepository.save(deletedThrift);
        });
    }

    public double percentage(Long of, Long in)
    {
        double percent = 0;

        try
        {
            double off = (double) of;
            double inn = (double) in;

            percent = (off/inn) * 100;
        }
        catch (ArithmeticException e)
        {
            System.out.println(e.getMessage());;
        }

        return percent;
    }

    public Slice<Thrift_hub> thriftIndexed(Thrift thrift, Long index, int page)
    {
        int pageSize = 10;
        Pageable pages = PageRequest.of(page, pageSize);

        return hubRepo.findByThriftAndThriftIndex(thrift, index, pages);
    }

    public List<Thrift> get_completed(User user)
    {
        List<Thrift> all = this.get_thrifts(user);
        List<Thrift> comp_list = new ArrayList<>();
        Lifecycle cycle = Lifecycle.COMPLETED;
        for (Thrift thrift: all)
        {
            if(thrift.getCycle().name().equals(cycle))
            {
                comp_list.add(thrift);
            }
        }

        return comp_list;
    }

    public List<ThrifterHistory> consent_note(User user)
    {
        List<ThrifterHistory> consent = this.consent_hr(user);
        for (ThrifterHistory isto: consent)
        {
            if(isto.getThrift().getOrganizer().equals(user))
            {
                List<User> members = this.get_members(isto.getThrift(), true);
                Consent con = Consent.TYELLOW;
                for (User member: members)
                {
                    ThrifterHistory history = historyRepository
                            .findByThriftAndUser(isto.getThrift(), member).get();
                    if((history.getConsent().equals(con)) && !(member.equals(user)))
                    {
                        consent.add(history);
                    }
                }
            }
        }

        return consent;
    }

    public List<Thrift> collector_notes(User user)
    {
        List<Thrift> thrifts = this.get_thrifts(user);

        for (Thrift thrift: thrifts)
        {
            if(!(thrift.getCollector().equals(user)))
            {
                thrifts.remove(thrift);
            }
        }

        return thrifts;
    }

    public Map<String, Map> note_manager(User user)
    {
        Map<String, Map> all = new HashMap<>();
        Map<String, List> consent = new HashMap<>();
        List<ThrifterHistory> consentData = this.consent_note(user);

        consent.put("data", consentData);

        all.put("Consent", consent);

        return all;
    }
}
