package com.example.demo.util;

import java.util.Random;

/**
 * @param
 * @Description TODO
 * @Author dongjingxiong
 * @return
 * @Date 2020-10-15 23:22
 */
public class RandomNumber {

    public static int getRandomNumber(int n) {
        Random random = new Random();
        int index = random.nextInt(n);
        return index;
    }

    public static int getRandomNumber() {
        Random random = new Random();
        int index = random.nextInt(10);
        return index;
    }
}
