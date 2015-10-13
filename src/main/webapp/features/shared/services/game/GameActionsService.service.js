'use strict';

angular.module('catan')
        .factory('GameActionsService', [function () {

            // If these calculations takes too much time, it should be refactored - do calculations once when game.details is received

            // TODO: maybe(!) this config should be moved to app.js
            var actionGroups = {
                "BUILD": ["BUILD_SETTLEMENT", "BUILD_CITY", "BUILD_ROAD"]
            };

            function getRelatedActions(actionGroupCode) {
                var relatedActions = [];
                var current;

                if (actionGroups[actionGroupCode]) {
                    for (var i in actionGroups[actionGroupCode]) {
                        current = actionGroups[actionGroupCode][i];
                        relatedActions.push(current);
                        relatedActions = relatedActions.concat(getRelatedActions(current));
                    }
                }

                return relatedActions;
            }

            var GameActionsService = {};

            GameActionsService.isActionEnableForUser = function(gameUser, actionCode) {
                return (
                        gameUser.actions &&
                        gameUser.actions.list &&
                        gameUser.actions.list.some(function(item) {
                            return item.code === actionCode;
                        })
                );
            };

            GameActionsService.isActionGroupEnableForUser = function(gameUser, actionGroupCode) {
                return (
                        gameUser.actions &&
                        gameUser.actions.list &&
                        gameUser.actions.list.some(function(item) {
                            return getRelatedActions(actionGroupCode).indexOf(item.code) !== -1;
                        })
                );
            };

            return GameActionsService;
        }]);