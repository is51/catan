package catan.services.util.play;

import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.types.HexType;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtil {

    public static final String ERROR_CODE_ERROR = "ERROR";
    public static final String OFFICES_LIMIT_IS_REACHED_ERROR = "OFFICES_LIMIT_IS_REACHED ";

    public static int toValidNumber(String numberString, Logger log) throws PlayException {
        try {
            return Integer.parseInt(numberString);
        } catch (Exception e) {
            log.error("Cannot convert number to integer value: {}", numberString);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public static HexType toValidResourceType(String resourceString, Logger log) throws PlayException {
        try {
            return HexType.valueOf(resourceString);
        } catch (Exception e) {
            log.debug("Illegal resource type: {}", resourceString);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }
}
