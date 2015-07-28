'use strict';

angular.module('catan')
    .directive('ctCreateGameForm', ['Remote', '$state', '$stateParams', 'User', function(Remote, $state, $stateParams, User) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/screens/createGame/createGameForm/ct-create-game-form.html",
            link: function(scope) {

                scope.data = $stateParams.data || {
                    privateGame: true,
                    targetVictoryPoints: 12
                };

                scope.submit = function() {

                    if (User.isAuthorized()) {

                        if (!scope.data.privateGame && User.isTypeGuest()) {
                            alert("Guest can't create public game. You should register. Registration from guest to regular user is NOT IMPLEMENTED");
                        } else {
                            createGame();
                        }

                    }

                    if (User.isNotAuthorized()) {

                        if (scope.data.privateGame) {
                            $state.go('registerGuest', {
                                onRegister: function() {
                                    $state.go('createGame', {data: scope.data});
                                    createGame();
                                },
                                onBack: function() {
                                    $state.go('createGame', {data: scope.data});
                                }
                            });
                        } else {
                            $state.go('login', {
                                onLogin: function() {
                                    $state.go('createGame', {data: scope.data});
                                    createGame();
                                },
                                onBack: function() {
                                    $state.go('createGame', {data: scope.data});
                                }
                            });
                        }

                    }

                    return false;
                };

                function createGame() {
                    Remote.game.create({
                        privateGame: scope.data.privateGame,
                        targetVictoryPoints: scope.data.targetVictoryPoints
                    })
                        .then(function(response) {
                            $state.go('game', {gameId: response.data.gameId});
                        }, function(response) {
                            alert('Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                }

            }
        };
    }]);