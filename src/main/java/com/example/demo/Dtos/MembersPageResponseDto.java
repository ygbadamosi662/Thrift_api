package com.example.demo.Dtos;

import lombok.Getter;

import java.util.List;

@Getter
public class MembersPageResponseDto extends PageResponseDto
{
    private List<MemberResponseDto> members;

    public MembersPageResponseDto(boolean hasNext, boolean hasPrevious, int pageSize, List<MemberResponseDto> members )
    {
        this.setHasNext(hasNext);
        this.setPageSize(pageSize);
        this.setHasPrevious(hasPrevious);
        this.members = members;
    }
}
