'use strict';

angular.module('catan')
        .factory('GameService', ['GameModel', '$q', 'Remote', '$timeout', function (GameModel, $q, Remote, $timeout) {

            var updatingTimeout = null;

            var GameService = {};

            GameService.findById = function(id) {
                var deferred = $q.defer();

                Remote.game.details({gameId: id}).then(function(response) {
                    var game = new GameModel(response.data);
                    deferred.resolve(game);
                }, function() {
                    deferred.reject();
                });

                return deferred.promise;
            };

            GameService.findAllByType = function(type) {
                // type can be "PUBLIC" or "CURRENT"
                var remoteMethodName = (type === 'CURRENT') ? 'listCurrent' : 'listPublic';
                return this.findAllByRemoteService("game", remoteMethodName);
            };

            GameService.findAllByRemoteService = function(remoteGroup, remoteRequest) {
                var deferred = $q.defer();

                Remote[remoteGroup][remoteRequest]()
                    .then(function(response) {
                        var items = [];

                        for (var i = 0, l = response.data.length; i < l; i++) {
                            items.push(new GameModel(response.data[i]));
                        }

                        deferred.resolve(items);
                    }, function() {
                        deferred.reject();
                    });

                return deferred.promise;
            };



            GameService.refresh = function(game) {
                var deferred = $q.defer();

                Remote.game.details({gameId: game.getId()}).then(function(response) {
                    refreshGameModel(game, response.data);

                    deferred.resolve();
                }, function() {
                    deferred.reject();
                });

                return deferred.promise;
            };

            GameService.startRefreshing = function(game, delay, onEverySuccess, onEveryError) {
                var self = this;
                this.stopRefreshing();

                (function runTimeout() {
                    updatingTimeout = $timeout(function() {
                        self.refresh(game).then(function() {
                            var continueUpdating = true;
                            if (onEverySuccess) {
                                continueUpdating = onEverySuccess() !== false;
                            }
                            if (continueUpdating) {
                                runTimeout();
                            }
                        }, function() {
                            var continueUpdating = true;
                            if (onEveryError) {
                                continueUpdating = onEveryError() !== false;
                            }
                            if (continueUpdating) {
                                runTimeout();
                            }
                        });
                    }, delay);
                })();
            };

            GameService.stopRefreshing = function() {
                $timeout.cancel(updatingTimeout);
            };

            return GameService;


            function refreshGameModel(game, data) {
                // We need replace all properties of game except "map". Map should be merged
                var currentGameMap = game.map;
                angular.extend(game, data);
                angular.merge(currentGameMap, game.map);
                game.map = currentGameMap;
            }
        }]);