'use strict';

angular.module('catan')
        .directive('ctChooseResources', ['SelectService', function(SelectService) {

            //TODO: this directive should be refactored

            return {
                restrict: 'E',
                scope: {
                    game: "=",
                    type: "@"
                },
                templateUrl: "/features/screens/game/dashboard/chooseResources/ct-choose-resources.html",
                link: function(scope) {

                    init(scope, scope.type);

                }
            };

            function init(scope, type) {

                var playerResources = scope.game.getCurrentUser().resources;
                var tradePortRatio = (type === "TRADE_PORT") ? scope.game.getCurrentUserAction("TRADE_PORT") : {};

                var maxResourcesCountLimit = getMaxResourcesCountLimit(type, playerResources);
                var maxResourcesCountForApply = getMaxResourcesCountForApply(type, playerResources);
                var minResourcesCountLimit = getMinResourcesCountLimit(type, playerResources);
                var minResourcesCountForApply = getMinResourcesCountForApply(type, playerResources);

                var removeOtherResourceWhenLimit = getRemoveOtherResourceWhenLimit(type);

                var tradeBalance = 0;

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
                    var step = 1;
                    if (type === "TRADE_PORT" && scope.resources[resourceType] < 0) {
                        step = tradePortRatio[resourceType];
                    }

                    if (scope.getTotalCount() + step <= maxResourcesCountLimit || maxResourcesCountLimit === null) {
                        scope.resources[resourceType] += step;
                    } else if (removeOtherResourceWhenLimit && step === 1) {
                        if (scope.removeOtherResource(resourceType)) {
                            scope.resources[resourceType]++;
                        }
                    }

                    if (type === "TRADE_PORT") {
                        tradeBalance = recalculateTradeBalance(scope.resources, tradePortRatio);
                    }
                };

                scope.removeResource = function(resourceType) {
                    var step = 1;
                    if (type === "TRADE_PORT" && scope.resources[resourceType] <= 0) {
                        step = tradePortRatio[resourceType];
                    }

                    if (scope.getTotalCount() - step >= minResourcesCountLimit && playerResources[resourceType] - step + scope.resources[resourceType] >= 0) {
                        scope.resources[resourceType] -= step;
                    }

                    if (type === "TRADE_PORT") {
                        tradeBalance = recalculateTradeBalance(scope.resources, tradePortRatio);
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

                    if (type === "TRADE_PROPOSE" && noNegativeOrPositiveResources(scope.resources)) {
                        return true;
                    }

                    return areAllResourcesZero(scope.resources)
                            || count < minResourcesCountForApply
                            || (count > maxResourcesCountForApply && maxResourcesCountForApply !== null)
                            || tradeBalance !== 0;
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
                    case "TRADE_PORT":
                    case "TRADE_PROPOSE":
                        return {
                            brick: resources.brick,
                            wood: resources.wood,
                            sheep: resources.sheep,
                            wheat: resources.wheat,
                            stone: resources.stone
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
                    case "TRADE_PORT":
                        return null; //unlimited
                    case "TRADE_PROPOSE":
                        return null; //unlimited
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
                    case "TRADE_PORT":
                        return null; //unlimited
                    case "TRADE_PROPOSE":
                        return null; //unlimited
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
                    case "TRADE_PORT":
                        return -calculateResourcesSum(playerResources);
                    case "TRADE_PROPOSE":
                        return -calculateResourcesSum(playerResources);
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
                    case "TRADE_PORT":
                        return -calculateResourcesSum(playerResources);
                    case "TRADE_PROPOSE":
                        return -calculateResourcesSum(playerResources);
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
                    case "TRADE_PORT":
                        return false;
                    case "TRADE_PROPOSE":
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
                    case "TRADE_PORT":
                        return "BOTH";
                    case "TRADE_PROPOSE":
                        return "BOTH";
                }
            }

            function calculateResourcesSum(resources) {
                var sum = 0;
                for (var i in resources) {
                    sum += resources[i];
                }
                return sum;
            }

            function calculateCountForKickOff(resources) {
                return Math.floor(calculateResourcesSum(resources) / 2);
            }

            function recalculateTradeBalance(resources, tradePortRatio) {
                var tradeBalance = 0;
                for (var i in resources) {
                    var count = resources[i];
                    var ratio = tradePortRatio[i];
                    if (count > 0) {
                        tradeBalance += count;
                    }
                    if (count < 0) {
                        tradeBalance += count / ratio;
                    }
                }
                return tradeBalance;
            }

            function areAllResourcesZero(resources) {
                for (var i in resources) {
                    if (resources[i] !== 0) {
                        return false;
                    }
                }
                return true;
            }

            function noNegativeOrPositiveResources(resources) {

                var isNegative = false;
                var isPositive = false;

                for (var i in resources) {
                    if (resources[i] > 0) {
                        isPositive = true;
                    }
                    if (resources[i] < 0) {
                        isNegative = true;
                    }
                }
                return !isPositive || !isNegative;
            }

        }]);