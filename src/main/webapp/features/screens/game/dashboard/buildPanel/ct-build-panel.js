'use strict';

angular.module('catan')
        .directive('ctBuildPanel', ['ModalWindowService', 'PlayService', 'GameService', function(ModalWindowService, PlayService, GameService) {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/buildPanel/ct-build-panel.html",
                link: function(scope) {

                    scope.buildSettlement = function() {
                        ModalWindowService.hide("BUILD_PANEL");
                        PlayService.buildSettlement(scope.game).then(function() {
                            GameService.refresh(scope.game);
                        }, function() {
                            alert("Build settlement error!");
                        });
                    };

                    scope.buildCity = function() {
                        ModalWindowService.hide("BUILD_PANEL");
                        PlayService.buildCity(scope.game).then(function() {
                            GameService.refresh(scope.game);
                        }, function() {
                            alert("Build city error!");
                        });
                    };

                    scope.buildRoad = function() {
                        ModalWindowService.hide("BUILD_PANEL");
                        PlayService.buildRoad(scope.game).then(function() {
                            GameService.refresh(scope.game);
                        }, function() {
                            alert("Build road error!");
                        });
                    };

                }
            };
        }]);