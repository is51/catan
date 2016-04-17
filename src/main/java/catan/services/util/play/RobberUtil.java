package catan.services.util.play;

import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RobberUtil {
    private Logger log = LoggerFactory.getLogger(RobberUtil.class);

    public static final String ERROR_CODE_ERROR = "ERROR";

    public void activateRobberIfNeeded(GameBean game) {
        if (isRobbersActivity(game)) {
            log.debug("Robber activity is started, checking if players should kick-off resources");
            checkIfPlayersShouldKickOffResources(game);
        }
    }

    public void changeRobbedHex(HexBean hexToRob) {
        for (HexBean hex : hexToRob.getGame().getHexes()) {
            if (hex.isRobbed()) {
                hex.setRobbed(false);
                break;
            }
        }
        hexToRob.setRobbed(true);
        log.info("Hex {} successfully robbed", hexToRob.getAbsoluteId());
    }

    private boolean isRobbersActivity(GameBean game) {
        return game.getDiceFirstValue() + game.getDiceSecondValue() == 7;
    }

    private void checkIfPlayersShouldKickOffResources(GameBean game) {
        GameUserBean gameUser = game.fetchActiveGameUser();
        boolean shouldResourcesBeKickedOff = false;
        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            if (gameUserIterated.getAchievements().getTotalResources() > 7) {
                gameUserIterated.setKickingOffResourcesMandatory(true);
                shouldResourcesBeKickedOff = true;
            }
        }

        if (shouldResourcesBeKickedOff) {
            MessagesUtil.updateDisplayedMsg(gameUser, "help_msg_wait_for_kicking_off_res");
            return;
        }
        game.setRobberShouldBeMovedMandatory(true);
        MessagesUtil.updateDisplayedMsg(gameUser, "help_msg_move_robber");
    }

    public void checkRobberShouldBeMovedMandatory(GameBean game) {
        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            if (gameUserIterated.isKickingOffResourcesMandatory()) {
                MessagesUtil.updateDisplayedMsg(game.fetchActiveGameUser(), "help_msg_wait_for_kicking_off_res");
                return;
            }
        }
        game.setRobberShouldBeMovedMandatory(true);
        MessagesUtil.updateDisplayedMsg(game.fetchActiveGameUser(), "help_msg_move_robber");
    }

    public void validateGameUserCouldBeRobbed(GameUserBean gameUser) throws PlayException {
        if (gameUser.equals(gameUser.getGame().fetchActiveGameUser())) {
            log.error("Player couldn't rob himself");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        for (HexBean hex : gameUser.getGame().getHexes()) {
            if (!hex.isRobbed()) {
                continue;
            }
            if (!hex.fetchGameUsersWithBuildingsAtNodes().remove(gameUser)) {
                log.error("GameUser {} doesn't have any buildings at robbed hex {} and couldn't be robbed", gameUser.getGameUserId(), hex.getAbsoluteId());
                throw new PlayException(ERROR_CODE_ERROR);
            }
            break;
        }
    }

    public void validateSumOfResourcesToKickOffIsTheHalfOfTotalResources(int sumOfUsersResources, int sumOfResourcesKickingOff) throws PlayException {
        if (sumOfResourcesKickingOff != sumOfUsersResources / 2) {
            log.error("Wrong resources quantity: {}", sumOfResourcesKickingOff);
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }

    public int toValidResourceQuantityToKickOff(String resourceQuantityString, int usersResourceQuantity) throws PlayException {
        int resourceQuantity = ValidationUtil.toValidNumber(resourceQuantityString, log);

        if (resourceQuantity < 0) {
            log.error("Resource quantity could not be below 0");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        if (resourceQuantity > usersResourceQuantity) {
            log.error("User cannot kick of more resources than he has: {} / {}", resourceQuantity, usersResourceQuantity);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        return resourceQuantity;
    }

    public HexBean toValidHex(GameBean game, String hexAbsoluteIdString) throws PlayException {
        int hexAbsoluteId = ValidationUtil.toValidNumber(hexAbsoluteIdString, log);

        for (HexBean hex : game.getHexes()) {
            if (hex.getAbsoluteId() == hexAbsoluteId) {
                return hex;
            }
        }

        log.error("Hex {} does not belong to game {}", hexAbsoluteId, game.getGameId());
        throw new PlayException(ERROR_CODE_ERROR);
    }

    public void validateHexCouldBeRobbed(HexBean hexToRob) throws PlayException {
        if (hexToRob.isRobbed() || hexToRob.getResourceType().equals(HexType.EMPTY)) {
            log.error("Hex {} cannot be robbed", hexToRob.getAbsoluteId());
            throw new PlayException(ERROR_CODE_ERROR);
        }
    }
}
