package com.wanda.pay.common.helper;

import org.junit.Test;

import java.util.Date;
import java.util.Random;

/**
 * Created by robin on 16/12/22.
 */
public class PayStringHelplerTest {

    @Test
    public void testRandomStr() {
        System.out.println(StringHelper.randomTimestampStr(new Date().getTime(), 10));
        System.out.println(StringHelper.randomTimestampStr(new Date().getTime(), 10).length());
<<<<<<< HEAD
        //System.out.println(new Random().nextInt(0));
=======
       // System.out.println(new Random().nextInt(0));
>>>>>>> master
    }
}
