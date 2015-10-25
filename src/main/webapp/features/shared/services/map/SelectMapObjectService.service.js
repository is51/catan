'use strict';

angular.module('catan')
        .factory('SelectMapObjectService', ['$q', function ($q) {

            var requestSelectionDeferred = {
                node: null,
                edge: null
            };

            var SelectMapObjectService = {};

            SelectMapObjectService.requestSelection = function(type) {
                this.cancelRequestSelection(type);
                requestSelectionDeferred[type] = $q.defer();
                return requestSelectionDeferred[type].promise;
            };

            SelectMapObjectService.select = function(type, mapObjectId) {
                if (requestSelectionDeferred[type]) {
                    requestSelectionDeferred[type].resolve(mapObjectId);
                }
            };

            SelectMapObjectService.cancelRequestSelection = function(type) {
                if (requestSelectionDeferred[type]) {
                    requestSelectionDeferred[type].reject("CANCELED");
                }
            };

            return SelectMapObjectService;
        }]);