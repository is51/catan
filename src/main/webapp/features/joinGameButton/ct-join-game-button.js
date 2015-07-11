'use strict';

angular.module('catan')
    .directive('ctJoinGameButton', ['Remote', function(Remote) {
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
                        }, function(response) {
                            alert('Error: ' + ((response.data && response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });

                    return false;
                });

            }
        };
    }]);