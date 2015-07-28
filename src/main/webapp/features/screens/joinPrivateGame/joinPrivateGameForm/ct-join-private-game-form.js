'use strict';

angular.module('catan')
    .directive('ctJoinPrivateGameForm', ['Remote', '$state', 'User', '$stateParams', function(Remote, $state, User, $stateParams) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/screens/joinPrivateGame/joinPrivateGameForm/ct-join-private-game-form.html",
            link: function(scope) {

                scope.data = $stateParams.data || {};

                scope.submit = function() {

                    if (User.isAuthorized()) {
                        joinPrivateGame();
                    }

                    if (User.isNotAuthorized()) {
                        $state.go('registerGuest', {
                            onRegister: function() {
                                $state.go('joinPrivateGame', {data: scope.data});
                                joinPrivateGame();
                            },
                            onBack: function() {
                                $state.go('joinPrivateGame', {data: scope.data});
                            }
                        });
                    }

                    return false;
                };

                function joinPrivateGame() {
                    Remote.game.joinPrivate({'privateCode': scope.data.privateCode})
                        .then(function(response) {
                            $state.go('game', {gameId: response.data.gameId});
                        }, function(response) {
                            alert('Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                }

            }
        };
    }]);