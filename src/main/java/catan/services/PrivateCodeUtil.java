package catan.services;

import catan.exception.PrivateCodeException;

public class PrivateCodeUtil {
    public static final int LETTER_LENGTH = 2;
    public static final int DIGIT_LENGTH = 4;
    public static final String WRONG_LENGTH_ERROR = "WRONG_LENGTH";
    public static final String FIRST_SYMBOLS_ARE_NOT_LETTERS_ERROR = "FIRST_SYMBOLS_ARE_NOT_LETTERS";
    public static final String SYMBOLS_ARE_NOT_DIGITS_ERROR = "SYMBOLS_ARE_NOT_DIGITS";

    private RandomValeGenerator rvg = new RandomValeGenerator();

    public int generateRandomPrivateCode() {
        int firstTwoDigits = (int) (1 + rvg.randomValue() * 26) * 1000000;
        int secondTwoDigits = (int) (1 + rvg.randomValue() * 26) * 10000;
        int remainingDigits = (int) (rvg.randomValue() * 10000);

        return firstTwoDigits + secondTwoDigits + remainingDigits;
    }

    public String getDisplayValueFromPrivateCode(int privateCode) {
        String privateCodeDisplayValue;
        String originalIntegerCode = String.valueOf(privateCode);
        if (originalIntegerCode.length() < (LETTER_LENGTH * 2 + DIGIT_LENGTH)) {
            originalIntegerCode = "0" + originalIntegerCode;
        }

        char firstLetter = (char) (64 + Integer.parseInt(originalIntegerCode.substring(0, 2)));
        char secondLetter = (char) (64 + Integer.parseInt(originalIntegerCode.substring(2, 4)));

        privateCodeDisplayValue = "" + firstLetter + secondLetter + originalIntegerCode.substring(DIGIT_LENGTH);

        return privateCodeDisplayValue;
    }

    public int getPrivateCodeFromDisplayValue(String gameIdentifier) throws PrivateCodeException {
        String privateCodeDisplayValue = gameIdentifier.toUpperCase();

        if (privateCodeDisplayValue.length() != LETTER_LENGTH + DIGIT_LENGTH) {
            throw new PrivateCodeException(PrivateCodeUtil.WRONG_LENGTH_ERROR);
        }

        char firstLetter = privateCodeDisplayValue.charAt(0);
        char secondLetter = privateCodeDisplayValue.charAt(1);

        if (firstLetter - 65 < 0 || secondLetter - 65 < 0 || firstLetter - 65 > 25 || secondLetter - 65 > 25) {
            throw new PrivateCodeException(PrivateCodeUtil.FIRST_SYMBOLS_ARE_NOT_LETTERS_ERROR);
        }

        int firstTwoDigits = (firstLetter - 64) * 1000000;
        int secondTwoDigits = (secondLetter - 64) * 10000;
        int remainingDigits;
        try{
            remainingDigits = Integer.parseInt(privateCodeDisplayValue.substring(LETTER_LENGTH));
        } catch (Exception e){
            throw new PrivateCodeException(PrivateCodeUtil.SYMBOLS_ARE_NOT_DIGITS_ERROR);
        }

        return firstTwoDigits + secondTwoDigits + remainingDigits;
    }

    public void setRvg(RandomValeGenerator rvg) {
        this.rvg = rvg;
    }
}
