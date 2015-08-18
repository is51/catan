'use strict';

angular.module('catan')
        .directive('ctReadyButton', ['$state', 'Remote', 'User', function($state, Remote, User) {
            return {
                restrict: 'A',
                scope: {
                    game: '='
                },
                link: function(scope, element) {

                    scope.$watch("game.isCurrentUserReady()", function(isReady) {
                        if (isReady) {
                            element.addClass("btn-success");
                        } else {
                            element.removeClass("btn-success");
                        }
                    });

                    element.on('click', function() {

                        var apiCallName = (scope.game.isCurrentUserReady()) ? "notReady" : "ready";

                        Remote.game[apiCallName]({gameId: scope.game.getId()})
                                .then(function() {
                                    scope.game.load();

                                }, function(response) {
                                    alert('Error: ' + ((response.data && response.data.errorCode) ? response.data.errorCode : 'unknown'));
                                });

                        return false;
                    });

                }
            };

        }]);