'use strict';

angular.module('catan')
        .directive('ctTradeReplyPanel', ['ModalWindowService', 'PlayService', 'GameService', function(ModalWindowService, PlayService, GameService) {

            return {
                restrict: 'E',
                scope: {
                    game: "="
                },
                templateUrl: "/features/screens/game/dashboard/tradeReplyPanel/ct-trade-reply-panel.html",
                link: function(scope) {

                    scope.PANEL_ID = "TRADE_REPLY_PANEL";

                    scope.propositionGive = {};
                    scope.propositionGet = {};

                    scope.$watch(function() {
                        return ModalWindowService.isVisible(scope.PANEL_ID);
                    }, function(isVisible) {
                        if (isVisible) {
                            init(scope);
                        }
                    });

                    scope.accept = function() {
                        PlayService.tradeAccept(scope.game).then(function() {
                            ModalWindowService.hide(scope.PANEL_ID);
                            GameService.refresh(scope.game);
                        }, function(response) {
                            alert('Trade Propose Accept error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                    };

                    scope.decline = function() {
                        PlayService.tradeDecline(scope.game).then(function() {
                            ModalWindowService.hide(scope.PANEL_ID);
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
                //TODO: create method in GameModel for this
                var tradeReplyAction = scope.game.getCurrentUser().availableActions.list.filter(function(item) {
                    return item.code === "TRADE_REPLY";
                })[0];

                var proposition = tradeReplyAction.params;
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