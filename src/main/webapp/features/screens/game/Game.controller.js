'use strict';

angular.module('catan')
    .controller('GameController', [
            '$scope', '$stateParams', '$state', 'Remote', '$timeout', '$q',
            function($scope, $stateParams, $state, Remote, $timeout, $q) {

        var GAME_ROOM_UPDATE_SECONDS = 5;

        $scope.game = null;

        $scope.updateGame = function() {

            var deferred = $q.defer();

            Remote.game.details({gameId: $stateParams.gameId})
                .then(function(response) {
                    $scope.game = response.data;
                    deferred.resolve();
                }, function(response) {
                    alert('Getting Game Details Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                    $state.go("start");
                    deferred.reject();
                });

            return deferred.promise;
        };


        (function updateGameAndSetTimeout() {
            $scope.updateGame().then(function() {
                $timeout(function() {
                    updateGameAndSetTimeout();
                }, GAME_ROOM_UPDATE_SECONDS * 1000);
            });
        })();

    }]);