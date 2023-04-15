package com.example.demo.Services;

import com.example.demo.Model.Thrift;
import com.example.demo.Model.User;
import com.example.demo.Repositories.ThriftsRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
public class SomeService
{
    private final ThriftsRepository thriftsRepository;

    public List<User> getAllOrganizers (Pageable pageable)
    {
        Page<Thrift> page = thriftsRepository.findAll(pageable);
        List<User> orgs = new ArrayList<>();
        page.getContent().forEach((thrift)-> {
            User org = thrift.getOrganizer();
            if(!orgs.contains(org))
            {
                orgs.add(org);
            }
        });

        return orgs;
    }
}
