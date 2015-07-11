'use strict';

angular.module('catan')
    .directive('ctJoinPrivateGameForm', ['Remote', function(Remote) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/joinPrivateGameForm/ct-join-private-game-form.html",
            link: function(scope) {

                scope.data = {};

                scope.submit = function() {
                    Remote.game.joinPrivate({'privateCode': scope.data.privateCode})
                        .then(function(response) {
                            alert('Successful joining');
                        }, function(response) {
                            alert('Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                };

            }
        };
    }]);