'use strict';

angular.module('catan')
    .controller('GameController', [
            '$scope', '$stateParams', '$state', 'Game',
            function($scope, $stateParams, $state, Game)
    {

        var GAME_UPDATE_DELAY = 5000;

        $scope.game = Game(+$stateParams.gameId);

        $scope.game.load()
            .then(null, function() {
                alert('Getting Game Details Error');
                $state.go("start");
            });

        $scope.game.startUpdating(GAME_UPDATE_DELAY, null, function() {
            alert('Getting Game Details Error. Probably there is a connection problem');
        });

        $scope.$on("$destroy", function() {
            $scope.game.stopUpdating();
        });

    }]);