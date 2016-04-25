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
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
                setArgsForBuildingPatternsForAllGameUsers(argsForMsgPattern, gameUser, "building_office");
                setPatternNameForAllUsers(patternNames, game, "log_msg_build_settlement");
                setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);
                break;

            case BUILD_CITY:
                setArgsForBuildingPatternsForAllGameUsers(argsForMsgPattern, gameUser, "building_business_centre");
                setPatternNameForAllUsers(patternNames, game, "log_msg_build_city");
                setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);
                break;

            case BUILD_ROAD:
                setArgsForBuildingPatternsForAllGameUsers(argsForMsgPattern, gameUser, "building_network");
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

            case TRADE_DECLINE:
                setArgsForPatternTradeDeclineForAllGameUsers(argsForMsgPattern, gameUser);
                setPatternNameForAllUsers(patternNames, game, "log_msg_trade_decline");
                setTrueDisplayedOnTopLogToAllGameUsersButFalseForGameUserWhoMadeAction(displayedOnTop, gameUser);
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

        setPatternNamesAndArgsForPatternThrowDiceForAllGameUsers(patternNames, argsForMsgPattern, game, producedResourcesForGameUsers);
        setSameDisplayedOnTopLogToAllGameUsers(true, displayedOnTop, game);

        addLogMsgForGameUsers(logCode, game, patternNames, argsForMsgPattern, displayedOnTop);
    }

    public static void addLogMsgForGameUsers(LogCodeType logCode, GameUserBean gameUser, Resources kickedOffResources) {
        if (!LogCodeType.DROP_RESOURCES.equals(logCode)) {
            return;
        }

        Map<GameUserBean, Object[]> argsForMsgPattern = new HashMap<GameUserBean, Object[]>();
        Map<GameUserBean, String> patternNames = new HashMap<GameUserBean, String>();
        Map<GameUserBean, Boolean> displayedOnTop = new HashMap<GameUserBean, Boolean>();
        GameBean game = gameUser.getGame();

        setArgsForPatternRobPlayerForAllGameUsers(argsForMsgPattern, gameUser, kickedOffResources);
        setPatternNameForAllUsers(patternNames, game, "log_msg_drop_resources");
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

        setArgsForPatternStealResourceForAllGameUsers(argsForMsgPattern, gameUser, stolenResourceType);
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

        setPatternNamesArgsForPatternUseMonopolyForAllGameUsers(patternNames, argsForMsgPattern, gameUser, quantityOfRes, stolenResourceType);
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

        setArgsForPatternUseYearOfPlentyForAllGameUsers(argsForMsgPattern, gameUser, firstRes, secondRes);
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

        setArgsForPatternBuyCardForAllGameUsers(argsForMsgPattern, gameUser, developmentCard);
        setPatternNameForAllUsers(patternNames, game, "log_msg_buy_card");
        setTrueDisplayedOnTopLogToAllGameUsersButFalseForActiveGameUser(displayedOnTop, game);

        addLogMsgForGameUsers(logCode, game, patternNames, argsForMsgPattern, displayedOnTop);
    }

    public static void addLogMsgForGameUsers(LogCodeType logCode, GameUserBean gameUser, Resources resourcesToSell, Resources resourcesToBuy) {
        Map<GameUserBean, Object[]> argsForMsgPattern = new HashMap<GameUserBean, Object[]>();
        Map<GameUserBean, String> patternNames = new HashMap<GameUserBean, String>();
        Map<GameUserBean, Boolean> displayedOnTop = new HashMap<GameUserBean, Boolean>();
        GameBean game = gameUser.getGame();

        switch (logCode) {
            case TRADE_PORT:
                setArgsForPatternTradePortForAllGameUsers(patternNames, argsForMsgPattern, gameUser, resourcesToSell, resourcesToBuy);
                setSameDisplayedOnTopLogToAllGameUsers(false, displayedOnTop, game);
                break;

            case TRADE_ACCEPT:
                setArgsForPatternTradeAcceptForAllGameUsers(patternNames, argsForMsgPattern, gameUser, resourcesToSell, resourcesToBuy);
                setSameDisplayedOnTopLogToAllGameUsers(true, displayedOnTop, game);
                break;

            case TRADE_PROPOSE:
                setArgsForPatternTradeProposeForAllGameUsers(argsForMsgPattern, gameUser, resourcesToSell, resourcesToBuy);
                setPatternNameForAllUsers(patternNames, game, "log_msg_trade_propose");
                setSameDisplayedOnTopLogToAllGameUsers(false, displayedOnTop, game);
                break;

            default:
                return;
        }

        addLogMsgForGameUsers(logCode, game, patternNames, argsForMsgPattern, displayedOnTop);
    }

    private static void addLogMsgForGameUsers(LogCodeType logCode, GameBean game, Map<GameUserBean, String> patternNames, Map<GameUserBean, Object[]> argsForMsgPattern, Map<GameUserBean, Boolean> displayedOnTop) {
        Set<GameLogBean> gameLogs = new HashSet<GameLogBean>();
        for (GameUserBean gameUser : game.getGameUsers()) {
            String msgToShow = getMsgPattern(gameUser, patternNames.get(gameUser)).format(argsForMsgPattern.get(gameUser));
            boolean gameLogAlreadyExist = false;
            for (GameLogBean gameLog : gameLogs) {
                if (gameLog.isDisplayedOnTop().equals(displayedOnTop.get(gameUser))
                        && gameLog.getCode().equals(logCode)
                        && gameLog.getMessage().equals(msgToShow)) {
                    gameUser.getGameLogs().add(gameLog);
                    gameLog.getGameUsers().add(gameUser);
                    gameLogAlreadyExist = true;
                    break;
                }
            }

            if (!gameLogAlreadyExist) {
                GameLogBean newGameLog = new GameLogBean(gameUser, logCode, msgToShow, displayedOnTop.get(gameUser));
                gameUser.getGameLogs().add(newGameLog);
                gameLogs.add(newGameLog);
            }
        }
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

    private static void setPatternNamesArgsForPatternUseMonopolyForAllGameUsers(Map<GameUserBean, String> patternNames, Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, int stolenResQuantity, HexType stolenResType) {
        String gameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            boolean isRequiredGameUser = gameUserIterated.equals(gameUser);
            //String quantityAndTypeOfRes;
            if (isRequiredGameUser) {
                if (stolenResQuantity == 0) {
                    patternNames.put(gameUserIterated, "log_msg_use_card_monopoly_no_res");
                    String stolenResName = getMsgPattern(gameUserIterated, stolenResType.getPatternName()).format(new Object[] {2});
                    // {stolen res type}
                    argsForMsgPattern.put(gameUserIterated, new Object[] {stolenResName});
                } else {
                    patternNames.put(gameUserIterated, "log_msg_use_card_monopoly_with_details");
                    String stolenResName = getMsgPattern(gameUserIterated, stolenResType.getPatternName()).format(new Object[] {stolenResQuantity});
                    String quantityAndTypeOfRes = String.valueOf(stolenResQuantity) + " " + stolenResName;
                    // {stolen res quantity and type}
                    argsForMsgPattern.put(gameUserIterated, new Object[] {quantityAndTypeOfRes});
                }
            } else {
                patternNames.put(gameUserIterated, "log_msg_use_card_monopoly");
                String stolenResName = getMsgPattern(gameUserIterated, stolenResType.getPatternName()).format(new Object[] {2});
                // {username of required game user; stolen res type}
                argsForMsgPattern.put(gameUserIterated, new Object[] {gameUserName, stolenResName});
            }
        }
    }

    private static void setArgsForPatternStealResourceForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, HexType stolenResType) {
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

    private static void setArgsForPatternUseYearOfPlentyForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, HexType firstRes, HexType secondRes) {
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

    private static void setArgsForPatternBuyCardForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, DevelopmentCard developmentCard) {
        String gameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            boolean isRequiredGameUser = gameUserIterated.equals(gameUser);
            String devCardName = getMsgs(gameUserIterated).getString(developmentCard.getPatternName());
            // {1 - if iterated game user is the same as required, 2 - if not; username of required game user; dev card name}
            argsForMsgPattern.put(gameUserIterated, new Object[] {(isRequiredGameUser ? 1 : 2), gameUserName, devCardName});
        }
    }

    private static void setArgsForBuildingPatternsForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, String buildingPatternName) {
        String gameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            boolean isRequiredGameUser = gameUserIterated.equals(gameUser);
            String buildingName = getMsgPattern(gameUserIterated, buildingPatternName).format(new Object[] {1});
            // {1 - if iterated game user is the same as required, 2 - if not; username of required game user; building name}
            argsForMsgPattern.put(gameUserIterated, new Object[] {(isRequiredGameUser ? 1 : 2), gameUserName, buildingName});
        }
    }

    private static void setArgsForPatternRobPlayerForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, Resources kickedOffRes) {
        String gameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            boolean isRequiredGameUser = gameUserIterated.equals(gameUser);
            String resourcesList = resourcesToString(gameUserIterated, kickedOffRes);
            // {1 - if iterated game user is the same as required, 2 - if not; username of required game user; list of kicked off resources}
            argsForMsgPattern.put(gameUserIterated, new Object[] {(isRequiredGameUser ? 1 : 2), gameUserName, resourcesList});
        }
    }

    private static void setArgsForPatternTradePortForAllGameUsers(Map<GameUserBean, String> patternNames, Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, Resources resourcesToSell, Resources resourcesToBuy) {
        String gameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {

            if (gameUserIterated.equals(gameUser)) {
                String soldResourcesList = resourcesToString(gameUserIterated, resourcesToSell);
                String boughtResourcesList = resourcesToString(gameUserIterated, resourcesToBuy);
                patternNames.put(gameUserIterated, "log_msg_trade_port_with_details");
                // {list of resources to sell; list of resources to buy}
                argsForMsgPattern.put(gameUserIterated, new Object[] {soldResourcesList, boughtResourcesList});

            } else {
                patternNames.put(gameUserIterated, "log_msg_trade_port");
                // {username of active user}
                argsForMsgPattern.put(gameUserIterated, new Object[] {gameUserName});
            }
        }
    }

    private static void setArgsForPatternTradeAcceptForAllGameUsers(Map<GameUserBean, String> patternNames, Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, Resources resourcesToSell, Resources resourcesToBuy) {
        GameBean game = gameUser.getGame();
        GameUserBean activeTraderGameUser = game.fetchActiveGameUser();
        String activeTraderGameUserName = activeTraderGameUser.getUser().getUsername();
        String passiveTraderGameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : game.getGameUsers()) {

            if (gameUserIterated.equals(gameUser)) {
                String soldResourcesList = resourcesToString(gameUserIterated, resourcesToBuy);
                String boughtResourcesList = resourcesToString(gameUserIterated, resourcesToSell);
                patternNames.put(gameUserIterated, "log_msg_trade_accept_with_details");
                // {list of resources to sell for passive; list of resources to buy for passive; username of active trader}
                argsForMsgPattern.put(gameUserIterated, new Object[] {soldResourcesList, boughtResourcesList, activeTraderGameUserName});

            } else if (gameUserIterated.equals(activeTraderGameUser)) {
                String soldResourcesList = resourcesToString(gameUserIterated, resourcesToSell);
                String boughtResourcesList = resourcesToString(gameUserIterated, resourcesToBuy);
                patternNames.put(gameUserIterated, "log_msg_trade_accept_with_details");
                // {list of resources to sell for active; list of resources to buy for active; username of passive trader}
                argsForMsgPattern.put(gameUserIterated, new Object[] {soldResourcesList, boughtResourcesList, passiveTraderGameUserName});

            } else {
                patternNames.put(gameUserIterated, "log_msg_trade_accept");
                // {username of active trader; username of passive trader}
                argsForMsgPattern.put(gameUserIterated, new Object[] {activeTraderGameUserName, passiveTraderGameUserName});
            }
        }
    }

    private static void setArgsForPatternTradeProposeForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser, Resources resourcesToSell, Resources resourcesToBuy) {
        String gameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : gameUser.getGame().getGameUsers()) {
            boolean isActiveGameUser = gameUserIterated.equals(gameUser);
            String sellResourcesList = resourcesToString(gameUserIterated, resourcesToSell);
            String buyResourcesList = resourcesToString(gameUserIterated, resourcesToBuy);
            // {list of resources to sell; list of resources to buy}
            argsForMsgPattern.put(gameUserIterated, new Object[] {(isActiveGameUser ? 1 : 2), gameUserName, sellResourcesList, buyResourcesList});

        }
    }

    private static void setArgsForPatternTradeDeclineForAllGameUsers(Map<GameUserBean, Object[]> argsForMsgPattern, GameUserBean gameUser) {
        GameBean game = gameUser.getGame();
        GameUserBean activeTraderGameUser = game.fetchActiveGameUser();
        String activeTraderGameUserName = activeTraderGameUser.getUser().getUsername();
        String passiveTraderGameUserName = gameUser.getUser().getUsername();
        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            int choiceLimit = gameUserIterated.equals(gameUser)
                    ? 1
                    : (gameUserIterated.equals(activeTraderGameUser)
                        ? 2
                        : 0);
            // {1 - if iterated game user is passive trader, 2 - if active, 0 - other game user; username of passive trader game user; username of active trader game user}
            argsForMsgPattern.put(gameUserIterated, new Object[] {choiceLimit, passiveTraderGameUserName, activeTraderGameUserName});
        }
    }

    private static void setPatternNamesAndArgsForPatternThrowDiceForAllGameUsers(Map<GameUserBean, String> patternNames, Map<GameUserBean, Object[]> argsForMsgPattern, GameBean game, Map<GameUserBean, Resources> producedResourcesForGameUsers) {
        GameUserBean activeGameUser = game.fetchActiveGameUser();
        String activeGameUserName = activeGameUser.getUser().getUsername();
        int diceSum = game.getDiceFirstValue() + game.getDiceSecondValue();
        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            boolean isActiveGameUser = gameUserIterated.equals(activeGameUser);
            if (diceSum == 7) {
                patternNames.put(gameUserIterated, "log_msg_throw_dice_and_robbers_activity");
                // {1 - if iterated game user is active, 2 - if not; username of active game user}
                argsForMsgPattern.put(gameUserIterated, new Object[] {(isActiveGameUser ? 1 : 2), activeGameUserName});
                continue;
            }

            Resources producedRes = producedResourcesForGameUsers.get(gameUserIterated);
            if (producedRes.calculateSum() == 0) {
                patternNames.put(gameUserIterated, "log_msg_throw_dice");
                // {1 - if iterated game user is active, 2 - if not; username of active game user; dice value}
                argsForMsgPattern.put(gameUserIterated, new Object[] {(isActiveGameUser ? 1 : 2), activeGameUserName, diceSum});
                continue;
            }

            String resourcesList = resourcesToString(gameUserIterated, producedRes);
            patternNames.put(gameUserIterated, "log_msg_throw_dice_and_got_resources");
            // {1 - if iterated game user is active, 2 - if not; username of active game user; dice value; list of produced resources}
            argsForMsgPattern.put(gameUserIterated, new Object[] {(isActiveGameUser ? 1 : 2), activeGameUserName, diceSum, resourcesList});
        }
    }

    private static String resourcesToString(GameUserBean gameUser, Resources resources) {
        String resourcesList = "";
        Map<HexType, Integer> resourcesMap = resources.resourcesToMap();
        for (HexType hexType : resourcesMap.keySet()) {
            Integer resQuantity = resourcesMap.get(hexType);
            if (resQuantity > 0) {
                resourcesList += resourcesList.equals("") ? "" : ", ";
                resourcesList += String.valueOf(resQuantity) + " " + getMsgPattern(gameUser, hexType.getPatternName()).format(new Object[] {resQuantity});
            }
        }

        return resourcesList;
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

    private static void setTrueDisplayedOnTopLogToAllGameUsersButFalseForGameUserWhoMadeAction(Map<GameUserBean, Boolean> displayedOnTop, GameUserBean gameUser) {
        GameBean game = gameUser.getGame();
        for (GameUserBean gameUserIterated : game.getGameUsers()) {
            displayedOnTop.put(gameUserIterated, !gameUserIterated.equals(gameUser));
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