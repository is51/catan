'use strict';

angular.module('catan')
    .controller('GameController', [
            '$scope', '$stateParams', '$state', 'GameService',
            function($scope, $stateParams, $state, GameService)
    {

        var GAME_UPDATE_DELAY = 5000;

        $scope.game = null;

        GameService.findById(+$stateParams.gameId)
            .then(function(game) {
                $scope.game = game;

                    GameService.startRefreshing($scope.game, GAME_UPDATE_DELAY, null, function() {
                    alert('Getting Game Details Error. Probably there is a connection problem');
                });

            }, function() {
                alert('Getting Game Details Error');
                $state.go("start");
            });

        $scope.$on("$destroy", function() {
            GameService.stopRefreshing();
        });

    }]);