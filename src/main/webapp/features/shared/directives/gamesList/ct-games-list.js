'use strict';

angular.module('catan')
    .directive('ctGamesList', ['Remote', 'Game', function(Remote, Game) {
        return {
            restrict: 'E',
            scope: {
                'typeOfGames': '@' // can be "PUBLIC" or "CURRENT"
            },
            templateUrl: "/features/shared/directives/gamesList/ct-games-list.html",
            link: function(scope) {
                scope.items = null;

                scope.update = function() {
                    Game.findAllByType(scope.typeOfGames)
                            .then(function(items) {
                                scope.items = items;
                            }, function(response) {
                                alert('Getting Games List Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                            });
                };

                scope.update();

            }
        };
    }]);