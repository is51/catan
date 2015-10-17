'use strict';

angular.module('catan')
        .factory('LinkMapEntitiesHelper', [function () {

            var LinkMapEntitiesHelper = {};

            LinkMapEntitiesHelper.linkNeighbourEntitiesByIds = function(map) {
                if (!map) {
                    return null;
                }

                var objectsById = {
                    hexes: populateObjectsWithIds(map.hexes, "hexId"),
                    nodes: populateObjectsWithIds(map.nodes, "nodeId"),
                    edges: populateObjectsWithIds(map.edges, "edgeId")
                };

                map.hexes.forEach(function(hex) {
                    hex.nodes = getObjectsByIds(hex.nodesIds, objectsById.nodes);
                    hex.edges = getObjectsByIds(hex.edgesIds, objectsById.edges);
                });

                map.nodes.forEach(function(node) {
                    node.hexes = getObjectsByIds(node.hexesIds, objectsById.hexes);
                    node.edges = getObjectsByIds(node.edgesIds, objectsById.edges);
                });

                map.edges.forEach(function(edge) {
                    edge.nodes = getObjectsByIds(edge.nodesIds, objectsById.nodes);
                    edge.hexes = getObjectsByIds(edge.hexesIds, objectsById.hexes);
                });

                return map;
            };

            return LinkMapEntitiesHelper;

            function getObjectsByIds(setOfIds, objectsById) {
                var objects = {};

                angular.forEach(setOfIds, function(id, position) {
                    objects[position.substr(0, position.length-2)] = objectsById[id];
                });

                return objects;
            }

            function populateObjectsWithIds(objects, idAttr) {
                var objectsById = {};

                objects.forEach(function(object) {
                    objectsById[object[idAttr]] = object;
                });

                return objectsById;
            }
        }]);