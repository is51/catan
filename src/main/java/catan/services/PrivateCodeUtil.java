package catan.services;

public class PrivateCodeUtil {

    private RandomValueGenerator rvg = new RandomValueGenerator();

    public String generateRandomPrivateCode(int numberOfDigits) {
        int digits = 1;
        for (int i = 1; i < numberOfDigits; i++) {
            digits *= 10;
        }

        char firstTwoDigits = (char) (65 + rvg.randomValue() * 26);
        char secondTwoDigits = (char) (65 + rvg.randomValue() * 26);
        int remainingDigits = (int) (digits + rvg.randomValue() * digits * 9);


        return "" + firstTwoDigits + secondTwoDigits + remainingDigits;
    }

    public void setRvg(RandomValueGenerator rvg) {
        this.rvg = rvg;
    }
}