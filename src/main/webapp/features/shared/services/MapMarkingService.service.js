'use strict';

angular.module('catan')
        .factory('MapMarkingService', [function () {

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
                this.marked.nodeIds = nodeIds;
                this.marked.playerColor = gameUser.colorId;
            };

            MapMarkingService.clearMarkingNodes = function() {
                this.marked.nodeIds = [];
                this.marked.playerColor = null;
            };

            MapMarkingService.markEdges = function(edgeIds, gameUser) {
                this.marked.edgeIds = edgeIds;
                this.marked.playerColor = gameUser.colorId;
            };

            MapMarkingService.clearMarkingEdges = function() {
                this.marked.edgeIds = [];
                this.marked.playerColor = null;
            };

            return MapMarkingService;

        }]);