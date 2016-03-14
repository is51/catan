'use strict';

angular.module('catan')
        .factory('SelectService', ['$q', function ($q) {

            var requestSelectionDeferred = {};

            var SelectService = {};

            SelectService.requestSelection = function(type) {
                this.cancelRequestSelection(type);
                requestSelectionDeferred[type] = $q.defer();
                return requestSelectionDeferred[type].promise;
            };

            SelectService.select = function(type, mapObjectId) {
                if (requestSelectionDeferred[type]) {
                    requestSelectionDeferred[type].resolve(mapObjectId);
                }
            };

            SelectService.cancelRequestSelection = function(type) {
                if (requestSelectionDeferred[type]) {
                    requestSelectionDeferred[type].reject("CANCELED");
                    requestSelectionDeferred[type] = null;
                }
            };

            SelectService.cancelAllRequestSelections = function() {
                for (var type in requestSelectionDeferred) {
                    this.cancelRequestSelection(type);
                }
            };

            return SelectService;
        }]);