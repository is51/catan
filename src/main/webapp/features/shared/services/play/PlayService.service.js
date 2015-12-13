'use strict';

angular.module('catan')
        .factory('PlayService', [
            'Remote', '$q', 'SelectService', 'ChooseResourcesWindowService',
        function (Remote, $q, SelectService, ChooseResourcesWindowService) {
            var PlayService = {};

            PlayService.endTurn = function (game) {
                return Remote.play.endTurn({gameId: game.getId()});
            };

            PlayService.throwDice = function (game) {
                return Remote.play.throwDice({gameId: game.getId()});
            };

            PlayService.buyCard = function (game) {
                return Remote.play.buyCard({gameId: game.getId()});
            };

            PlayService.buildSettlement = function (game) {
                var deferred = $q.defer();

                SelectService.requestSelection('node').then(
                        function(nodeId) {
                            Remote.play.buildSettlement({
                                gameId: game.getId(),
                                nodeId: nodeId
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

            PlayService.buildCity = function (game) {
                var deferred = $q.defer();

                SelectService.requestSelection('node').then(
                        function(nodeId) {
                            Remote.play.buildCity({
                                gameId: game.getId(),
                                nodeId: nodeId
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

            PlayService.buildRoad = function (game) {
                var deferred = $q.defer();

                SelectService.requestSelection('edge').then(
                        function(edgeId) {
                            Remote.play.buildRoad({
                                gameId: game.getId(),
                                edgeId: edgeId
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

            PlayService.useCardYearOfPlenty = function (game) {
                var deferred = $q.defer();

                var windowAndSelectionId = "CARD_YEAR_OF_PLENTY";

                ChooseResourcesWindowService.show(windowAndSelectionId);

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

                            ChooseResourcesWindowService.hide();
                        },
                        function(response) {
                            deferred.reject(response);
                            ChooseResourcesWindowService.hide();
                        });

                return deferred.promise;
            };

            PlayService.useCardRoadBuilding = function (game) {
                return Remote.play.useCardRoadBuilding({gameId: game.getId()});
            };
            PlayService.useCardMonopoly = function (game) {
                var deferred = $q.defer();

                var windowAndSelectionId = "CARD_MONOPOLY";

                ChooseResourcesWindowService.show(windowAndSelectionId);

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

                            ChooseResourcesWindowService.hide();
                        },
                        function(response) {
                            deferred.reject(response);
                            ChooseResourcesWindowService.hide();
                        });

                return deferred.promise;
            };

            PlayService.moveRobber = function (game) {
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
                var deferred = $q.defer();

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
                                }, function(response) {
                                    deferred.reject(response);
                                });
                            } else {
                                deferred.reject();
                            }
                        },
                        function(response) {
                            deferred.reject(response);
                        }
                );

                return deferred.promise;
            };


            return PlayService;
        }]);