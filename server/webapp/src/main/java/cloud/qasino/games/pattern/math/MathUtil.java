package cloud.qasino.games.pattern.math;

public class MathUtil {

    // Casting an int to double is a widening primitive conversion
    public static double roundToNDigits(double value, int n) {
        // https://www.w3schools.com/java/ref_math_round.asp
        return Math.round(value * (10 ^ n)) / (double) (10 ^ n);
    }
}
