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

            MapMarkingService.markHexes = function(hexIds, gameUser) {
                //TODO: write code here for hex choosing (following user story)
            };

            MapMarkingService.clearMarkingHexes = function() {
                //TODO: write code here for hex choosing (following user story)
            };

            MapMarkingService.markNodes = function(nodeIds, gameUser) {
                var service = this;
                $timeout(function() {
                    service.marked.nodeIds = nodeIds;
                    service.marked.playerColor = gameUser.colorId;
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
                    service.marked.playerColor = gameUser.colorId;
                });
            };

            MapMarkingService.clearMarkingEdges = function() {
                this.marked.edgeIds = [];
                this.marked.playerColor = null;
            };

            return MapMarkingService;

        }]);