import { Injectable } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

import { DrawMapHelper } from '../helpers/draw-map.helper';
import { TemplatesService } from 'app/play/shared/services/templates.service';

import { Game } from 'app/shared/domain/game';
import { GameMap } from 'app/shared/domain/game-map/game-map';
import { Node } from 'app/shared/domain/game-map/node';
import { Hex } from 'app/shared/domain/game-map/hex';
import { Edge, EdgeOrientation } from 'app/shared/domain/game-map/edge';
import { Point } from 'app/shared/domain/game-map/point';

const NS = 'http://www.w3.org/2000/svg';
const HEX_WIDTH = 50;
const HEX_HEIGHT = 25;
const PORT_DISTANCE = 18;

const PORT_COLORS = {
    brick: '#43a8d9',
    wood: '#d5c867',
    sheep: '#48da83',
    wheat: '#a187aa',
    stone: '#f69f7e',
    any: '#cccccc'
};

const DICE_COLORS = {
    brick: {
        colorNumber: '#3699D1',
        colorMarkerGrad1: '#4BB0DD',
        colorMarkerGrad2: '#4BB0DD',
        colorMarkerGrad3: '#3699D1'
    },
    wood: {
        colorNumber: '#d2c14d',
        colorMarkerGrad1: '#d8ce80',
        colorMarkerGrad2: '#d8ce80',
        colorMarkerGrad3: '#d2c14d'
    },
    sheep: {
        colorNumber: '#00c553',
        colorMarkerGrad1: '#45d980',
        colorMarkerGrad2: '#45d980',
        colorMarkerGrad3: '#00c553'
    },
    wheat: {
        colorNumber: '#93729d',
        colorMarkerGrad1: '#aa93b2',
        colorMarkerGrad2: '#aa93b2',
        colorMarkerGrad3: '#93729d'
    },
    stone: {
        colorNumber: '#f38057',
        colorMarkerGrad1: '#e69677',
        colorMarkerGrad2: '#e69677',
        colorMarkerGrad3: '#f38057'
    },
};

const BUILDING_COLORS = {
    1: '#f6663d',
    2: '#727df4',
    3: '#ddcc00',
    4: '#00ff55'
};

const ROAD_COLORS = {
    1: '#f6663d',
    2: '#727df4',
    3: '#ddcc00',
    4: '#00ff55'
};

@Injectable()
export class DrawMapService {
    HEX_SELECTOR: string = '.hex';
    NODE_SELECTOR: string = '.node';
    EDGE_SELECTOR: string = '.edge';
    ROBBER_SELECTOR: string = '.robber';
    NS: string = NS;

    constructor(
        private _templates: TemplatesService,
        private _helper: DrawMapHelper,
        private _dom: BrowserDomAdapter) {}

    drawMap(canvas: Element, game: Game, map: GameMap) {

        this._clear(canvas);

        let actualSize = { //TODO: try to calculate actual image (map) size somehow more pretty
            xMin: null,
            xMax: null,
            yMin: null,
            yMax: null
        };

        this.drawMapBottom(canvas);

        this.drawPorts(canvas, map);

        map.hexes.forEach(hex => {
            let coords = this._helper.getHexCoords(hex, HEX_WIDTH, HEX_HEIGHT);
            actualSize = this._updateActualSize(actualSize, coords.x, coords.y, HEX_HEIGHT, HEX_HEIGHT);
            this.drawHex(canvas, coords, hex);
        });

        map.edges.forEach(edge => {
            let coords = this._helper.getEdgeCoords(edge, HEX_WIDTH, HEX_HEIGHT);
            this.drawEdge(canvas, game, coords, edge);
        });

        map.nodes.forEach(node => {
            let coords = this._helper.getNodeCoords(node, HEX_WIDTH, HEX_HEIGHT);
            this.drawNode(canvas, game, coords, node);
        });

        this.drawRobber(canvas, map);

        this.drawClouds(canvas);

        this._setViewBox(canvas, actualSize);
    }

    private _clear(element: Element) {
        this._dom.clearNodes(element);
    }

    private _updateActualSize(oldActualSize, x: number, y: number, w: number, h: number) {
        let newActualSize = {
            xMin: oldActualSize.xMin,
            xMax: oldActualSize.xMax,
            yMin: oldActualSize.yMin,
            yMax: oldActualSize.yMax
        };
        let xLeft = x - w / 2,
            xRight = x + w / 2,
            yTop = y - h / 2,
            yBottom = y + h / 2;

        if (oldActualSize.xMin === null || xLeft < oldActualSize.xMin) {
            newActualSize.xMin = xLeft;
        }
        if (oldActualSize.xMax === null || xRight > oldActualSize.xMax) {
            newActualSize.xMax = xRight;
        }
        if (oldActualSize.yMin === null || yTop < oldActualSize.yMin) {
            newActualSize.yMin = yTop;
        }
        if (oldActualSize.yMax === null || yBottom > oldActualSize.yMax) {
            newActualSize.yMax = yBottom;
        }

        return newActualSize;
    }

    private _setViewBox(canvas: Element, actualSize) {
        let additionalWidth = 3.9 * HEX_WIDTH;
        let additionalHeight = 2.8 * HEX_HEIGHT;
        let width = actualSize.xMax - actualSize.xMin + additionalWidth;
        let height = actualSize.yMax - actualSize.yMin + additionalHeight;
        let offsetX = actualSize.xMin - additionalWidth / 2;
        let offsetY = actualSize.yMin - additionalHeight / 2.5;

        this._dom.setAttribute(canvas, 'viewBox', offsetX + ' ' + offsetY + ' ' + width + ' ' + height);
    }

    drawMapBottom(canvas: Element) {
        //TODO: Map Bottom is hardcoded for default map
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'class', 'map-bottom');
        this._dom.appendChild(canvas, group);
        this._dom.setInnerHTML(group,
            this._templates.get('map-bottom')
        );
    }

    drawHex(canvas: Element, coords: Point, hex: Hex) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'hex');
        this._dom.setAttribute(group, 'hex-id', <string>hex.id);
        this._dom.appendChild(canvas, group);

        this._dom.setInnerHTML(group, this._getHexHTML(hex));

        hex.onUpdate(() => this.updateHex(canvas, group, hex));
    }

    private _getHexHTML(hex: Hex) {
        let html = this._templates.get('hex-bg');

        if (!hex.edges.bottomLeft.isJoint()) {
            html += this._templates.get('hex-bg-edge-bottom-left');
        }
        if (!hex.edges.bottomRight.isJoint()) {
            html += this._templates.get('hex-bg-edge-bottom-right');
        }
        if (!hex.edges.left.isJoint()) {
            html += this._templates.get('hex-bg-edge-left');
        }
        if (!hex.edges.right.isJoint()) {
            html += this._templates.get('hex-bg-edge-right');
        }
        if (!hex.edges.topLeft.isJoint()) {
            html += this._templates.get('hex-bg-edge-top-left');
        }
        if (!hex.edges.topRight.isJoint()) {
            html += this._templates.get('hex-bg-edge-top-right');
        }

        if (!hex.nodes.top.isJoint()) {
            html += this._templates.get('hex-bg-node-top');
        }
        if (!hex.nodes.bottom.isJoint()) {
            html += this._templates.get('hex-bg-node-bottom');
        }
        if (!hex.nodes.topRight.isJoint()) {
            html += this._templates.get('hex-bg-node-top-right');
            html += this._templates.get('hex-bg-node-right-top');
        }
        if (!hex.nodes.topLeft.isJoint()) {
            html += this._templates.get('hex-bg-node-top-left');
            html += this._templates.get('hex-bg-node-left-top');
        }
        if (!hex.nodes.bottomRight.isJoint()) {
            html += this._templates.get('hex-bg-node-bottom-right');
            html += this._templates.get('hex-bg-node-right-bottom');
        }
        if (!hex.nodes.bottomLeft.isJoint()) {
            html += this._templates.get('hex-bg-node-bottom-left');
            html += this._templates.get('hex-bg-node-left-bottom');
        }

        if (!hex.nodes.bottomRight.hexes.bottom && hex.nodes.bottomRight.hexes.topRight) {
            html += this._templates.get('hex-bg-node-bottom-right');
        }
        if (!hex.nodes.topRight.hexes.top && hex.nodes.topRight.hexes.bottomRight) {
            html += this._templates.get('hex-bg-node-top-right');
        }

        let resourceTypeStr = hex.getTypeToString().toLowerCase();

        html += this._templates.get('hex-' + resourceTypeStr);

        if (hex.dice) {
            html += this._templates.get('hex-dice', Object.assign({
                number: (hex.dice)?hex.dice:'',
                resourceType: resourceTypeStr,
            }, DICE_COLORS[resourceTypeStr]))
        }

        return html;
    }

    updateHex(canvas: Element, element: Element, hex: Hex) {
        // Updatable properties of HEX:
        // 1. hex.robbed
        // --

        let hexDice = this._dom.querySelector(element, '.dice');

        if (hex.robbed) {
            this.updateRobber(canvas, hex);
        }
    }

    drawRobber(canvas: Element, map: GameMap) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'class', 'robber');
        this._dom.appendChild(canvas, group);

        this._dom.setInnerHTML(group,
            this._templates.get('robber')
        );

        let robbedHex = map.hexes.filter(hex => hex.robbed)[0];
        this.updateRobber(canvas, robbedHex);
    }

    updateRobber(canvas: Element, robbedHex: Hex) {
        let coords = this._helper.getHexCoords(robbedHex, HEX_WIDTH, HEX_HEIGHT);
        let robber = this._dom.querySelector(canvas, '.robber');
        this._dom.setAttribute(robber, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
    }

    drawNode(canvas: Element, game: Game, coords: Point, node: Node) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'node');
        this._dom.setAttribute(group, 'node-id', <string>node.id);
        this._dom.appendChild(canvas, group);

        if (node.building) {
            let colorId = game.getPlayer(node.building.ownerPlayerId).colorId;

            if (node.hasSettlement()) {
                this.drawSettlement(group, new Point(0, 0), colorId);
            }
            if (node.hasCity()) {
                this.drawCity(group, new Point(0, 0), colorId);
            }
        } else {
            this.drawEmptyNode(group, new Point(0, 0));
        }

        node.onUpdate(() => this.updateNode(group, game, node));
    }

    updateNode(element: Element, game: Game, node: Node) {
        // Updatable properties of NODE:
        // 1. node.building
        // --

        this._dom.clearNodes(element);

        if (node.building) {
            let colorId = game.getPlayer(node.building.ownerPlayerId).colorId;

            if (node.hasSettlement()) {
                this.drawSettlement(element, new Point(0, 0), colorId);
            }
            if (node.hasCity()) {
                this.drawCity(element, new Point(0, 0), colorId);
            }
        } else {
            this.drawEmptyNode(element, new Point(0, 0));
        }
    }

    drawEmptyNode(canvas: Element, coords: Point) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'blank-node');
        this._dom.appendChild(canvas, group);

        this._dom.setInnerHTML(group,
            this._templates.get('blank-node')
        );
    }

    drawSettlement(canvas: Element, coords: Point, colorId: number) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'settlement');
        this._dom.appendChild(canvas, group);

        this._dom.setInnerHTML(group,
            this._templates.get('settlement', {color: BUILDING_COLORS[colorId]})
        );
    }

    drawCity(canvas: Element, coords: Point, colorId: number) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'city');
        this._dom.appendChild(canvas, group);

        this._dom.setInnerHTML(group,
            this._templates.get('city', {color: BUILDING_COLORS[colorId]})
        );
    }

    drawEdge(canvas: Element, game: Game, coords: Point, edge: Edge) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'edge');
        this._dom.setAttribute(group, 'edge-id', <string>edge.id);
        this._dom.appendChild(canvas, group);

        if (edge.building) {
            let colorId = game.getPlayer(edge.building.ownerPlayerId).colorId;
            this.drawRoad(group, new Point(0, 0), edge.orientation, colorId);
        } else {
            this.drawEmptyEdge(group, new Point(0, 0), edge.orientation);
        }

        edge.onUpdate(() => this.updateEdge(group, game, edge));
    }

    updateEdge(element: Element, game: Game, edge: Edge) {
        // Updatable properties of EDGE:
        // 1. edge.building
        // --

        this._dom.clearNodes(element);

        if (edge.building) {
            let colorId = game.getPlayer(edge.building.ownerPlayerId).colorId;
            this.drawRoad(element, new Point(0, 0), edge.orientation, colorId);
        } else {
            this.drawEmptyEdge(element, new Point(0, 0), edge.orientation);
        }
    }

    drawEmptyEdge(canvas: Element, coords: Point, orientation: EdgeOrientation) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'blank-edge');
        this._dom.appendChild(canvas, group);

        if (orientation === EdgeOrientation.VERTICAL) {
            this._dom.setInnerHTML(group, this._templates.get('blank-edge-vertical'));
        } else {
            this._dom.setInnerHTML(group, this._templates.get('blank-edge-horizontal'));
        }
    }

    drawRoad(canvas: Element, coords: Point, orientation: EdgeOrientation, colorId: number) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'road');
        this._dom.appendChild(canvas, group);

        let rect = this._dom.createElementNS(NS, 'rect');
        if (orientation === EdgeOrientation.VERTICAL) {
            this._dom.setInnerHTML(group, this._templates.get('road-vertical', {color: ROAD_COLORS[colorId]}));
        } else {
            this._dom.setInnerHTML(group, this._templates.get('road-horizontal', {color: ROAD_COLORS[colorId]}));
        }
    }

    drawPorts(canvas: Element, map: GameMap) {
        let portPairs = this._helper.getPortPairs(map.nodes);

        portPairs.forEach(pair => {

            let node1Coords = this._helper.getNodeCoords(pair.firstNode, HEX_WIDTH, HEX_HEIGHT);
            let node1PortOffset = this._helper.getPortOffset(pair.firstNode, PORT_DISTANCE);
            let node1PortCoords =  node1Coords.plus(node1PortOffset);

            let node2Coords = this._helper.getNodeCoords(pair.secondNode, HEX_WIDTH, HEX_HEIGHT);
            let node2PortOffset = this._helper.getPortOffset(pair.secondNode, PORT_DISTANCE);
            let node2PortCoords =  node2Coords.plus(node2PortOffset);

            let coords = node1PortCoords.average(node2PortCoords);

            let node1OffsetBack = node1Coords.minus(coords);
            let node2OffsetBack = node2Coords.minus(coords);

            let portTemplateName = (pair.edge.isVertical()) ? 'port-vertical' : 'port-horizontal';

            let resourceIconTemplate = this._templates.get('icon-' + pair.firstNode.getPortToString().toLowerCase());

            let group = this._dom.createElementNS(NS, 'g');
            this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
            this._dom.appendChild(canvas, group);
            this._dom.setInnerHTML(group,
                this._templates.get(portTemplateName, {
                    x1: node1OffsetBack.x,
                    x2: node2OffsetBack.x,
                    y1: node1OffsetBack.y,
                    y2: node2OffsetBack.y,
                    icon: resourceIconTemplate,
                    iconLabel: (pair.firstNode.hasPortAny()) ? '3:1' : '2:1'
                })
            );
        });
    }

    drawClouds(canvas: Element) {
        //TODO: Clouds are hardcoded for default map
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'class', 'clouds');
        this._dom.appendChild(canvas, group);
        this._dom.setInnerHTML(group,
            this._templates.get('clouds')
        );
    }

}