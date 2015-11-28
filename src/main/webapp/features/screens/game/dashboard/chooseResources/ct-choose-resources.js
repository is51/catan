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

                var MAX_RESOURCES_COUNT_LIMIT = getMaxResourcesCountLimit(type);
                var MAX_RESOURCES_COUNT_FOR_APPLY = getMaxResourcesCountForApply(type);
                var MIN_RESOURCES_COUNT_LIMIT = getMinResourcesCountLimit(type);
                var MIN_RESOURCES_COUNT_FOR_APPLY = getMinResourcesCountForApply(type);

                var REMOVE_OTHER_RESOURCE_WHEN_LIMIT = getRemoveOtherResourceWhenLimit(type);

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
                    if (scope.getTotalCount() < MAX_RESOURCES_COUNT_LIMIT) {
                        scope.resources[resourceType]++;
                    } else if (REMOVE_OTHER_RESOURCE_WHEN_LIMIT) {
                        if (scope.removeOtherResource(resourceType)) {
                            scope.resources[resourceType]++;
                        }
                    }
                };

                scope.removeResource = function(resourceType) {
                    if (scope.getTotalCount() > MIN_RESOURCES_COUNT_LIMIT) {
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
                    return  count < MIN_RESOURCES_COUNT_FOR_APPLY || count > MAX_RESOURCES_COUNT_FOR_APPLY;
                };

                scope.cancel = function() {
                    SelectService.cancelRequestSelection(type);
                };

                scope.ok = function() {
                    SelectService.select(type, convertResourcesToSelection(type, scope.resources));
                };
            }

            function convertResourcesToSelection(type, resources) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        var selection = {};
                        for (var i in resources) {
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
                }
            }

            function getMaxResourcesCountLimit(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 2;
                }
            }

            function getMaxResourcesCountForApply(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 2;
                }
            }

            function getMinResourcesCountLimit(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 0;
                }
            }

            function getMinResourcesCountForApply(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return 2;
                }
            }

            function getRemoveOtherResourceWhenLimit(type) {
                switch (type) {
                    case "CARD_YEAR_OF_PLENTY":
                        return true; //TODO: or 'false' for 'year of plenty'?
                }
            }


        }]);