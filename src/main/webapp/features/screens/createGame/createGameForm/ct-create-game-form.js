'use strict';

angular.module('catan')
    .directive('ctCreateGameForm', ['Remote', '$state', '$stateParams', 'User', function(Remote, $state, $stateParams, User) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/screens/createGame/createGameForm/ct-create-game-form.html",
            link: function(scope) {

                scope.initialBuildingsSetIdValues = [
                    {value: 1, name: "2 settlements + 2 roads"},
                    {value: 2, name: "1 city + 2 settlements + 3 roads"}
                ];

                scope.data = $stateParams.data || {
                    privateGame: true,
                    targetVictoryPoints: 12,
                    initialBuildingsSetId: 1
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
                                onRegister: goBackAndCreateGame,
                                onBack: goBack
                            });
                        } else {
                            $state.go('login', {
                                onLogin: goBackAndCreateGame,
                                onBack: goBack
                            });
                        }

                    }

                    return false;
                };

                function goBack() {
                    $state.go('createGame', {data: scope.data});
                }

                function goBackAndCreateGame() {
                    goBack();
                    createGame();
                }

                function createGame() {
                    Remote.game.create({
                        privateGame: scope.data.privateGame,
                        targetVictoryPoints: scope.data.targetVictoryPoints,
                        initialBuildingsSetId: scope.data.initialBuildingsSetId
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