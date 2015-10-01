'use strict';

angular.module('catan')
        .factory('PlayService', ['Remote', '$q', function (Remote, $q) {
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


            return PlayService;
        }]);