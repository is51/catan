package catan.services.impl.bots.smart.utils;

import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BuildRoadUtil extends SmartBotUtil{

    public static boolean hasPlaceForNextBuilding(GameUserBean player) {
        for (EdgeBean possibleRoad : player.getGame().getEdges()) {
            if (possibleRoad.getBuilding() == null || !possibleRoad.getBuilding().getBuildingOwner().equals(player)) {
                continue;
            }

            for (NodeBean possiblePlaceForBuilding : possibleRoad.getNodes().listAllNotNullItems()) {
                if(possiblePlaceForBuilding.getBuilding() != null){
                    continue;
                }

                if (possiblePlaceForBuilding.getBuilding() == null && !isNodeHasNeighbourBuilding(possiblePlaceForBuilding)) {
                    return true;
                }
            }
        }

        return false;
    }


    public static String getEdgeIdOfBestPlaceToBuildRoad(GameUserBean player, int limitRoadLengthToNextBuilding, List<Integer> edgeIds) {
        EdgeBean edgeToBuildRoad = BuildRoadUtil.calculateNextNecessaryRoad(player, limitRoadLengthToNextBuilding, edgeIds);

        return edgeToBuildRoad != null
                ? edgeToBuildRoad.getAbsoluteId().toString()
                : edgeIds.get((int) (Math.random() * edgeIds.size())).toString();
    }

    private static EdgeBean calculateNextNecessaryRoad(GameUserBean player, int limitRoadLengthToNextBuilding, List<Integer> possibleEdgeIds) {
        Map<Double, LinkedList<EdgeBean>> nodeProbabilitiesOfRoadDestinations = new HashMap<Double, LinkedList<EdgeBean>>();

        List<NodeBean> possibleBeginRoadNodes = findNodesAvailableToStartBuildRoad(player, possibleEdgeIds);
        for (NodeBean possibleBeginRoadNode : possibleBeginRoadNodes) {
            for (NodeBean possiblePlaceForBuilding : player.getGame().getNodes()) {
                if (possiblePlaceForBuilding.getBuilding() != null || isNodeHasNeighbourBuilding(possiblePlaceForBuilding)) {
                    //current possiblePlaceForBuilding is not valid for building
                    continue;
                }

                if (possiblePlaceForBuilding.equals(possibleBeginRoadNode)) {
                    continue;
                }

                List<LinkedList<EdgeBean>> ways = new ArrayList<LinkedList<EdgeBean>>();
                calculateRoadsFromNodeToNode(player, possibleBeginRoadNode, possiblePlaceForBuilding, ways, null);
                if (ways.size() == 0) {
                    //not possible to build road of minimum length (10 hardcoded)
                    continue;
                }

                LinkedList<EdgeBean> minLengthWay = ways.get(0);//Default before cycle
                for (LinkedList<EdgeBean> currentLength : ways) {
                    if (minLengthWay.size() > currentLength.size()) {
                        minLengthWay = currentLength;
                    }
                }

                if (minLengthWay.size() <= limitRoadLengthToNextBuilding) {
                    double sumProbabilityForPossiblePlaceForBuilding = calculateSumProbabilityForNode(possiblePlaceForBuilding);
                    //TODO: nodeProbabilitiesOfRoadDestinations.get(sumProbabilityForPossiblePlaceForBuilding) put only if minLengthWay less than existing value in map - need to make 'double' key have the same value
                    nodeProbabilitiesOfRoadDestinations.put(sumProbabilityForPossiblePlaceForBuilding, minLengthWay);
                }
            }
        }


        int minLength = 1000;
        Map<Double, LinkedList<EdgeBean>> nodeProbabilitiesOfMinLengthRoadDestinations = new HashMap<Double, LinkedList<EdgeBean>>();
        for (Map.Entry<Double, LinkedList<EdgeBean>> nodeProbabilityOfRoadDestination : nodeProbabilitiesOfRoadDestinations.entrySet()) {
            Double probability = nodeProbabilityOfRoadDestination.getKey();
            LinkedList<EdgeBean> pathToDestination = nodeProbabilityOfRoadDestination.getValue();

            if (pathToDestination.size() < minLength) {
                minLength = pathToDestination.size();
                nodeProbabilitiesOfMinLengthRoadDestinations.clear();
                nodeProbabilitiesOfMinLengthRoadDestinations.put(probability, pathToDestination);
            } else if (pathToDestination.size() == minLength) {
                nodeProbabilitiesOfMinLengthRoadDestinations.put(probability, pathToDestination);
            }
        }

        double maxProbability = 0;
        LinkedList<EdgeBean> bestWay = null;
        for (Double probability : nodeProbabilitiesOfMinLengthRoadDestinations.keySet()) {
            if (probability > maxProbability) {
                maxProbability = probability;
                bestWay = nodeProbabilitiesOfMinLengthRoadDestinations.get(probability);
            }
        }


        //TODO: check best way calculation
        return bestWay == null || bestWay.size() == 0
                ? null
                : bestWay.get(0);
    }

    private static boolean isNodeHasNeighbourBuilding(NodeBean possiblePlaceForBuilding) {
        boolean nodeHasNeighbourBuilding = false;
        for (EdgeBean edgeOfPossiblePlaceForBuilding : possiblePlaceForBuilding.getEdges().listAllNotNullItems()) {
            for (NodeBean oppositeNode : edgeOfPossiblePlaceForBuilding.getNodes().listAllNotNullItems()) {
                if (!oppositeNode.equals(possiblePlaceForBuilding) && oppositeNode.getBuilding() != null) {
                    nodeHasNeighbourBuilding = true;
                    break;
                }
            }

            if (nodeHasNeighbourBuilding) {
                break;
            }
        }
        return nodeHasNeighbourBuilding;
    }

    private static List<NodeBean> findNodesAvailableToStartBuildRoad(GameUserBean player, List<Integer> possibleEdgeIds) {
        List<NodeBean> nodesAvailableToStartBuildRoad = new ArrayList<NodeBean>();
        for (NodeBean node : player.getGame().getNodes()) {
            if ((player.getGame().getStage() == GameStage.PREPARATION
                    && !node.hasBuildingBelongsToUser(player))
                    || (player.getGame().getStage() == GameStage.MAIN
                    && !node.hasBuildingBelongsToUser(player)
                    && !node.hasNeighbourRoadBelongsToGameUser(player))) {
                continue;
            }

            boolean hasEmptyEdgeToBuildRoad = false;
            for (EdgeBean edge : node.getEdges().listAllNotNullItems()) {
                if (edge.getBuilding() != null || !possibleEdgeIds.contains(edge.getAbsoluteId())) {
                    continue;
                }

                hasEmptyEdgeToBuildRoad = true;
            }

            if (hasEmptyEdgeToBuildRoad) {
                nodesAvailableToStartBuildRoad.add(node);
            }
        }

        return nodesAvailableToStartBuildRoad;
    }

    private static void calculateRoadsFromNodeToNode(GameUserBean player,
                                                     NodeBean sourceNode,
                                                     NodeBean destinationNode,
                                                     List<LinkedList<EdgeBean>> ways,
                                                     List<EdgeBean> path) {
        if (path == null) {
            path = new LinkedList<EdgeBean>();
        }

        if (sourceNode.equals(destinationNode)) {
            ways.add(new LinkedList<EdgeBean>(path));
            return;
        } else if (path.size() > 10) {
            return;
        }

        for (EdgeBean possibleRoadPlace : sourceNode.getEdges().listAllNotNullItems()) {
            if (path.contains(possibleRoadPlace) || possibleRoadPlace.getBuilding() != null) {
                continue;
            }

            NodeBean oppositeToSourceNode = getOppositeNode(possibleRoadPlace, sourceNode);

            path.add(possibleRoadPlace);
            calculateRoadsFromNodeToNode(player, oppositeToSourceNode, destinationNode, ways, path);
            path.remove(possibleRoadPlace);
        }
    }


    private static NodeBean getOppositeNode(EdgeBean edge, NodeBean currentNode) {
        for (NodeBean nodeBean : edge.getNodes().listAllNotNullItems()) {
            if (nodeBean.equals(currentNode)) {
                continue;
            }

            return nodeBean;
        }

        return null;
    }

}
