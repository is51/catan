'use strict';

angular.module('catan')
    .directive('ctJoinPublicGameButton', ['Remote', '$state', function(Remote, $state) {
        return {
            restrict: 'A',
            scope: {
                game: '='
            },
            link: function(scope, element) {

                element.on('click', function() {
                    Remote.game.joinPublic({gameId: scope.game.gameId})
                        .then(function() {
                            alert('Successful joining');
                            $state.go('game', {gameId: scope.game.gameId});
                        }, function(response) {
                            alert('Error: ' + ((response.data && response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });

                    return false;
                });

            }
        };
    }]);