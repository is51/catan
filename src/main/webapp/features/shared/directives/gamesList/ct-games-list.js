'use strict';

angular.module('catan')
    .directive('ctGamesList', ['Remote', function(Remote) {
        return {
            restrict: 'E',
            scope: {
                'typeOfGames': '@'
            },
            templateUrl: "/features/shared/directives/gamesList/ct-games-list.html",
            link: function(scope) {
                var remoteMethodForGettingGames = (scope.typeOfGames === 'CURRENT') ? 'listCurrent' : 'listPublic';

                scope.items = null;

                scope.update = function() {
                    Remote.game[remoteMethodForGettingGames]()
                        .then(function(response) {
                            scope.items = response.data;
                        }, function(response) {
                            alert('Getting Games List Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                };

                scope.update();

            }
        };
    }]);