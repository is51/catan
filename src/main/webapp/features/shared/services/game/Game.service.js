'use strict';

angular.module('catan')
        .factory('Game', ['GameModel', '$q', 'Remote', function (GameModel, $q, Remote) {

            var Game = function(idOrDetails) {
                return new GameModel(idOrDetails);
            };

            Game.findPublic = function() {
                return this.findByRemoteService("game", "listPublic");
            };

            Game.findCurrent = function() {
                return this.findByRemoteService("game", "listCurrent");
            };

            Game.findByRemoteService = function(remoteGroup, remoteRequest) {
                var deferred = $q.defer();

                Remote[remoteGroup][remoteRequest]()
                    .then(function(response) {
                        var items = [];

                        for (var i = 0, l = response.data.length; i < l; i++) {
                            items.push(Game(response.data[i]));
                        }

                        deferred.resolve(items);
                    }, function() {
                        deferred.reject();
                    });

                return deferred.promise;
            };

            return Game;
        }]);