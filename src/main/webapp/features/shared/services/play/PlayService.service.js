'use strict';

angular.module('catan')
        .factory('PlayService', ['Remote', '$q', 'SelectMapObjectService', function (Remote, $q, SelectMapObjectService) {
            var PlayService = {};

            PlayService.endTurn = function (game) {
                var deferred = $q.defer();

                Remote.play.endTurn({gameId: game.getId()}).then(function() {
                    deferred.resolve();
                }, function(response) {
                    deferred.reject(response);
                });

                return deferred.promise;
            };

            PlayService.throwDice = function (game) {
                return Remote.play.throwDice({gameId: game.getId()});
            };

            PlayService.buildSettlement = function (game) {
                var deferred = $q.defer();

                SelectMapObjectService.requestSelection('node').then(
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

                SelectMapObjectService.requestSelection('node').then(
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

                SelectMapObjectService.requestSelection('edge').then(
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


            return PlayService;
        }]);