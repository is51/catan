'use strict';

angular.module('catan')
        .factory('Game', ['GameModel', '$q', 'Remote', '$timeout', function (GameModel, $q, Remote, $timeout) {

            var updatingTimeout = null;

            var Game = {};

            Game.findById = function(id) {
                var deferred = $q.defer();

                Remote.game.details({gameId: id}).then(function(response) {
                    var game = new GameModel(response.data);
                    deferred.resolve(game);
                }, function() {
                    deferred.reject();
                });

                return deferred.promise;
            };

            Game.findAllByType = function(type) {
                // type can be "PUBLIC" or "CURRENT"
                var remoteMethodName = (type === 'CURRENT') ? 'listCurrent' : 'listPublic';
                return this.findAllByRemoteService("game", remoteMethodName);
            };

            Game.findAllByRemoteService = function(remoteGroup, remoteRequest) {
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



            Game.refresh = function(game) {
                var deferred = $q.defer();

                Remote.game.details({gameId: game.getId()}).then(function(response) {
                    angular.extend(game, response.data);

                    deferred.resolve();
                }, function() {
                    deferred.reject();
                });

                return deferred.promise;
            };

            Game.startRefreshing = function(game, delay, onEverySuccess, onEveryError) {
                var self = this;
                this.stopRefreshing();

                (function runTimeout() {
                    updatingTimeout = $timeout(function() {
                        self.refresh(game).then(function() {
                            if (onEverySuccess) {
                                onEverySuccess();
                            }
                            runTimeout();
                        }, function() {
                            if (onEveryError) {
                                onEveryError();
                            }
                            runTimeout();
                        });
                    }, delay);
                })();
            };

            Game.stopRefreshing = function() {
                $timeout.cancel(updatingTimeout);
            };

            return Game;
        }]);