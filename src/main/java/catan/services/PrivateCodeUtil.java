package catan.services;

import catan.domain.model.game.GameBean;
import catan.exception.GameException;
import catan.services.impl.GameServiceImpl;
import org.slf4j.Logger;

public class PrivateCodeUtil {
    public static final int LETTER_LENGTH = 2;
    public static final int DIGIT_LENGTH = 4;

    public static int generateRandomPrivateCode() {
        return (int) (Math.random() * 25) * 1000000 + (int) (Math.random() * 25) * 10000 + (int) (Math.random() * 9999);
    }

    public static String getPrivateCodeDisplayValue(GameBean game) {
        String privateCode;
        String originalIntegerCode = String.valueOf(game.getPrivateCode());
        if (originalIntegerCode.length() < (LETTER_LENGTH * 2 + DIGIT_LENGTH)) {
            originalIntegerCode = "0" + originalIntegerCode;
        }

        char firstLetter = (char) (65 + Integer.parseInt(originalIntegerCode.substring(0, 2)));
        char secondLetter = (char) (65 + Integer.parseInt(originalIntegerCode.substring(2, 4)));

        privateCode = "" + firstLetter + secondLetter + originalIntegerCode.substring(DIGIT_LENGTH);

        return privateCode;
    }

    public static int getPrivateCodeFromDisplayValue(String gameIdentifier, Logger log) throws GameException {
        if (gameIdentifier.length() != LETTER_LENGTH + DIGIT_LENGTH) {
            log.debug("<< Private code has wrong length");
            throw new GameException(GameServiceImpl.INVALID_CODE_ERROR);
        }

        char firstLetter = gameIdentifier.charAt(0);
        char secondLetter = gameIdentifier.charAt(1);

        if (firstLetter - 65 < 0 || secondLetter - 65 < 0) {
            log.debug("<< First to symbols of private code are not letters");
            throw new GameException(GameServiceImpl.INVALID_CODE_ERROR);
        }

        int privateCode = (firstLetter - 65) * 1000000 + (secondLetter - 65) * 10000 +
                Integer.parseInt(gameIdentifier.substring(firstLetter > 74 ? LETTER_LENGTH * 2 - 2 : LETTER_LENGTH * 2 - 1));

        log.debug("Game identifier: " + gameIdentifier + " converted to private code: " + privateCode);

        return privateCode;
    }
}
