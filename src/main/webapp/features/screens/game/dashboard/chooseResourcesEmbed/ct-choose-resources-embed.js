'use strict';

angular.module('catan')
        .directive('ctChooseResourcesEmbed', ['SelectService', function(SelectService) {

            return {
                restrict: 'E',
                scope: {
                    game: "=",
                    type: "@"
                },
                templateUrl: "/features/screens/game/dashboard/chooseResourcesEmbed/ct-choose-resources-embed.html",
                link: function(scope) {

                    init(scope, scope.type);

                }
            };

            function init(scope, type) {

                var playerResources = scope.game.getCurrentUser().resources;

                var maxResourcesCountLimit = getMaxResourcesCountLimit(type, playerResources);
                var maxResourcesCountForApply = getMaxResourcesCountForApply(type, playerResources);
                var minResourcesCountLimit = getMinResourcesCountLimit(type, playerResources);
                var minResourcesCountForApply = getMinResourcesCountForApply(type, playerResources);

                var removeOtherResourceWhenLimit = getRemoveOtherResourceWhenLimit(type);

                scope.balanceType = getBalanceType(type);

                scope.resources = {
                    brick: 0,
                    wood: 0,
                    sheep: 0,
                    wheat: 0,
                    stone: 0
                };

                scope.getTotalCount = function() {
                    var sum = 0;
                    for (var i in scope.resources) {
                        sum += scope.resources[i];
                    }
                    return sum;
                };

                scope.addResource = function(resourceType) {
                    if (scope.getTotalCount() < maxResourcesCountLimit) {
                        scope.resources[resourceType]++;
                    } else if (removeOtherResourceWhenLimit) {
                        if (scope.removeOtherResource(resourceType)) {
                            scope.resources[resourceType]++;
                        }
                    }
                };

                scope.removeResource = function(resourceType) {
                    if (scope.getTotalCount() > minResourcesCountLimit && playerResources[resourceType] + scope.resources[resourceType] > 0) {
                        scope.resources[resourceType]--;
                    }
                };

                scope.removeOtherResource = function (resourceType) {
                    for (var i in scope.resources) {
                        if (i !== resourceType && scope.resources[i] > 0) {
                            scope.resources[i]--;
                            return true;
                        }
                    }
                    return false;
                };

                scope.okDisabled = function() {
                    var count = scope.getTotalCount();
                    return  count < minResourcesCountForApply || count > maxResourcesCountForApply;
                };

                scope.cancel = function() {
                    SelectService.cancelRequestSelection(type);
                };

                scope.ok = function() {
                    SelectService.select(type, convertResourcesToSelection(type, scope.resources));
                };
            }

            function convertResourcesToSelection(type, resources) {
                var i;

                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        var selection = {};
                        for (i in resources) {
                            if (resources[i] > 0) {
                                selection.firstResource = i.toUpperCase();
                                resources[i]--;
                                break;
                            }
                        }
                        for (i in resources) {
                            if (resources[i] > 0) {
                                selection.secondResource = i.toUpperCase();
                            }
                        }
                        return selection;
                    case "CARD_MONOPOLY":
                        for (i in resources) {
                            if (resources[i] > 0) {
                                return {resource: i.toUpperCase()};
                            }
                        }
                        return;
                    case "KICK_OFF_RESOURCES":
                        return {
                            brick: -resources.brick,
                            wood: -resources.wood,
                            sheep: -resources.sheep,
                            wheat: -resources.wheat,
                            stone: -resources.stone
                        };
                }
            }

            function getMaxResourcesCountLimit(type, playerResources) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 2;
                    case "CARD_MONOPOLY":
                        return 1;
                    case "KICK_OFF_RESOURCES":
                        return 0;
                }
            }

            function getMaxResourcesCountForApply(type, playerResources) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 2;
                    case "CARD_MONOPOLY":
                        return 1;
                    case "KICK_OFF_RESOURCES":
                        return -calculateCountForKickOff(playerResources);
                }
            }

            function getMinResourcesCountLimit(type, playerResources) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 0;
                    case "CARD_MONOPOLY":
                        return 0;
                    case "KICK_OFF_RESOURCES":
                        return -calculateCountForKickOff(playerResources);
                }
            }

            function getMinResourcesCountForApply(type, playerResources) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 2;
                    case "CARD_MONOPOLY":
                        return 1;
                    case "KICK_OFF_RESOURCES":
                        return -calculateCountForKickOff(playerResources);
                }
            }

            function getRemoveOtherResourceWhenLimit(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return true; //TODO: or 'false' for 'year of plenty'?
                    case "CARD_MONOPOLY":
                        return true;
                    case "KICK_OFF_RESOURCES":
                        return false;
                }
            }

            function getBalanceType(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return "POSITIVE";
                    case "CARD_MONOPOLY":
                        return "POSITIVE";
                    case "KICK_OFF_RESOURCES":
                        return "NEGATIVE";
                }
            }

            function calculateCountForKickOff(resources) {
                var sum = 0;
                for (var i in resources) {
                    sum += resources[i];
                }
                return Math.floor(sum / 2);
            }

        }]);