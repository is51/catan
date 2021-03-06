'use strict';

angular.module('catan')
    .directive('ctJoinPublicGameButton', ['Remote', '$state', 'User', function(Remote, $state, User) {
        return {
            restrict: 'A',
            scope: {
                game: '='
            },
            link: function(scope, element) {

                element.on('click', function() {

                    if (User.isAuthorized()) {
                        joinPublicGame();
                    }

                    if (User.isNotAuthorized()) {
                        $state.go('registerGuest', {
                            onRegister: function() {
                                goBack();
                                joinPublicGame();
                            },
                            onBack: goBack
                        });
                    }

                    return false;
                });

                function goBack() {
                    $state.go('joinPublicGame');
                }

                function joinPublicGame() {
                    Remote.game.joinPublic({gameId: scope.game.getId()})
                        .then(function() {
                            $state.go('game', {gameId: scope.game.getId()});
                        }, function(response) {
                            alert('Error: ' + ((response.data && response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                }

            }
        };

    }]);