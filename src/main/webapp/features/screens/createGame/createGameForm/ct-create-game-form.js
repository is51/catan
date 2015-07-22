'use strict';

angular.module('catan')
    .directive('ctCreateGameForm', ['Remote', '$state', function(Remote, $state) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/screens/createGame/createGameForm/ct-create-game-form.html",
            link: function(scope) {

                scope.data = {
                    privateGame: false,
                    targetVictoryPoints: 12
                };

                scope.submit = function() {
                    Remote.game.create({'privateGame': scope.data.privateGame, 'targetVictoryPoints': scope.data.targetVictoryPoints})
                        .then(function(response) {
                            var gameId = response.data.gameId;

                            alert('Game has been created! Game ID = ' + gameId);
                            $state.go('game', {gameId: gameId});
                        }, function(response) {
                            alert('Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                };

            }
        };
    }]);