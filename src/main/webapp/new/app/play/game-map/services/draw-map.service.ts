import { Injectable } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

import { DrawMapHelper } from '../helpers/draw-map.helper';

import { Game } from 'app/shared/domain/game';
import { GameMap } from 'app/shared/domain/game-map/game-map';
import { Node } from 'app/shared/domain/game-map/node';
import { Hex } from 'app/shared/domain/game-map/hex';
import { Edge, EdgeOrientation } from 'app/shared/domain/game-map/edge';
import { Point } from 'app/shared/domain/game-map/point';

const NS = 'http://www.w3.org/2000/svg';
const HEX_WIDTH = 78;
const HEX_HEIGHT = 40;
const HEX_ROUNDING = 4;
const EDGE_WIDTH = 3;

@Injectable()
export class DrawMapService {
    HEX_SELECTOR: string = '.hex';
    NODE_SELECTOR: string = '.node';
    EDGE_SELECTOR: string = '.edge';
    NS: string = NS;

    constructor(
        private _helper: DrawMapHelper,
        private _dom: BrowserDomAdapter) {}

    drawMap(canvas: Element, game: Game, map: GameMap) {

        //canvas.clear();

        let coords;
        let actualSize = { //TODO: try to calculate actual image (map) size somehow more pretty
            xMin: null,
            xMax: null,
            yMin: null,
            yMax: null
        };

        map.hexes.forEach(hex => {
            coords = this._helper.getHexCoords(hex, HEX_WIDTH, HEX_HEIGHT);
            actualSize = this._updateActualSize(actualSize, coords.x, coords.y, HEX_HEIGHT, HEX_HEIGHT);
            this.drawHex(canvas, coords, hex);
        });

        map.edges.forEach(edge => {
            coords = this._helper.getEdgeCoords(edge, HEX_WIDTH, HEX_HEIGHT);
            this.drawEdge(canvas, game, coords, edge);
        });

        map.nodes.forEach(node => {
            coords = this._helper.getNodeCoords(node, HEX_WIDTH, HEX_HEIGHT);
            this.drawNode(canvas, game, coords, node);
        });

        this._setViewBox(canvas, actualSize);
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
        let additionalWidth = 2 * HEX_WIDTH;
        let additionalHeight = 2.5 * HEX_HEIGHT;
        let width = actualSize.xMax - actualSize.xMin + additionalWidth;
        let height = actualSize.yMax - actualSize.yMin + additionalHeight;
        let offsetX = actualSize.xMin - HEX_WIDTH;
        let offsetY = actualSize.yMin - HEX_HEIGHT;

        this._dom.setAttribute(canvas, 'viewBox', offsetX + ' ' + offsetY + ' ' + width + ' ' + height);
    }

    drawHex(canvas: Element, coords: Point, hex: Hex) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'hex');
        this._dom.setAttribute(group, 'hex-id', <string>hex.id);
        this._dom.appendChild(canvas, group);

        // Background rectangle
        let rectangle = this._dom.createElementNS(NS, 'rect');
        this._dom.setAttribute(rectangle, 'x', <string>(-HEX_WIDTH/2));
        this._dom.setAttribute(rectangle, 'y', <string>(-HEX_HEIGHT/2));
        this._dom.setAttribute(rectangle, 'width', <string>HEX_WIDTH);
        this._dom.setAttribute(rectangle, 'height', <string>HEX_HEIGHT);
        this._dom.setAttribute(rectangle, 'rx', <string>HEX_ROUNDING);
        this._dom.setAttribute(rectangle, 'ry', <string>HEX_ROUNDING);
        this._dom.setAttribute(rectangle, 'transform', 'scale(0.95, 0.9)');
        this._dom.setAttribute(rectangle, 'class', 'hex-background');
        this._dom.setAttribute(rectangle, 'resource-type', hex.getTypeToString().toLowerCase());
        this._dom.appendChild(group, rectangle);

        // Dice text
        let hexDice = this._dom.createElementNS(NS, 'text');
        this._dom.setAttribute(hexDice, 'x', "1");
        this._dom.setAttribute(hexDice, 'y', "5");
        this._dom.setAttribute(hexDice, 'class', 'dice');
        this._dom.setAttribute(hexDice, 'text-anchor', 'middle');
        this._dom.appendChild(group, hexDice);

        // Dice text element
        let hexDiceText = ((hex.robbed) ? '(R)' : '') + ((hex.dice) ? hex.dice : '');
        let hexDiceTextNode = this._dom.createTextNode(hexDiceText);
        this._dom.appendChild(hexDice, hexDiceTextNode);
    }

    drawNode(canvas: Element, game: Game, coords: Point, node: Node) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'node');
        this._dom.setAttribute(group, 'node-id', <string>node.id);
        this._dom.appendChild(canvas, group);

        if (node.hasPort()) {
            this.drawPort(group, this._helper.getPortOffset(node), node.getPortToString());
        }

        if (node.building) {
            let colorId = game.getPlayer(node.building.ownerPlayerId).colorId;

            if (node.hasSettlement()) {
                this.drawSettlement(group, <Point>{x: 0, y: 0}, colorId);
            }
            if (node.hasCity()) {
                this.drawCity(group, <Point>{x: 0, y: 0}, colorId);
            }
        } else {
            this.drawEmptyNode(group, <Point>{x: 0, y: 0});
        }
    }

    drawEmptyNode(canvas: Element, coords: Point) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'blank-node');
        this._dom.appendChild(canvas, group);

        var NODE_BACKGROUND_RADIUS = 7;
        let circle = this._dom.createElementNS(NS, 'circle');
        this._dom.setAttribute(circle, 'cx', '0');
        this._dom.setAttribute(circle, 'cy', '0');
        this._dom.setAttribute(circle, 'r', <string>NODE_BACKGROUND_RADIUS);
        this._dom.appendChild(group, circle);
    }

    drawSettlement(canvas: Element, coords: Point, colorId: number) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'settlement');
        this._dom.appendChild(canvas, group);

        var SETTLEMENT_RADIUS = 9;
        let circle = this._dom.createElementNS(NS, 'circle');
        this._dom.setAttribute(circle, 'cx', '0');
        this._dom.setAttribute(circle, 'cy', '0');
        this._dom.setAttribute(circle, 'r', <string>SETTLEMENT_RADIUS);
        this._dom.setAttribute(circle, 'player-color', <string>colorId);
        this._dom.appendChild(group, circle);

        let text = this._dom.createElementNS(NS, 'text');
        this._dom.setAttribute(text, 'x', '0');
        this._dom.setAttribute(text, 'y', '4');
        this._dom.setAttribute(text, 'text-anchor', 'middle');
        this._dom.appendChild(circle, text);

        let textNode = this._dom.createTextNode('s');
        this._dom.appendChild(text, textNode);
    }

    drawCity(canvas: Element, coords: Point, colorId: number) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'city');
        this._dom.appendChild(canvas, group);

        var CITY_RADIUS = 9;
        let circle = this._dom.createElementNS(NS, 'circle');
        this._dom.setAttribute(circle, 'cx', '0');
        this._dom.setAttribute(circle, 'cy', '0');
        this._dom.setAttribute(circle, 'r', <string>CITY_RADIUS);
        this._dom.setAttribute(circle, 'player-color', <string>colorId);
        this._dom.appendChild(group, circle);

        let text = this._dom.createElementNS(NS, 'text');
        this._dom.setAttribute(text, 'x', '0');
        this._dom.setAttribute(text, 'y', '5');
        this._dom.setAttribute(text, 'text-anchor', 'middle');
        this._dom.appendChild(circle, text);

        let textNode = this._dom.createTextNode('C');
        this._dom.appendChild(text, textNode);
    }

    drawPort(canvas: Element, offset: Point, portTypeString: string) {
        let PORT_DISTANCE = 12;
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + offset.x * PORT_DISTANCE + ',' + offset.y * PORT_DISTANCE + ')');
        this._dom.setAttribute(group, 'class', 'port');
        this._dom.appendChild(canvas, group);

        let path = this._dom.createElementNS(NS, 'path');
        this._dom.setAttribute(path, 'd', 'M0 -6 L-5 3 L5 3 Z');
        this._dom.setAttribute(path, 'resource-type', portTypeString.toLowerCase());
        this._dom.appendChild(group, path);
    }

    drawEdge(canvas: Element, game: Game, coords: Point, edge: Edge) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'edge');
        this._dom.setAttribute(group, 'edge-id', <string>edge.id);
        this._dom.appendChild(canvas, group);

        if (edge.building) {
            let colorId = game.getPlayer(edge.building.ownerPlayerId).colorId;
            this.drawRoad(group, <Point>{x: 0, y: 0}, edge.orientation, colorId);
        } else {
            this.drawEmptyEdge(group, <Point>{x: 0, y: 0}, edge.orientation);
        }
    }

    drawEmptyEdge(canvas: Element, coords: Point, orientation: EdgeOrientation) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'blank-edge');
        this._dom.appendChild(canvas, group);

        let rect = this._dom.createElementNS(NS, 'rect');
        if (orientation === EdgeOrientation.VERTICAL) {
            this._dom.setAttribute(rect, 'x', <string>(-EDGE_WIDTH));
            this._dom.setAttribute(rect, 'y', <string>(-HEX_HEIGHT/2));
            this._dom.setAttribute(rect, 'width', <string>(2*EDGE_WIDTH));
            this._dom.setAttribute(rect, 'height', <string>HEX_HEIGHT);
        } else {
            this._dom.setAttribute(rect, 'x', <string>(-HEX_WIDTH/4));
            this._dom.setAttribute(rect, 'y', <string>(-EDGE_WIDTH));
            this._dom.setAttribute(rect, 'width', <string>(HEX_WIDTH/2));
            this._dom.setAttribute(rect, 'height', <string>(2*EDGE_WIDTH));
        }
        this._dom.appendChild(group, rect);
    }

    drawRoad(canvas: Element, coords: Point, orientation: EdgeOrientation, colorId: number) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'road');
        this._dom.appendChild(canvas, group);

        let rect = this._dom.createElementNS(NS, 'rect');
        if (orientation === EdgeOrientation.VERTICAL) {
            this._dom.setAttribute(rect, 'x', <string>(-EDGE_WIDTH));
            this._dom.setAttribute(rect, 'y', <string>(-HEX_HEIGHT/2));
            this._dom.setAttribute(rect, 'width', <string>(2*EDGE_WIDTH));
            this._dom.setAttribute(rect, 'height', <string>HEX_HEIGHT);
        } else {
            this._dom.setAttribute(rect, 'x', <string>(-HEX_WIDTH/4));
            this._dom.setAttribute(rect, 'y', <string>(-EDGE_WIDTH));
            this._dom.setAttribute(rect, 'width', <string>(HEX_WIDTH/2));
            this._dom.setAttribute(rect, 'height', <string>(2*EDGE_WIDTH));
        }
        this._dom.setAttribute(rect, 'player-color', <string>colorId);
        this._dom.appendChild(group, rect);
    }

}