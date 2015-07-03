'use strict';

angular.module('catan')
    .directive('ctPublicGamesList', ['Remote', function(Remote) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/publicGamesList/ct-public-games-list.html",
            link: function(scope) {

                scope.items = null;

                scope.update = function() {
                    Remote.game.listPublic()
                        .then(function(response) {
                            scope.items = response.data;
                        }, function(response) {
                            alert('Getting Public Games List Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                };

                scope.update();

            }
        };
    }]);