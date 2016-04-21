package catan.services.util.play;

import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.LogCodeType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameLogBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.user.UserBean;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class MessagesUtil {

    public static void updateDisplayedMsg(GameUserBean gameUser, String msgCode) {
        Object[] argsForMsgPattern = getArgsForMsgPattern(gameUser, msgCode);
        if (argsForMsgPattern == null) {
            return;
        }
        String msgToShow = getMsgPattern(gameUser, msgCode).format(argsForMsgPattern);
        gameUser.setDisplayedMessage(msgToShow);
    }

    public static void addLogMsgForGameUsers(LogCodeType logCode, GameBean game) {
        Map<GameUserBean, Object[]> argsForMsgPattern;
        Map<GameUserBean, Boolean> displayedOnTop;

        switch (logCode) {
            case START_GAME:
                argsForMsgPattern = getArgsForMsgPatternStartGameToAllGameUsers(game);
                displayedOnTop = getTrueDisplayedOnTopLogToAllGameUsers(game);
                break;
            default:
                return;
        }

        addLogMsgForGameUsers(logCode, game, argsForMsgPattern, displayedOnTop);
    }

    public static void addLogMsgForGameUsers(LogCodeType logCode, GameBean game, Map<GameUserBean, Resources> producedResourcesForGameUsers) {
        Map<GameUserBean, Object[]> argsForMsgPattern;
        Map<GameUserBean, Boolean> displayedOnTop;

        switch (logCode) {
            case THROW_DICE:
                argsForMsgPattern = getArgsForMsgPatternThrowDiceToAllGameUsers(game, producedResourcesForGameUsers);
                displayedOnTop = getTrueDisplayedOnTopLogToAllGameUsers(game);
                break;
            default:
                return;
        }

        addLogMsgForGameUsers(logCode, game, argsForMsgPattern, displayedOnTop);
    }

    private static void addLogMsgForGameUsers(LogCodeType logCode, GameBean game, Map<GameUserBean, Object[]> argsForMsgPattern, Map<GameUserBean, Boolean> displayedOnTop) {
        for (GameUserBean gameUser : game.getGameUsers()) {
            addLogMsgForGameUser(logCode, gameUser, argsForMsgPattern.get(gameUser), displayedOnTop.get(gameUser));
        }
    }

    private static void addLogMsgForGameUser(LogCodeType logCode, GameUserBean gameUser, Object[] argsForMsgPattern, Boolean displayedOnTop) {
        String msgToShow = getMsgPattern(gameUser, logCode.getLogMsgPatternName()).format(argsForMsgPattern);
        GameLogBean gameLog = new GameLogBean(gameUser, new Date(), logCode, msgToShow, displayedOnTop);
        gameUser.getGameLogs().add(gameLog);
    }

    private static MessageFormat getMsgPattern(GameUserBean gameUser, String key) {
        return new MessageFormat(getMsgs(gameUser).getString(key));
    }

    public static ResourceBundle getMsgs(GameUserBean gameUser) {
        return getMsgs(gameUser.getUser());
    }

    private static ResourceBundle getMsgs(UserBean user) {
        Locale currentLocale = new Locale(user.getLanguage() == null ? "en" : user.getLanguage(), user.getCountry() == null ? "US" : user.getCountry());
        return ResourceBundle.getBundle("i18n.library", currentLocale);
    }

    public static void clearUsersMsgs(GameBean game) {
        for (GameUserBean gameUser : game.getGameUsers()) {
            gameUser.setDisplayedMessage(null);
        }
    }

    private static Map<GameUserBean, Object[]> getArgsForMsgPatternStartGameToAllGameUsers(GameBean game) {
        Map<GameUserBean, Object[]> argsForMsgPattern = new HashMap<GameUserBean, Object[]>();
        GameUserBean activeGameUser = game.fetchActiveGameUser();
        String activeGameUserName = activeGameUser.getUser().getUsername();
        for (GameUserBean gameUser : game.getGameUsers()) {
            boolean isActiveGameUser = gameUser.equals(activeGameUser);
            // {1 - if iterated game user is active, 2 - if not; username of active game user}
            argsForMsgPattern.put(gameUser, new Object[] {(isActiveGameUser ? 1 : 2), activeGameUserName});
        }

        return argsForMsgPattern;
    }

    private static Map<GameUserBean, Object[]> getArgsForMsgPatternThrowDiceToAllGameUsers(GameBean game, Map<GameUserBean, Resources> producedResourcesForGameUsers) {
        Map<GameUserBean, Object[]> argsForMsgPattern = new HashMap<GameUserBean, Object[]>();
        GameUserBean activeGameUser = game.fetchActiveGameUser();
        String activeGameUserName = activeGameUser.getUser().getUsername();
        int diceSum = game.getDiceFirstValue() + game.getDiceSecondValue();
        for (GameUserBean gameUser : game.getGameUsers()) {
            boolean isActiveGameUser = gameUser.equals(activeGameUser);
            Resources producedRes = producedResourcesForGameUsers.get(gameUser);
            int producedResCount = producedRes.calculateSum();
            String resourcesList = "";
            if (producedResCount > 0) {
                Integer producedBrick = producedRes.getBrick();
                if (producedBrick > 0) {
                    resourcesList += String.valueOf(producedBrick) + getMsgPattern(gameUser, "resource_server").format(new Object[] {producedBrick});
                }
                Integer producedWood = producedRes.getWood();
                if (producedWood > 0) {
                    resourcesList += resourcesList.equals("") ? "" : ", ";
                    resourcesList += String.valueOf(producedWood) + getMsgPattern(gameUser, "resource_cable").format(new Object[] {producedWood});
                }
                Integer producedSheep = producedRes.getSheep();
                if (producedSheep > 0) {
                    resourcesList += resourcesList.equals("") ? "" : ", ";
                    resourcesList += String.valueOf(producedSheep) + getMsgPattern(gameUser, "resource_developer").format(new Object[] {producedSheep});
                }
                Integer producedWheat = producedRes.getWheat();
                if (producedWheat > 0) {
                    resourcesList += resourcesList.equals("") ? "" : ", ";
                    resourcesList += String.valueOf(producedWheat) + getMsgPattern(gameUser, "resource_building").format(new Object[] {producedWheat});
                }
                Integer producedStone = producedRes.getStone();
                if (producedStone > 0) {
                    resourcesList += resourcesList.equals("") ? "" : ", ";
                    resourcesList += String.valueOf(producedStone) + getMsgPattern(gameUser, "resource_consultant").format(new Object[] {producedStone});
                }
            }
            // {1 - if iterated game user is active, 2 - if not; username of active game user; dice value; count of produced resources; list of produced resources}
            argsForMsgPattern.put(gameUser, new Object[] {(isActiveGameUser ? 1 : 2), activeGameUserName, diceSum, producedResCount, resourcesList});
        }

        return argsForMsgPattern;
    }

    private static Map<GameUserBean, Boolean> getTrueDisplayedOnTopLogToAllGameUsers(GameBean game) {
        Map<GameUserBean, Boolean> displayedOnTop = new HashMap<GameUserBean, Boolean>();
        for (GameUserBean gameUser : game.getGameUsers()) {
            displayedOnTop.put(gameUser, true);
        }

        return displayedOnTop;
    }

    private static Object[] getArgsForMsgPattern(GameUserBean gameUser, String msgCode) {
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

    private static Object[] getArgsForBuildSettlementMsgPattern(GameUserBean gameUser) {
        //arguments: {username; number of already built offices}
        return new Object[] {gameUser.getUser().getUsername(), gameUser.getBuildingsCount().getSettlements()};
    }

    private static Object[] getArgsForBuildCityMsgPattern(GameUserBean gameUser) {
        //arguments: {username; number of already built business centres}
        return new Object[] {gameUser.getUser().getUsername(), gameUser.getBuildingsCount().getCities()};
    }

    private static Object[] getArgsForBuildRoadMsgPattern(GameUserBean gameUser) {
        //arguments: {username; building on node type 2 - if user should build network near business centre, 1 - near office; }
        return new Object[] {gameUser.getUser().getUsername(), ("CITY".equals(getTypeOfBuildingWithoutRoadsInPreparation(gameUser)) ? 2 : 1), };
    }

    private static Object[] getArgsForMoveRobberMsgPattern(GameUserBean gameUser) {
        //arguments: {username}
        return new Object[] {gameUser.getUser().getUsername()};
    }

    private static Object[] getArgsForChoosePlayerMsgPattern(GameUserBean gameUser) {
        //arguments: {username}
        return new Object[] {gameUser.getUser().getUsername()};
    }

    private static Object[] getArgsForWaitingForKickingOffResMsgPattern(GameUserBean gameUser) {
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

    private static String getTypeOfBuildingWithoutRoadsInPreparation(GameUserBean gameUser) {
        for (NodeBean node : gameUser.getGame().getNodes()) {
            if (node.hasBuildingBelongsToUser(gameUser) && !node.hasNeighbourRoadBelongsToGameUser(gameUser)) {
                return node.getBuilding().getBuilt().toString();
            }
        }
        return null;
    }
}
