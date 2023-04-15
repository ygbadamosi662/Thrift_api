package com.example.demo.Services;

import com.example.demo.Dtos.LogAccDto;
import com.example.demo.Enums.Side;
import com.example.demo.Model.Account;
import com.example.demo.Model.Thrift;
import com.example.demo.Repositories.AccountRepository;
import com.example.demo.Repositories.ThriftsRepository;
import com.example.demo.Utilities.Utility;
import jakarta.persistence.PersistenceException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class BankService
{
    private final AccountRepository accRepo;

    private final ThriftsRepository thriftsRepository;

    private final Utility util;

    public Account assignAcc()
    {
        List<Account> accounts = accRepo.findBySide(Side.INACTIVE);

        if(accounts.isEmpty())
        {
            return null;
        }
        Account acc = accounts.get(accounts.size() - 1);
        acc.setSide(Side.ACTIVE);
        return acc;

    }

    public List<Account> getAccounts (int page, Side side)
    {
        Pageable pageable = PageRequest.of(page, 50);
        Slice<Account> accs = accRepo.findBySide(side, pageable);

        return accs.getContent();
    }



    public Map<String, Map<String, Double>> ActiveToInactiveInfo()
    {
        Long active = accRepo.countBySide(Side.ACTIVE);
        Long inactive = accRepo.countBySide(Side.INACTIVE);
        Map<String, Map<String, Double>> all = new HashMap<>();

        Map<String, Double> info = new HashMap<>();
        info.put("activeAcc", (double) active);
        info.put("percentage", util.percentage(active, active+inactive));
        all.put("active", info);

        info = new HashMap<>();
        info.put("inactiveAcc", (double) inactive);
        info.put("percentage", util.percentage(inactive, active+inactive));
        all.put("inactive", info);

        return all;
    }

    public Account logAcc(LogAccDto dto)
    {
        Account acc = new Account();
        try
        {
            acc = accRepo.save(dto.getAcc());
        }
        catch (PersistenceException e)
        {
            System.out.println("database connection failed");
        }

        return acc;
    }

    public List<Thrift> getBens(Long id)
    {
        Optional<Account> byId = accRepo.findById(id);
        if(byId.isEmpty())
        {
            return null;
        }

        List<Thrift> thrifts = thriftsRepository.findByThriftAccount(byId.get());

        return thrifts;
    }

    public Map<String, List<Thrift>> afterHours(int page)
    {
//        assigns Available accounts to all thrifts with no account in order of their start date
        Map<String, List<Thrift>> res = new HashMap<>();


        List<Account> accs = accRepo.findBySide(Side.INACTIVE);
        if(accs.size() != 0) {
            try
            {
                List<Thrift> lll = new ArrayList<>();
                List<Thrift> nullAccs = this.getUnasigned(1, accs.size());

                int index = 0;
                for (Account acc : accs) {
                    nullAccs.get(index).setThriftAccount(acc);
                    lll.add(thriftsRepository.save(nullAccs.get(index)));
                    index = index + 1;
                }
                res.put("asigned", lll);
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                return null;
            }
        }
        res.put("unasigned", this.getUnasigned(page, 50));

        return res;
    }

    public List<Thrift> getUnasigned(int page, int size)
    {
        Pageable pageRequest = PageRequest.of(page-1, size,
                Sort.by("thrift_start").descending());
        List<Thrift> unasigned = thriftsRepository.findByThriftAccount(null, pageRequest);

        return unasigned;
    }

    public Map<String, List<Thrift>> afterHours()
    {
        return this.afterHours(1);
    }
}
