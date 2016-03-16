'use strict';

angular.module('catan')
        .factory('MapMarkingService', ['$timeout', function ($timeout) {

            var MapMarkingService = {};

            MapMarkingService.marked = {
                edgeIds: [],
                nodeIds: [],
                hexIds: [],
                playerColor: null
            };

            MapMarkingService.markHexes = function(hexIds) {
                var service = this;
                $timeout(function() {
                    service.marked.hexIds = hexIds;
                });
            };

            MapMarkingService.clearMarkingHexes = function() {
                this.marked.hexIds = [];
            };

            MapMarkingService.markNodes = function(nodeIds, gameUser) {
                var service = this;
                $timeout(function() {
                    service.marked.nodeIds = nodeIds;
                    if (gameUser) {
                        service.marked.playerColor = gameUser.colorId;
                    }
                });
            };

            MapMarkingService.clearMarkingNodes = function() {
                this.marked.nodeIds = [];
                this.marked.playerColor = null;
            };

            MapMarkingService.markEdges = function(edgeIds, gameUser) {
                var service = this;
                $timeout(function() {
                    service.marked.edgeIds = edgeIds;
                    if (gameUser) {
                        service.marked.playerColor = gameUser.colorId;
                    }
                });
            };

            MapMarkingService.clearMarkingEdges = function() {
                this.marked.edgeIds = [];
                this.marked.playerColor = null;
            };

            return MapMarkingService;

        }]);