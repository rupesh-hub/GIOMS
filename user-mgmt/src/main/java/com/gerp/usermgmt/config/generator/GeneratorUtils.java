package com.gerp.usermgmt.config.generator;

import java.util.Random;

public class GeneratorUtils {

    public  static  int getRandomNonZeroNumber() {
        Random random = new Random();
        int num = random.nextInt(3);
        if(num == 0) {
            num++;
        }
        return num;
    }
}
