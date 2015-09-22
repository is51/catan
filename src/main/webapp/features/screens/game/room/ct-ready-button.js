'use strict';

angular.module('catan')
        .directive('ctReadyButton', ['$state', 'Remote', 'User', 'Game', function($state, Remote, User, Game) {
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
                                    Game.refresh(scope.game);

                                }, function(response) {
                                    alert('Error: ' + ((response.data && response.data.errorCode) ? response.data.errorCode : 'unknown'));
                                });

                        return false;
                    });

                }
            };

        }]);