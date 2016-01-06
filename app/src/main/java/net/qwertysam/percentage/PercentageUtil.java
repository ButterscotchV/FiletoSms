package net.qwertysam.percentage;

public class PercentageUtil {

    public static int getPercentage(int numerator, int denominator) {
        return getPercentage((long) numerator, (long) denominator);
    }

    public static int getPercentage(long numerator, long denominator) {
        return Math.round((float) 100 * (float) ((float) numerator / (float) denominator));
    }
}
