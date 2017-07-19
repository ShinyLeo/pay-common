package com.wanda.pay.common.helper;

/**
 * Created by robin on 16/12/22.
 */
public class NumberHelper {
    /**
     * 求a的b次方
     * @param a
     * @param b
     * @return
     */
    public static int pow(int a, int b) {
        if (a == 0) {
            return 0;
        }
        int result = 1;
        for (int i = 0; i < b; i++) {
            result *= a;
        }
        return result;
    }
}
