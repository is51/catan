'use strict';

angular.module('catan')
    .directive('ctCurrentGamesList', ['Remote', function(Remote) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/currentGamesList/ct-current-games-list.html",
            link: function(scope) {

                scope.items = null;

                scope.update = function() {
                    Remote.game.listCurrent()
                        .then(function(response) {
                            scope.items = response.data;
                        }, function(response) {
                            alert('Getting Current Games List Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                };

                scope.update();

            }
        };
    }]);