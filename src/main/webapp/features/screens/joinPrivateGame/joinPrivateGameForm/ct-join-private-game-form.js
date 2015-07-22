'use strict';

angular.module('catan')
    .directive('ctJoinPrivateGameForm', ['Remote', '$state', function(Remote, $state) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/screens/joinPrivateGame/joinPrivateGameForm/ct-join-private-game-form.html",
            link: function(scope) {

                scope.data = {};

                scope.submit = function() {
                    Remote.game.joinPrivate({'privateCode': scope.data.privateCode})
                        .then(function(response) {
                            alert('Successful joining... but API should return GAME ID for redirecting to game screen');
                            //$state.go('game', {gameId: response.data.gameId});
                        }, function(response) {
                            alert('Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                };

            }
        };
    }]);