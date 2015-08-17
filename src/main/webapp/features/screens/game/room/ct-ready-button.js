'use strict';

angular.module('catan')
        .directive('ctReadyButton', ['$state', 'Remote', 'User', function($state, Remote, User) {
            return {
                restrict: 'A',
                scope: {
                    game: '=',
                    afterSet: '='
                },
                link: function(scope, element) {

                    scope.$watch(function() {
                        return isCurrentUserReady(scope.game);
                    }, function(ready) {
                        if (ready) {
                            element.addClass("btn-success");
                        } else {
                            element.removeClass("btn-success");
                        }
                    });

                    element.on('click', function() {

                        var apiCallName = (isCurrentUserReady(scope.game)) ? "notReady" : "ready";

                        Remote.game[apiCallName]({gameId: scope.game.gameId})
                                .then(function() {
                                    if (scope.afterSet) {
                                        scope.afterSet();
                                    }
                                }, function(response) {
                                    alert('Error: ' + ((response.data && response.data.errorCode) ? response.data.errorCode : 'unknown'));
                                });

                        return false;
                    });

                }
            };

            //TODO: Game service should be created for that
            function isCurrentUserReady(game) {
                for (var i in game.gameUsers) {
                    if (game.gameUsers[i].user.id === User.get().id) {
                        break;
                    }
                }
                return game.gameUsers[i].ready;
            }
        }]);