package com.example.demo.Dtos;

import com.example.demo.Enums.Account_type;
import com.example.demo.Enums.Side;
import com.example.demo.Model.Account;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogAccDto
{
    @Size(min=5)
    @NotNull
    @NotBlank
    private String bank;

    @Size(min=5)
    @NotNull
    @NotBlank
    private String acc_name;

    @Size(min=10,max=10)
    @NotNull
    @NotBlank
    private String acc_num;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Account_type account_type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Side side = Side.INACTIVE;

    public Account getAcc()
    {
        Account acc = new Account();
        acc.setAcc_name(this.acc_name);
        acc.setAcc_num(this.acc_num);
        acc.setBank(this.bank);
        acc.setSide(this.side);
        acc.setAccount_type(this.account_type);

        return acc;
    }
}
