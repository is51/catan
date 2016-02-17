package catan.services.util.play;

import catan.domain.exception.GameException;
import catan.domain.model.dashboard.Building;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.actions.BuildOnEdgeParams;
import catan.domain.model.game.actions.BuildOnNodeParams;
import catan.domain.model.game.actions.ResourcesParams;
import catan.domain.model.game.actions.TradingParams;
import catan.domain.model.game.types.DevelopmentCard;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainStageUtil {
    private Logger log = LoggerFactory.getLogger(MainStageUtil.class);

    private static final Gson GSON = new Gson();

    private ActionParamsUtil actionParamsUtil;

    public void updateNextMove(GameBean game) {
        Integer nextMoveNumber = game.getCurrentMove().equals(game.getGameUsers().size())
                ? 1
                : game.getCurrentMove() + 1;

        log.debug("Next move order in {} stage is changing from {} to {}", game.getStage(), game.getCurrentMove(), nextMoveNumber);
        game.setCurrentMove(nextMoveNumber);
    }

    public void takeResourceFromPlayer(Resources usersResources, HexType resource, int quantityToDecrease) {
        int newResourceQuantity = usersResources.quantityOf(resource) - quantityToDecrease;
        usersResources.updateResourceQuantity(resource, newResourceQuantity);
    }

    public void resetDices(GameBean game) {
        game.setDiceThrown(false);
        game.setDiceFirstValue(null);
        game.setDiceSecondValue(null);
    }

    public void produceResourcesFromActiveDiceHexes(List<HexBean> hexes) {
        for (HexBean hex : hexes) {
            if (hex.isRobbed()) {
                log.debug("Ignoring for produce resources from robbed " + hex);
                continue;
            }
            for (NodeBean node : hex.fetchNodesWithBuildings()) {
                produceResources(hex, node.getBuilding());
            }
        }
    }

    private void produceResources(HexBean sourceHex, Building<NodeBuiltType> consumingBuilding) {
        HexType resourceType =  sourceHex.getResourceType();
        GameUserBean buildingOwner = consumingBuilding.getBuildingOwner();
        Resources userResources = buildingOwner.getResources();

        int currentResourceQuantity = userResources.quantityOf(resourceType);
        int resourceQuantityToAdd = consumingBuilding.getBuilt().getResourceQuantityToAdd();

        userResources.updateResourceQuantity(resourceType, currentResourceQuantity + resourceQuantityToAdd);

        log.debug("GameUser (name: " + buildingOwner.getUser().getUsername() + ", colorId: " + buildingOwner.getColorId() + ", id: " +buildingOwner.getGameUserId() + ")" +
                " got " + resourceQuantityToAdd + " " +  resourceType + " for " + consumingBuilding.getBuilt() + " at " + sourceHex);
    }

    public void updateAvailableActionsForAllUsers(GameBean game) throws GameException {
        for (GameUserBean gameUser : game.getGameUsers()) {
            updateAvailableActionsForUser(gameUser, game);
        }
    }

    private void updateAvailableActionsForUser(GameUserBean gameUser, GameBean game) {
        List<Action> actionsList = new ArrayList<Action>();
        boolean isMandatory = false;

        allowKickingOffResourcesMandatory(gameUser, game, actionsList);
        allowMoveRobberMandatory(gameUser, game, actionsList);
        allowChoosePlayerToRob(gameUser, game, actionsList);
        allowBuildRoadMandatory(gameUser, game, actionsList);
        allowTradeReply(gameUser, game, actionsList);
        if (actionsList.size() > 0) {
            isMandatory = true;
        } else if (noOneNeedsToKickOfResourcesOrTradeReply(game)) {
            allowBuildSettlement(gameUser, game, actionsList);
            allowBuildCity(gameUser, game, actionsList);
            allowBuildRoad(gameUser, game, actionsList);
            allowEndTurn(gameUser, game, actionsList);
            allowThrowDice(gameUser, game, actionsList);
            allowBuyCard(gameUser, game, actionsList);
            allowUseCardYearOfPlenty(gameUser, game, actionsList);
            allowUseCardMonopoly(gameUser, game, actionsList);
            allowUseCardRoadBuilding(gameUser, game, actionsList);
            allowUseCardKnight(gameUser, game, actionsList);
            allowPortTrading(gameUser, game, actionsList);
            allowProposeTrade(gameUser, game, actionsList);
        }

        AvailableActions availableActions = new AvailableActions();
        availableActions.setList(actionsList);
        availableActions.setIsMandatory(isMandatory);

        String availableActionsString = GSON.toJson(availableActions, AvailableActions.class);
        gameUser.setAvailableActions(availableActionsString);
    }

    private void allowKickingOffResourcesMandatory(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && gameUser.isKickingOffResourcesMandatory()) {
            actionsList.add(new Action(GameUserActionCode.KICK_OFF_RESOURCES));
        }
    }

    private void allowMoveRobberMandatory(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isRobberShouldBeMovedMandatory()) {
            actionsList.add(new Action(GameUserActionCode.MOVE_ROBBER));
        }
    }

    private void allowChoosePlayerToRob(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isChoosePlayerToRobMandatory()) {
            actionsList.add(new Action(GameUserActionCode.CHOOSE_PLAYER_TO_ROB));
        }
    }

    private void allowBuildRoadMandatory(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.getRoadsToBuildMandatory() > 0) {
            actionsList.add(new Action(GameUserActionCode.BUILD_ROAD));
        }
    }

    private void allowTradeReply(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && gameUser.isAvailableTradeReply()
                && game.getTradeProposal() != null
                && game.getTradeProposal().getOfferId() != null) {
            TradingParams tradingParams = new TradingParams(game.getTradeProposal());
            actionsList.add(new Action(GameUserActionCode.TRADE_REPLY, tradingParams));
        }
    }

    private void allowThrowDice(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && !game.isDiceThrown()) {
            actionsList.add(new Action(GameUserActionCode.THROW_DICE));
        }
    }

    private void allowEndTurn(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isDiceThrown()) {
            actionsList.add(new Action(GameUserActionCode.END_TURN));
        }
    }

    private void allowBuildCity(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isDiceThrown()
                && userHasResourcesToBuildCity(gameUser)) {
            BuildOnNodeParams buildOnNodeParams = new BuildOnNodeParams(actionParamsUtil.calculateBuildCityParams(game, gameUser));
            actionsList.add(new Action(GameUserActionCode.BUILD_CITY, buildOnNodeParams));
        }
    }

    private void allowBuildSettlement(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isDiceThrown()
                && userHasResourcesForSettlement(gameUser)) {
            BuildOnNodeParams buildOnNodeParams = new BuildOnNodeParams(actionParamsUtil.calculateBuildSettlementParams(game, gameUser));
            actionsList.add(new Action(GameUserActionCode.BUILD_SETTLEMENT, buildOnNodeParams));
        }
    }

    private void allowBuildRoad(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isDiceThrown()
                && userHasResourcesToBuildRoad(gameUser)) {
            BuildOnEdgeParams buildOnEdgeParams = new BuildOnEdgeParams(actionParamsUtil.calculateBuildRoadParams(game, gameUser));
            actionsList.add(new Action(GameUserActionCode.BUILD_ROAD, buildOnEdgeParams));
        }
    }

    private void allowBuyCard(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isDiceThrown()
                && userHasResourcesToBuyCard(gameUser)) {
            actionsList.add(new Action(GameUserActionCode.BUY_CARD));
        }
    }

    private void allowUseCardYearOfPlenty(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isDiceThrown()
                && userHasCard(gameUser, DevelopmentCard.YEAR_OF_PLENTY)) {
            actionsList.add(new Action(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY));
        }
    }

    private void allowUseCardMonopoly(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isDiceThrown()
                && userHasCard(gameUser, DevelopmentCard.MONOPOLY)) {
            actionsList.add(new Action(GameUserActionCode.USE_CARD_MONOPOLY));
        }
    }

    private void allowUseCardRoadBuilding(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isDiceThrown()
                && userHasCard(gameUser, DevelopmentCard.ROAD_BUILDING)) {
            actionsList.add(new Action(GameUserActionCode.USE_CARD_ROAD_BUILDING));
        }
    }

    private void allowUseCardKnight(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && userHasCard(gameUser, DevelopmentCard.KNIGHT)) {
            actionsList.add(new Action(GameUserActionCode.USE_CARD_KNIGHT));
        }
    }

    private void allowProposeTrade(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isDiceThrown()) {
            actionsList.add(new Action(GameUserActionCode.TRADE_PROPOSE));
        }
    }

    private void allowPortTrading(GameUserBean gameUser, GameBean game, List<Action> actionsList) {
        if (gameNotFinished(game)
                && isCurrentUsersMove(gameUser, game)
                && game.isDiceThrown()) {
            ResourcesParams resourcesParams = actionParamsUtil.calculateResourcesParams(gameUser, game);
            actionsList.add(new Action(GameUserActionCode.TRADE_PORT, resourcesParams));
        }
    }

    private boolean userHasCard(GameUserBean gameUser, DevelopmentCard developmentCard) {
        return gameUser.getDevelopmentCards().quantityOf(developmentCard) > 0;
    }

    private boolean userHasResourcesToBuildCity(GameUserBean gameUser) {
        return gameUser.getResources().getStone() >= 3
                && gameUser.getResources().getWheat() >= 2;
    }

    private boolean userHasResourcesForSettlement(GameUserBean gameUser) {
        return gameUser.getResources().getWood() >= 1
                && gameUser.getResources().getBrick() >= 1
                && gameUser.getResources().getSheep() >= 1
                && gameUser.getResources().getWheat() >= 1;
    }

    private boolean userHasResourcesToBuildRoad(GameUserBean gameUser) {
        return gameUser.getResources().getWood() >= 1
                && gameUser.getResources().getBrick() >= 1;
    }

    private boolean userHasResourcesToBuyCard(GameUserBean gameUser) {
        return gameUser.getResources().getStone() >= 1
                && gameUser.getResources().getSheep() >= 1
                && gameUser.getResources().getWheat() >= 1;
    }

    private boolean isCurrentUsersMove(GameUserBean gameUser, GameBean game) {
        return gameUser.getMoveOrder() == game.getCurrentMove();
    }

    private boolean gameNotFinished(GameBean game) {
        return !GameStatus.FINISHED.equals(game.getStatus());
    }

    private boolean noOneNeedsToKickOfResourcesOrTradeReply (GameBean game) {
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.isKickingOffResourcesMandatory()) {
                return false;
            }
        }

        return game.getTradeProposal() == null || game.getTradeProposal().getOfferId() == null;
    }

    @Autowired
    public void setActionParamsUtil(ActionParamsUtil actionParamsUtil) {
        this.actionParamsUtil = actionParamsUtil;
    }
}
