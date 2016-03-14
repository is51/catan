'use strict';

angular.module('catan')
        .factory('PlayService', [
            'Remote', '$q', 'SelectService', 'ModalWindowService', 'MapMarkingService',
        function (Remote, $q, SelectService, ModalWindowService, MapMarkingService) {
            var PlayService = {};

            PlayService.endTurn = function (game) {
                beforeAnyAction();
                return Remote.play.endTurn({gameId: game.getId()});
            };

            PlayService.throwDice = function (game) {
                beforeAnyAction();
                return Remote.play.throwDice({gameId: game.getId()});
            };

            PlayService.buyCard = function (game) {
                beforeAnyAction();
                return Remote.play.buyCard({gameId: game.getId()});
            };

            PlayService.buildSettlement = function (game) {
                beforeAnyAction();

                var deferred = $q.defer();

                var availableNodes = game.getCurrentUserAction("BUILD_SETTLEMENT").nodeIds;

                if (availableNodes.length === 0) {
                    deferred.reject("NO_AVAILABLE_PLACES");
                    return deferred.promise;
                }

                MapMarkingService.markNodes(availableNodes, game.getCurrentUser());

                SelectService.requestSelection('node').then(
                        function(nodeId) {
                            Remote.play.buildSettlement({
                                gameId: game.getId(),
                                nodeId: nodeId
                            }).then(function() {
                                deferred.resolve();
                                MapMarkingService.clearMarkingNodes();
                            }, function(response) {
                                deferred.reject(response);
                                MapMarkingService.clearMarkingNodes();
                            });
                        },
                        function(response) {
                            deferred.reject(response);
                            MapMarkingService.clearMarkingNodes();
                        }
                );

                return deferred.promise;
            };

            PlayService.buildCity = function (game) {
                beforeAnyAction();

                var deferred = $q.defer();

                var availableNodes = game.getCurrentUserAction("BUILD_CITY").nodeIds;

                if (availableNodes.length === 0) {
                    deferred.reject("NO_AVAILABLE_PLACES");
                    return deferred.promise;
                }

                MapMarkingService.markNodes(availableNodes, game.getCurrentUser());

                SelectService.requestSelection('node').then(
                        function(nodeId) {
                            Remote.play.buildCity({
                                gameId: game.getId(),
                                nodeId: nodeId
                            }).then(function() {
                                deferred.resolve();
                                MapMarkingService.clearMarkingNodes();
                            }, function(response) {
                                deferred.reject(response);
                                MapMarkingService.clearMarkingNodes();
                            });
                        },
                        function(response) {
                            deferred.reject(response);
                            MapMarkingService.clearMarkingNodes();
                        }
                );

                return deferred.promise;
            };

            PlayService.buildRoad = function (game) {
                beforeAnyAction();

                var deferred = $q.defer();

                var availableEdges = game.getCurrentUserAction("BUILD_ROAD").edgeIds;

                if (availableEdges.length === 0) {
                    deferred.reject("NO_AVAILABLE_PLACES");
                    return deferred.promise;
                }

                MapMarkingService.markEdges(availableEdges, game.getCurrentUser());

                SelectService.requestSelection('edge').then(
                        function(edgeId) {
                            Remote.play.buildRoad({
                                gameId: game.getId(),
                                edgeId: edgeId
                            }).then(function() {
                                deferred.resolve();
                                MapMarkingService.clearMarkingEdges();
                            }, function(response) {
                                deferred.reject(response);
                                MapMarkingService.clearMarkingEdges();
                            });
                        },
                        function(response) {
                            deferred.reject(response);
                            MapMarkingService.clearMarkingEdges();
                        }
                );

                return deferred.promise;
            };

            PlayService.useCardYearOfPlenty = function (game) {
                beforeAnyAction();

                var deferred = $q.defer();

                var windowAndSelectionId = "CARD_YEAR_OF_PLENTY";

                ModalWindowService.show(windowAndSelectionId);

                SelectService.requestSelection(windowAndSelectionId).then(
                        function(response) {
                            Remote.play.useCardYearOfPlenty({
                                gameId: game.getId(),
                                firstResource: response.firstResource,
                                secondResource: response.secondResource
                            }).then(function() {
                                deferred.resolve();
                            }, function(response) {
                                deferred.reject(response);
                            });

                            ModalWindowService.hide(windowAndSelectionId);
                        },
                        function(response) {
                            deferred.reject(response);
                            ModalWindowService.hide(windowAndSelectionId);
                        });

                return deferred.promise;
            };

            PlayService.useCardRoadBuilding = function (game) {
                beforeAnyAction();
                return Remote.play.useCardRoadBuilding({gameId: game.getId()});
            };

            PlayService.useCardMonopoly = function (game) {
                beforeAnyAction();

                var deferred = $q.defer();

                var windowAndSelectionId = "CARD_MONOPOLY";

                ModalWindowService.show(windowAndSelectionId);

                SelectService.requestSelection(windowAndSelectionId).then(
                        function(response) {
                            Remote.play.useCardMonopoly({
                                gameId: game.getId(),
                                resource: response.resource
                            }).then(function(response) {
                                deferred.resolve(response);
                            }, function(response) {
                                deferred.reject(response);
                            });

                            ModalWindowService.hide(windowAndSelectionId);
                        },
                        function(response) {
                            deferred.reject(response);
                            ModalWindowService.hide(windowAndSelectionId);
                        });

                return deferred.promise;
            };

            PlayService.useCardKnight = function (game) {
                beforeAnyAction();
                return Remote.play.useCardKnight({gameId: game.getId()});
            };

            PlayService.moveRobber = function (game) {
                beforeAnyAction();

                var deferred = $q.defer();

                SelectService.requestSelection('hex').then(
                        function(hexId) {
                            Remote.play.moveRobber({
                                gameId: game.getId(),
                                hexId: hexId
                            }).then(function() {
                                deferred.resolve();
                            }, function(response) {
                                deferred.reject(response);
                            });
                        },
                        function(response) {
                            deferred.reject(response);
                        }
                );

                return deferred.promise;
            };

            PlayService.choosePlayerToRob = function (game) {
                beforeAnyAction();

                var deferred = $q.defer();

                var availableNodes = game.getCurrentUserAction("CHOOSE_PLAYER_TO_ROB").nodeIds;
                MapMarkingService.markNodes(availableNodes);

                SelectService.requestSelection('node').then(
                        function(nodeId) {
                            var node = game.getMapObjectById('node', nodeId);

                            if (node.building) {
                                var gameUserId = node.building.ownerGameUserId;

                                Remote.play.choosePlayerToRob({
                                    gameId: game.getId(),
                                    gameUserId: gameUserId
                                }).then(function() {
                                    deferred.resolve();
                                    MapMarkingService.clearMarkingNodes();
                                }, function(response) {
                                    deferred.reject(response);
                                    MapMarkingService.clearMarkingNodes();
                                });
                            } else {
                                deferred.reject();
                                MapMarkingService.clearMarkingNodes();
                            }
                        },
                        function(response) {
                            deferred.reject(response);
                            MapMarkingService.clearMarkingNodes();
                        }
                );

                return deferred.promise;
            };

            PlayService.kickOffResources = function (game) {
                beforeAnyAction();

                var deferred = $q.defer();

                var windowAndSelectionId = "KICK_OFF_RESOURCES";

                ModalWindowService.show(windowAndSelectionId);

                SelectService.requestSelection(windowAndSelectionId).then(
                        function(response) {
                            Remote.play.kickOffResources({
                                gameId: game.getId(),
                                brick: response.brick,
                                wood: response.wood,
                                sheep: response.sheep,
                                wheat: response.wheat,
                                stone: response.stone
                            }).then(function(response) {
                                deferred.resolve(response);
                            }, function(response) {
                                deferred.reject(response);
                            });

                            ModalWindowService.hide(windowAndSelectionId);
                        },
                        function(response) {
                            deferred.reject(response);
                            ModalWindowService.hide(windowAndSelectionId);
                        });

                return deferred.promise;
            };

            PlayService.tradePort = function (game) {
                beforeAnyAction();

                var deferred = $q.defer();

                var selectionId = "TRADE_PORT";

                SelectService.requestSelection(selectionId).then(
                        function(response) {
                            Remote.play.tradePort({
                                gameId: game.getId(),
                                brick: response.brick,
                                wood: response.wood,
                                sheep: response.sheep,
                                wheat: response.wheat,
                                stone: response.stone
                            }).then(function(response) {
                                deferred.resolve(response);
                            }, function(response) {
                                deferred.reject(response);
                            });

                        },
                        function(response) {
                            deferred.reject(response);
                        });

                return deferred.promise;
            };

            PlayService.tradePropose = function (game) {
                beforeAnyAction();

                var deferred = $q.defer();

                var selectionId = "TRADE_PROPOSE";

                SelectService.requestSelection(selectionId).then(
                        function(response) {
                            Remote.play.tradePropose({
                                gameId: game.getId(),
                                brick: response.brick,
                                wood: response.wood,
                                sheep: response.sheep,
                                wheat: response.wheat,
                                stone: response.stone
                            }).then(function(response) {
                                deferred.resolve(response);
                            }, function(response) {
                                deferred.reject(response);
                            });

                        },
                        function(response) {
                            deferred.reject(response);
                        });

                return deferred.promise;
            };

            PlayService.tradeAccept = function (game, offerId) {
                beforeAnyAction();
                return Remote.play.tradeAccept({gameId: game.getId(), offerId: offerId});
            };

            PlayService.tradeDecline = function (game, offerId) {
                beforeAnyAction();
                return Remote.play.tradeDecline({gameId: game.getId(), offerId: offerId});
            };

            return PlayService;

            function beforeAnyAction() {
                SelectService.cancelAllRequestSelections();
            }
        }]);