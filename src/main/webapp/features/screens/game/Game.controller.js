'use strict';

angular.module('catan')
    .controller('GameController', [
            '$scope', '$stateParams', '$state', 'Game',
            function($scope, $stateParams, $state, Game)
    {

        var GAME_UPDATE_DELAY = 5000;

        $scope.game = null;

        Game.findById(+$stateParams.gameId)
            .then(function(game) {
                $scope.game = game;

                Game.startRefreshing($scope.game, GAME_UPDATE_DELAY, null, function() {
                    alert('Getting Game Details Error. Probably there is a connection problem');
                });

            }, function() {
                alert('Getting Game Details Error');
                $state.go("start");
            });

        $scope.$on("$destroy", function() {
            Game.stopRefreshing();
        });

    }]);