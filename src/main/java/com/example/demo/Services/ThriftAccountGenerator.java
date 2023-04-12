package com.example.demo.Services;


import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Setter
@Getter
public class ThriftAccountGenerator
{
    private String Bank = "MyBank";

    public String generateAccNum()
    {
        String accNum = "";
        Random rand = new Random();

        for (int i = 0; i < 10; i++)
        {
            accNum = accNum + rand.nextInt(10)+"";
        }

        return accNum;
    }
}
