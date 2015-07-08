'use strict';

angular.module('catan')
    .directive('ctCreateGameForm', ['Remote', function(Remote) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/createGameForm/ct-create-game-form.html",
            link: function(scope) {

                scope.data = {
                    privateGame: false
                };

                scope.submit = function() {
                    Remote.game.create({'privateGame': scope.data.privateGame})
                        .then(function(response) {
                            alert('Game has been created! Game ID = ' + response.data.gameId);
                        }, function(response) {
                            alert('Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                };

            }
        };
    }]);