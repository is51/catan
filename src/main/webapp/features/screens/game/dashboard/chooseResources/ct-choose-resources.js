'use strict';

angular.module('catan')
        .directive('ctChooseResources', ['SelectService', 'ChooseResourcesWindowService', function(SelectService, ChooseResourcesWindowService) {

            return {
                restrict: 'E',
                scope: {},
                templateUrl: "/features/screens/game/dashboard/chooseResources/ct-choose-resources.html",
                link: function(scope) {

                    scope.$watch(function() {
                        return ChooseResourcesWindowService.getType();
                    }, function(type) {
                        if (type) {
                            init(scope, type);
                        }
                    });

                }
            };

            function init(scope, type) {

                var maxResourcesCountLimit = getMaxResourcesCountLimit(type);
                var maxResourcesCountForApply = getMaxResourcesCountForApply(type);
                var minResourcesCountLimit = getMinResourcesCountLimit(type);
                var minResourcesCountForApply = getMinResourcesCountForApply(type);

                var removeOtherResourceWhenLimit = getRemoveOtherResourceWhenLimit(type);

                scope.resources = {
                    BRICK: 0,
                    WOOD: 0,
                    SHEEP: 0,
                    WHEAT: 0,
                    STONE: 0
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
                    if (scope.getTotalCount() > minResourcesCountLimit) {
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
                                selection.firstResource = i;
                                resources[i]--;
                                break;
                            }
                        }
                        for (i in resources) {
                            if (resources[i] > 0) {
                                selection.secondResource = i;
                            }
                        }
                        return selection;
                    case "CARD_MONOPOLY":
                        for (i in resources) {
                            if (resources[i] > 0) {
                                return {resource: i};
                            }
                        }
                }
            }

            function getMaxResourcesCountLimit(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 2;
                    case "CARD_MONOPOLY":
                        return 1;
                }
            }

            function getMaxResourcesCountForApply(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 2;
                    case "CARD_MONOPOLY":
                        return 1;
                }
            }

            function getMinResourcesCountLimit(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 0;
                    case "CARD_MONOPOLY":
                        return 0;
                }
            }

            function getMinResourcesCountForApply(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 2;
                    case "CARD_MONOPOLY":
                        return 1;
                }
            }

            function getRemoveOtherResourceWhenLimit(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return true; //TODO: or 'false' for 'year of plenty'?
                    case "CARD_MONOPOLY":
                        return true;
                }
            }


        }]);