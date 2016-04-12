package catan.services.util.play;

import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.user.UserBean;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class MessagesUtil {

    public void updateDisplayedMessage(GameUserBean gameUser, String msgCode) {
        Object[] argsForMsgPattern = getArgsForMsgPattern(gameUser, msgCode);
        if (argsForMsgPattern == null) {
            return;
        }
        String msgToShow = getMsgPattern(gameUser, msgCode).format(argsForMsgPattern);
        gameUser.setDisplayedMessage(msgToShow);
    }

    public MessageFormat getMsgPattern(GameUserBean gameUser, String key) {
        return new MessageFormat(getMsgs(gameUser).getString(key));
    }

    public ResourceBundle getMsgs(GameUserBean gameUser) {
        return getMsgs(gameUser.getUser());
    }

    public ResourceBundle getMsgs(UserBean user) {
        Locale currentLocale = new Locale(user.getLanguage() == null ? "en" : user.getLanguage(), user.getCountry() == null ? "US" : user.getCountry());
        return ResourceBundle.getBundle("i18n.library", currentLocale);
    }

    public void clearUsersMsgs(GameBean game) {
        for (GameUserBean gameUser : game.getGameUsers()) {
            gameUser.setDisplayedMessage(null);
        }
    }

    private Object[] getArgsForMsgPattern(GameUserBean gameUser, String msgCode) {
        if (msgCode.equals("help_msg_build_settlement")) {
            return getArgsForBuildSettlementMsgPattern(gameUser);
        }

        if (msgCode.equals("help_msg_build_city")) {
            return getArgsForBuildCityMsgPattern(gameUser);
        }

        if (msgCode.equals("help_msg_build_road")) {
            return getArgsForBuildRoadMsgPattern(gameUser);
        }

        if (msgCode.equals("help_msg_move_robber")) {
            return getArgsForMoveRobberMsgPattern(gameUser);
        }

        if (msgCode.equals("help_msg_choose_player_to_rob")) {
            return getArgsForChoosePlayerMsgPattern(gameUser);
        }

        if (msgCode.equals("help_msg_wait_for_kicking_off_res")) {
            return getArgsForWaitingForKickingOffResMsgPattern(gameUser);
        }

        throw new IllegalArgumentException("Invalid message code");
    }

    private Object[] getArgsForBuildSettlementMsgPattern(GameUserBean gameUser) {
        //arguments: {number of already built offices; username}
        return new Object[] {gameUser.getBuildingsCount().getSettlements(), gameUser.getUser().getUsername()};
    }

    private Object[] getArgsForBuildCityMsgPattern(GameUserBean gameUser) {
        //arguments: {number of already built business centres; username}
        return new Object[] {gameUser.getBuildingsCount().getCities(), gameUser.getUser().getUsername()};
    }

    private Object[] getArgsForBuildRoadMsgPattern(GameUserBean gameUser) {
        //arguments: {2 - if user should build network near business centre, 1 - near office; username}
        return new Object[] {(getTypeOfBuildingWithoutRoadsInPreparation(gameUser).equals("CITY") ? 2 : 1), gameUser.getUser().getUsername()};
    }

    private Object[] getArgsForMoveRobberMsgPattern(GameUserBean gameUser) {
        //arguments: {username}
        return new Object[] {gameUser.getUser().getUsername()};
    }

    private Object[] getArgsForChoosePlayerMsgPattern(GameUserBean gameUser) {
        //arguments: {username}
        return new Object[] {gameUser.getUser().getUsername()};
    }

    private Object[] getArgsForWaitingForKickingOffResMsgPattern(GameUserBean gameUser) {
        //arguments: {list of users that should kick off their resources; number of users in this list}
        if (gameUser.isKickingOffResourcesMandatory()) {
            return null;
        }
        String waitingListString = "";
        int waitingListSize = 0;
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            if (!gameUserIterated.isKickingOffResourcesMandatory()) {
                continue;
            }
            String userName = gameUserIterated.getUser().getUsername();
            if (!waitingListString.equals("")) {
                waitingListString = waitingListString + ", " + userName;
            } else {
                waitingListString = waitingListString + userName;
            }
            waitingListSize++;
        }
        return new Object[] {waitingListString, waitingListSize};
    }

    private String getTypeOfBuildingWithoutRoadsInPreparation(GameUserBean gameUser) {
        for (NodeBean node : gameUser.getGame().getNodes()) {
            if (node.hasBuildingBelongsToUser(gameUser) && !node.hasNeighbourRoadBelongsToGameUser(gameUser)) {
                return node.getBuilding().getBuilt().toString();
            }
        }
        return null;
    }
}
