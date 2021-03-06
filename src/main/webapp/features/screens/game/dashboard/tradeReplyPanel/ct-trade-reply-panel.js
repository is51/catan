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

                    scope.$watch(function() {
                        var actionParams = scope.game.getCurrentUserAction("TRADE_REPLY");
                        return (actionParams) ? actionParams.offerId : null;
                    }, function(offerId) {
                        scope.offerIsActive = scope.offerId === offerId;
                    });

                    scope.accept = function() {
                        PlayService.tradeAccept(scope.game, scope.offerId).then(function() {
                            ModalWindowService.hide(MODAL_WINDOW_ID);
                            GameService.refresh(scope.game);
                        }, function(response) {
                            alert('Trade Propose Accept error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                            if (response.data.errorCode === "OFFER_ALREADY_ACCEPTED") {
                                ModalWindowService.hide(MODAL_WINDOW_ID);
                                GameService.refresh(scope.game);
                            }
                        });
                    };

                    scope.decline = function() {
                        PlayService.tradeDecline(scope.game, scope.offerId).then(function() {
                            ModalWindowService.hide(MODAL_WINDOW_ID);
                            GameService.refresh(scope.game);
                        }, function(response) {
                            if (response.data.errorCode === "OFFER_ALREADY_ACCEPTED") {
                                ModalWindowService.hide(MODAL_WINDOW_ID);
                                GameService.refresh(scope.game);
                            } else {
                                alert('Trade Propose Decline error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                            }
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
                var actionParams = scope.game.getCurrentUserAction("TRADE_REPLY");

                var proposition = actionParams.resources;

                scope.offerId = actionParams.offerId;
                scope.offerIsActive = true;

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