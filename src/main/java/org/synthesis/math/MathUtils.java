package org.synthesis.math;

public class MathUtils {

    public static int closestPowerOfTwoAbove(int N) {
        return 1 << (int) Math.ceil(Math.log(N) / Math.log(2));
    }

    public static boolean isPowerOfTwo(int x) {
        final int maxBits = 32;
        int n = 2;
        for (int i = 2; i <= maxBits; i++) {
            if (n == x)
                return true;
            n <<= 1;
        }
        return false;
    }

}
