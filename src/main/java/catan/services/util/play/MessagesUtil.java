package catan.services.util.play;

import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.LogCodeType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameLogBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.game.types.DevelopmentCard;
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

    public static void addLogMsgForGameUsers(LogCodeType logCode, GameUserBean gameUser) {
        Map<GameUserBean, Object[]> argsForMsgPattern = new HashMap<GameUserBean, Object[]>();
        Map<GameUserBean, String> patternNames = new HashMap<GameUserBean, String>();
        Map<GameUserBean, Boolean> displayedOnTop = new HashMap<GameUserBean, Boolean>();
        GameBean game = gameUser.getGame();

        switch (logCode) {
            case START_GAME:
                setUserNameToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser);
                setPatternNameForAllUsers(patternNames, game, "log_msg_start_game");
                setSameDisplayedOnTopLogToAllGameUsers(true, displayedOnTop, game);
                break;

            case END_TURN:
                setUserNameToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser);
                setPatternNameForAllUsers(patternNames, game, "log_msg_end_turn");
                setSameDisplayedOnTopLogToAllGameUsers(false, displayedOnTop, game);
                break;

            case FINISH_GAME:
                setUserNameToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser);
                setPatternNameForAllUsers(patternNames, game, "log_msg_finish_game");
                setSameDisplayedOnTopLogToAllGameUsers(true, displayedOnTop, game);
                break;

            case BUILD_SETTLEMENT:
                setUserNameAndBuildingNameToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser, "building_office");
                setPatternNameForAllUsers(patternNames, game, "log_msg_build_settlement");
                setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);
                break;

            case BUILD_CITY:
                setUserNameAndBuildingNameToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser, "building_business_centre");
                setPatternNameForAllUsers(patternNames, game, "log_msg_build_city");
                setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);
                break;

            case BUILD_ROAD:
                setUserNameAndBuildingNameToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser, "building_network");
                setPatternNameForAllUsers(patternNames, game, "log_msg_build_road");
                setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);
                break;

            case NEW_WIDEST_NETWORK:
                setUserNameToPatternArgsForAllGameUsers(argsForMsgPattern, game.getLongestWayOwner());
                setPatternNameForAllUsers(patternNames, game, "log_msg_new_widest_network");
                setSameDisplayedOnTopLogToAllGameUsers(true, displayedOnTop, game);
                break;

            case INTERRUPTED_WIDEST_NETWORK:
                setPatternNameForAllUsers(patternNames, game, "log_msg_interrupted_widest_network");
                setSameDisplayedOnTopLogToAllGameUsers(false, displayedOnTop, game);
                break;

            case NEW_SECURITY_LEADER:
                setUserNameToPatternArgsForAllGameUsers(argsForMsgPattern, game.getBiggestArmyOwner());
                setPatternNameForAllUsers(patternNames, game, "log_msg_new_security_leader");
                setSameDisplayedOnTopLogToAllGameUsers(true, displayedOnTop, game);
                break;

            case MOVE_ROBBER:
                setPatternNameForAllUsers(patternNames, game, "log_msg_move_robber");
                setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);
                break;

            case USE_CARD_KNIGHT:
                setUserNameToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser);
                setPatternNameForAllUsers(patternNames, game, "log_msg_use_card_knight");
                setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);
                break;

            case USE_CARD_ROAD_BUILDING:
                setUserNameToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser);
                setPatternNameForAllUsers(patternNames, game, "log_msg_use_card_road_building");
                setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);
                break;

            default:
                return;
        }

        addLogMsgForGameUsers(logCode, game, patternNames, argsForMsgPattern, displayedOnTop);
    }

    public static void addLogMsgForGameUsers(LogCodeType logCode, GameBean game, Map<GameUserBean, Resources> producedResourcesForGameUsers) {
        if (!LogCodeType.THROW_DICE.equals(logCode)) {
            return;
        }

        Map<GameUserBean, Object[]> argsForMsgPattern = new HashMap<GameUserBean, Object[]>();
        Map<GameUserBean, String> patternNames = new HashMap<GameUserBean, String>();
        Map<GameUserBean, Boolean> displayedOnTop = new HashMap<GameUserBean, Boolean>();

        setPatternNamesAndArgsThrowDiceToAllGameUsers(patternNames, argsForMsgPattern, game, producedResourcesForGameUsers);
        setSameDisplayedOnTopLogToAllGameUsers(true, displayedOnTop, game);

        addLogMsgForGameUsers(logCode, game, patternNames, argsForMsgPattern, displayedOnTop);
    }

    public static void addLogMsgForGameUsers(LogCodeType logCode, GameUserBean gameUser, Resources kickedOffResources) {
        if (!LogCodeType.ROB_PLAYER.equals(logCode)) {
            return;
        }

        Map<GameUserBean, Object[]> argsForMsgPattern = new HashMap<GameUserBean, Object[]>();
        Map<GameUserBean, String> patternNames = new HashMap<GameUserBean, String>();
        Map<GameUserBean, Boolean> displayedOnTop = new HashMap<GameUserBean, Boolean>();
        GameBean game = gameUser.getGame();

        setUserNameAndKickedOffResToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser, kickedOffResources);
        setPatternNameForAllUsers(patternNames, game, "log_msg_rob_player");
        setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveAndThoseWhoMadeActionGameUsers(displayedOnTop, gameUser);

        addLogMsgForGameUsers(logCode, game, patternNames, argsForMsgPattern, displayedOnTop);
    }

    public static void addLogMsgForGameUsers(LogCodeType logCode, GameUserBean gameUser, HexType stolenResourceType) {
        if (!LogCodeType.STEAL_RESOURCE.equals(logCode)) {
            return;
        }

        Map<GameUserBean, Object[]> argsForMsgPattern = new HashMap<GameUserBean, Object[]>();
        Map<GameUserBean, String> patternNames = new HashMap<GameUserBean, String>();
        Map<GameUserBean, Boolean> displayedOnTop = new HashMap<GameUserBean, Boolean>();
        GameBean game = gameUser.getGame();

        setUserNameAndStolenResTypeToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser, stolenResourceType);
        setPatternNameForAllUsers(patternNames, game, "log_msg_steal_resource");
        setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);

        addLogMsgForGameUsers(logCode, game, patternNames, argsForMsgPattern, displayedOnTop);
    }

    public static void addLogMsgForGameUsers(LogCodeType logCode, GameUserBean gameUser, int quantityOfRes, HexType stolenResourceType) {
        if (!LogCodeType.USE_CARD_MONOPOLY.equals(logCode)) {
            return;
        }

        Map<GameUserBean, Object[]> argsForMsgPattern = new HashMap<GameUserBean, Object[]>();
        Map<GameUserBean, String> patternNames = new HashMap<GameUserBean, String>();
        Map<GameUserBean, Boolean> displayedOnTop = new HashMap<GameUserBean, Boolean>();
        GameBean game = gameUser.getGame();

        setUserNameAndStolenResQuantityAndTypeToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser, quantityOfRes, stolenResourceType);
        setPatternNameForAllUsers(patternNames, game, "log_msg_use_card_monopoly");
        setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);

        addLogMsgForGameUsers(logCode, game, patternNames, argsForMsgPattern, displayedOnTop);
    }

    public static void addLogMsgForGameUsers(LogCodeType logCode, GameUserBean gameUser, HexType firstRes, HexType secondRes) {
        if (!LogCodeType.USE_CARD_YEAR_OF_PLENTY.equals(logCode)) {
            return;
        }

        Map<GameUserBean, Object[]> argsForMsgPattern = new HashMap<GameUserBean, Object[]>();
        Map<GameUserBean, String> patternNames = new HashMap<GameUserBean, String>();
        Map<GameUserBean, Boolean> displayedOnTop = new HashMap<GameUserBean, Boolean>();
        GameBean game = gameUser.getGame();

        setUserNameAndGottenResQuantityAndTypeToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser, firstRes, secondRes);
        setPatternNameForAllUsers(patternNames, game, "log_msg_use_card_year_of_plenty");
        setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);

        addLogMsgForGameUsers(logCode, game, patternNames, argsForMsgPattern, displayedOnTop);
    }

    public static void addLogMsgForGameUsers(LogCodeType logCode, GameUserBean gameUser, DevelopmentCard developmentCard) {
        if (!LogCodeType.BUY_CARD.equals(logCode)) {
            return;
        }

        Map<GameUserBean, Object[]> argsForMsgPattern = new HashMap<GameUserBean, Object[]>();
        Map<GameUserBean, String> patternNames = new HashMap<GameUserBean, String>();
        Map<GameUserBean, Boolean> displayedOnTop = new HashMap<GameUserBean, Boolean>();
        GameBean game = gameUser.getGame();

        setUserNameAndBoughtCardToPatternArgsForAllGameUsers(argsForMsgPattern, gameUser, developmentCard);
        setPatternNameForAllUsers(patternNames, game, "log_msg_buy_card");
        setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);

        addLogMsgForGameUsers(logCode, game, patternNames, argsForMsgPattern, displayedOnTop);
    }

    private static void addLogMsgForGameUsers(LogCodeType logCode, GameBean game, Map<GameUserBean, String> patternNames, Map<GameUserBean, Object[]> argsForMsgPattern, Map<GameUserBean, Boolean> displayedOnTop) {
        for (GameUserBean gameUser : game.getGameUsers()) {
            addLogMsgForGameUser(logCode, gameUser, patternNames.get(gameUser), argsForMsgPattern.get(gameUser), displayedOnTop.get(gameUser));
        }
    }

    private static void addLogMsgForGameUser(LogCodeType logCode, GameUserBean gameUser, String patternName, Object[] argsForMsgPattern, Boolean displayedOnTop) {
        String msgToShow = getMsgPattern(gameUser, patternName).format(argsForMsgPattern);
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

    private static void setPatternNameForAllUsers(Map<GameUserBean, String> patternNames, GameBean game, String patternName) {
        for (GameUserBean gameUser : game.getGameUsers()) {
            patternNames.put(gameUser, patternName);
        }
    }

    private static void setUserNameToPatternArgsForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser) {
        String gameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            boolean isRequiredGameUser = gameUserIterated.equals(gameUser);
            // {1 - if iterated game user is the same as required, 2 - if not; username of required game user}
            argsForMsgPattern.put(gameUserIterated, new Object[] {(isRequiredGameUser ? 1 : 2), gameUserName});
        }
    }

    private static void setUserNameAndStolenResQuantityAndTypeToPatternArgsForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, int stolenResQuantity, HexType stolenResType) {
        String gameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            boolean isRequiredGameUser = gameUserIterated.equals(gameUser);
            String quantityAndTypeOfRes;
            if (isRequiredGameUser) {
                String stolenResName = getMsgPattern(gameUserIterated, stolenResType.getPatternName()).format(new Object[] {stolenResQuantity});
                quantityAndTypeOfRes = String.valueOf(stolenResQuantity) + " " + stolenResName;
            } else {
                quantityAndTypeOfRes = getMsgPattern(gameUserIterated, stolenResType.getPatternName()).format(new Object[] {Double.POSITIVE_INFINITY});
            }
            // {1 - if iterated game user is the same as required, 2 - if not; username of required game user; stolen res quantity and type}
            argsForMsgPattern.put(gameUserIterated, new Object[] {(isRequiredGameUser ? 1 : 2), gameUserName, quantityAndTypeOfRes});
        }
    }
    private static void setUserNameAndStolenResTypeToPatternArgsForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, HexType stolenResType) {
        String robbedGameUserName = gameUser.getUser().getUsername();
        GameBean game = gameUser.getGame();
        GameUserBean activeGameUser = game.fetchActiveGameUser();
        String activeGameUserName = activeGameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            int choiceLimit = gameUserIterated.equals(gameUser)
                    ? 1
                    : (gameUserIterated.equals(activeGameUser)
                    ? 2
                    : 0);
            String stolenResName = getMsgPattern(gameUserIterated, stolenResType.getPatternName()).format(new Object[] {1});
            // {1 - if iterated game user is robbed, 2 - if active, 0 - other game user; username of robbed game user; stolen res name; username of active game user}
            argsForMsgPattern.put(gameUserIterated, new Object[] {choiceLimit, robbedGameUserName, stolenResName, activeGameUserName});
        }
    }

    private static void setUserNameAndGottenResQuantityAndTypeToPatternArgsForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, HexType firstRes, HexType secondRes) {
        String gameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            boolean isRequiredGameUser = gameUserIterated.equals(gameUser);
            String resourcesList;
            if (firstRes.equals(secondRes)) {
                resourcesList = "2 " + getMsgPattern(gameUserIterated, firstRes.getPatternName()).format(new Object[] {2});
            } else {
                resourcesList = getMsgPattern(gameUserIterated, firstRes.getPatternName()).format(new Object[] {1})
                        + ", " + getMsgPattern(gameUserIterated, secondRes.getPatternName()).format(new Object[] {1});
            }
            // {1 - if iterated game user is the same as required, 2 - if not; username of required game user, gotten resources}
            argsForMsgPattern.put(gameUserIterated, new Object[] {(isRequiredGameUser ? 1 : 2), gameUserName, resourcesList});
        }
    }

    private static void setUserNameAndBoughtCardToPatternArgsForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, DevelopmentCard developmentCard) {
        String gameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            boolean isRequiredGameUser = gameUserIterated.equals(gameUser);
            String devCardName = getMsgs(gameUser).getString(developmentCard.getPatternName());
            // {1 - if iterated game user is the same as required, 2 - if not; username of required game user; dev card name}
            argsForMsgPattern.put(gameUserIterated, new Object[] {(isRequiredGameUser ? 1 : 2), gameUserName, devCardName});
        }
    }

    private static void setUserNameAndBuildingNameToPatternArgsForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, String buildingPatternName) {
        String gameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            boolean isRequiredGameUser = gameUserIterated.equals(gameUser);
            String buildingName = getMsgPattern(gameUserIterated, buildingPatternName).format(new Object[] {1});
            // {1 - if iterated game user is the same as required, 2 - if not; username of required game user; building name}
            argsForMsgPattern.put(gameUserIterated, new Object[] {(isRequiredGameUser ? 1 : 2), gameUserName, buildingName});
        }
    }

    private static void setUserNameAndKickedOffResToPatternArgsForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, Resources kickedOffRes) {
        String gameUserName = gameUser.getUser().getUsername();
        String resourcesList = resourcesToString(gameUser, kickedOffRes);
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            boolean isRequiredGameUser = gameUserIterated.equals(gameUser);
            // {1 - if iterated game user is the same as required, 2 - if not; username of required game user; list of kicked off resources}
            argsForMsgPattern.put(gameUserIterated, new Object[] {(isRequiredGameUser ? 1 : 2), gameUserName, resourcesList});
        }
    }

    private static void setPatternNamesAndArgsThrowDiceToAllGameUsers(Map<GameUserBean, String> patternNames, Map<GameUserBean, Object[]> argsForMsgPattern, GameBean game, Map<GameUserBean, Resources> producedResourcesForGameUsers) {
        GameUserBean activeGameUser = game.fetchActiveGameUser();
        String activeGameUserName = activeGameUser.getUser().getUsername();
        int diceSum = game.getDiceFirstValue() + game.getDiceSecondValue();
        for (GameUserBean gameUser : game.getGameUsers()) {
            boolean isActiveGameUser = gameUser.equals(activeGameUser);
            if (diceSum == 7) {
                patternNames.put(gameUser, "log_msg_throw_dice_and_robbers_activity");
                // {1 - if iterated game user is active, 2 - if not; username of active game user}
                argsForMsgPattern.put(gameUser, new Object[] {(isActiveGameUser ? 1 : 2), activeGameUserName});
                continue;
            }

            Resources producedRes = producedResourcesForGameUsers.get(gameUser);
            if (producedRes.calculateSum() == 0) {
                patternNames.put(gameUser, "log_msg_throw_dice");
                // {1 - if iterated game user is active, 2 - if not; username of active game user; dice value}
                argsForMsgPattern.put(gameUser, new Object[] {(isActiveGameUser ? 1 : 2), activeGameUserName, diceSum});
                continue;
            }

            String resourcesList = resourcesToString(gameUser, producedRes);
            patternNames.put(gameUser, "log_msg_throw_dice_and_got_resources");
            // {1 - if iterated game user is active, 2 - if not; username of active game user; dice value; list of produced resources}
            argsForMsgPattern.put(gameUser, new Object[] {(isActiveGameUser ? 1 : 2), activeGameUserName, diceSum, resourcesList});
        }
    }

    private static String resourcesToString(GameUserBean gameUser, Resources resources) {
        String resourcesList = "";
        addResQuantityToList(gameUser, resources, resourcesList, HexType.BRICK);
        addResQuantityToList(gameUser, resources, resourcesList, HexType.WOOD);
        addResQuantityToList(gameUser, resources, resourcesList, HexType.SHEEP);
        addResQuantityToList(gameUser, resources, resourcesList, HexType.WHEAT);
        addResQuantityToList(gameUser, resources, resourcesList, HexType.STONE);
        return resourcesList;
    }

    private static void addResQuantityToList(GameUserBean gameUser, Resources producedRes, String resourcesList, HexType resType) {
        Integer producedResQuantity = producedRes.quantityOf(resType);
        if (producedResQuantity > 0) {
            resourcesList += resourcesList.equals("") ? "" : ", ";
            resourcesList += String.valueOf(producedResQuantity) + getMsgPattern(gameUser, resType.getPatternName()).format(new Object[] {producedResQuantity});
        }
    }

    private static void setSameDisplayedOnTopLogToAllGameUsers(boolean valueToSet, Map<GameUserBean, Boolean> displayedOnTop, GameBean game) {
        for (GameUserBean gameUser : game.getGameUsers()) {
            displayedOnTop.put(gameUser, valueToSet);
        }
    }

    private static void setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(Map<GameUserBean, Boolean> displayedOnTop, GameBean game) {
        GameUserBean activeGameUser = game.fetchActiveGameUser();
        for (GameUserBean gameUser : game.getGameUsers()) {
            displayedOnTop.put(gameUser, !gameUser.equals(activeGameUser));
        }
    }

    private static void setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveAndThoseWhoMadeActionGameUsers(Map<GameUserBean, Boolean> displayedOnTop, GameUserBean gameUser) {
        GameBean game = gameUser.getGame();
        GameUserBean activeGameUser = game.fetchActiveGameUser();
        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            displayedOnTop.put(gameUserIterated, (!gameUserIterated.equals(gameUser) && !gameUserIterated.equals(activeGameUser)));
        }
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