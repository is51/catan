'use strict';

angular.module('catan')
        .factory('MapService', [function () {

            var MapService = {};

            MapService.linkEntities = function(map) {
                if (map.isLinked) {
                    return map;
                }

                var i, l;
                for (i = 0, l = map.hexes.length; i < l; i++) {
                    linkEntitiesByType(map, "node", map.hexes[i].nodes);
                    linkEntitiesByType(map, "edge", map.hexes[i].edges);
                }
                for (i = 0, l = map.nodes.length; i < l; i++) {
                    linkEntitiesByType(map, "hex", map.nodes[i].hexes);
                    linkEntitiesByType(map, "edge", map.nodes[i].edges);
                }
                for (i = 0, l = map.edges.length; i < l; i++) {
                    linkEntitiesByType(map, "node", map.edges[i].nodes);
                    linkEntitiesByType(map, "hex", map.edges[i].hexes);
                }
                map.isLinked = true;
                return map;
            };

            return MapService;

            function linkEntitiesByType(map, type, arr) {
                var entity;
                for (var k in arr) {
                    entity = getEntityById(map, type, arr[k]);
                    delete arr[k];
                    arr[k.substr(0, k.length-2)] = entity;
                }
            }

            function getEntityById(map, type, id) {
                var index = (type === "hex") ? type + "es" : type + "s";
                var i,
                    l = map[index].length;
                for (i = 0; i < l; i++) {
                    if (map[index][i][type+"Id"] === id) {
                        return map[index][i];
                    }
                }
            }
        }]);