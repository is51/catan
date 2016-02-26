'use strict';

angular.module('catan')

        .directive('ctGameMap', ['DrawMapService', 'SelectService', '$timeout', 'MapMarkingService', function(DrawMapService, SelectService, $timeout, MapMarkingService) {

            var HEXES_HIGHLIGHT_DELAY = 3000;
            var HEXES_HIGHLIGHT_CLASS = "highlighted";

            var CANVAS_PRESERVE_ASPECT_RATIO = "xMidYMid meet";

            return {
                restrict: 'E',
                scope: {
                    game: '='
                },
                link: function(scope, element) {

                    var svg = Snap._.make("svg", element[0]);
                    var canvas = Snap(svg)
                            .attr('preserveAspectRatio', CANVAS_PRESERVE_ASPECT_RATIO);


                    // TODO: $watchCollection is slowly. Probably some updateDate should be created and watched

                    // TODO: Separate drawing of static and dynamic objects. And update only dynamic objects

                    scope.$watchCollection("game", function(game) {
                        DrawMapService.drawMap(canvas, game, game.map);
                        updateMapMarking(element);
                    });

                    //TODO: $watchCollection is slowly. Potentially there is a low performance place
                    scope.$watchCollection(function() {
                        return MapMarkingService.marked;
                    }, function() {
                        updateMapMarking(element);
                    });

                    scope.$watch("game.dice", function(newDice, oldDice) {
                        if (oldDice !== undefined && oldDice.thrown === false && newDice.thrown === true) {
                            highlightHexes(element, scope.game.map, newDice.value);
                        }
                    });

                    element.on('click', DrawMapService.NODE_SELECTOR, function(event) {
                        var nodeElement = angular.element(event.target).closest(DrawMapService.NODE_SELECTOR);
                        if (nodeElement.attr('marked')) {
                            var nodeId = +nodeElement.attr('node-id');
                            SelectService.select('node', nodeId);
                        }
                    });

                    element.on('click', DrawMapService.EDGE_SELECTOR, function(event) {
                        var edgeElement = angular.element(event.target).closest(DrawMapService.EDGE_SELECTOR);
                        if (edgeElement.attr('marked')) {
                            var edgeId = +edgeElement.attr('edge-id');
                            SelectService.select('edge', edgeId);
                        }
                    });

                    element.on('click', DrawMapService.HEX_SELECTOR, function(event) {
                        var hexElement = angular.element(event.target).closest(DrawMapService.HEX_SELECTOR);
                        //TODO: uncomment once US93 is finished
                        //if (hexElement.attr('marked')) {
                            var hexId = +hexElement.attr('hex-id');
                            SelectService.select('hex', hexId);
                        //}
                    });

                }
            };

            //TODO: move that code to some helper?
            function highlightHexes(parentElement, map, dice) {
                var hexesToHighlight = map.hexes.filter(function(hex) {
                    return (dice === 7) ? hex.robbed : !hex.robbed && hex.dice === dice;
                });

                var elementsToHighlight = angular.element([]);
                hexesToHighlight.forEach(function(hex) {
                    elementsToHighlight = elementsToHighlight.add(DrawMapService.HEX_SELECTOR + "[hex-id="+hex.hexId+"]", parentElement);
                });

                elementsToHighlight.attr("class", "hex " + HEXES_HIGHLIGHT_CLASS);

                $timeout(function() {
                    elementsToHighlight.attr("class", "hex");
                }, HEXES_HIGHLIGHT_DELAY);
            }

            //TODO: move that code to some helper?
            function updateMapMarking(parentElement) {
                var i, l;
                var markedElements = MapMarkingService.marked;

                // Edges
                parentElement.find(".edge[marked]")
                        .removeAttr("marked")
                        .removeAttr("player-color");
                for (i = 0, l = markedElements.edgeIds.length; i < l; i++) {
                    parentElement.find('.edge[edge-id="' + markedElements.edgeIds[i] + '"]')
                            .attr("marked", true)
                            .attr("player-color", markedElements.playerColor);
                }

                // Nodes
                parentElement.find(".node[marked]")
                        .removeAttr("marked")
                        .removeAttr("player-color");
                for (i = 0, l = markedElements.nodeIds.length; i < l; i++) {
                    parentElement.find('.node[node-id="' + markedElements.nodeIds[i] + '"]')
                            .attr("marked", true)
                            .attr("player-color", markedElements.playerColor);
                }

                // Hexes
                //TODO: write code here for hex choosing (following user story)
            }

        }]);