'use strict';

angular.module('catan')
        .directive('ctTradeReplyPanel', ['ModalWindowService', 'PlayService', 'GameService', function(ModalWindowService, PlayService, GameService) {

            var MODAL_WINDOW_ID = "TRADE_REPLY_PANEL";

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/tradeReplyPanel/ct-trade-reply-panel.html",
                link: function(scope) {

                    scope.modalWindowId = MODAL_WINDOW_ID;

                    scope.propositionGive = {};
                    scope.propositionGet = {};

                    scope.$watch(function() {
                        return ModalWindowService.isVisible(MODAL_WINDOW_ID);
                    }, function(isVisible) {
                        if (isVisible) {
                            init(scope);
                        }
                    });

                    scope.accept = function() {
                        PlayService.tradeAccept(scope.game).then(function() {
                            ModalWindowService.hide(MODAL_WINDOW_ID);
                            GameService.refresh(scope.game);
                        }, function(response) {
                            alert('Trade Propose Accept error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                    };

                    scope.decline = function() {
                        PlayService.tradeDecline(scope.game).then(function() {
                            ModalWindowService.hide(MODAL_WINDOW_ID);
                            GameService.refresh(scope.game);
                        }, function(response) {
                            alert('Trade Propose Decline error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                    };

                    scope.acceptDisabled = function() {
                        var currentUserResources = scope.game.getCurrentUser().resources;
                        for (var i in scope.propositionGive) {
                            if (currentUserResources[i] < scope.propositionGive[i]) {
                                return true;
                            }
                        }
                        return false;
                    };

                }
            };

            function init(scope) {
                var proposition = scope.game.getCurrentUserAction("TRADE_REPLY");

                scope.propositionGive = {};
                scope.propositionGet = {};

                for (var i in proposition) {
                    if (proposition[i] > 0) {
                        scope.propositionGive[i] = proposition[i];
                        scope.propositionGet[i] = 0;
                    } else {
                        scope.propositionGive[i] = 0;
                        scope.propositionGet[i] = -proposition[i];
                    }
                }

                scope.proposerName = scope.game.getCurrentMoveUser().user.username;
            }
        }]);