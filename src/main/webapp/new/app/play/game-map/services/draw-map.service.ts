import { Injectable } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

import { DrawMapHelper } from '../helpers/draw-map.helper';
import { MapTemplatesService } from '../services/map-templates.service';

import { Game } from 'app/shared/domain/game';
import { GameMap } from 'app/shared/domain/game-map/game-map';
import { Node } from 'app/shared/domain/game-map/node';
import { Hex } from 'app/shared/domain/game-map/hex';
import { Edge, EdgeOrientation } from 'app/shared/domain/game-map/edge';
import { Point } from 'app/shared/domain/game-map/point';

const NS = 'http://www.w3.org/2000/svg';
const HEX_WIDTH = 50;
const HEX_HEIGHT = 25;
const PORT_DISTANCE = 9;
const ROBBED_DICE_OFFSET = {x: 5, y: -3};
const ROBBER_RESOURCE_OFFSET = {x: -5, y: 2};

@Injectable()
export class DrawMapService {
    HEX_SELECTOR: string = '.hex';
    NODE_SELECTOR: string = '.node';
    EDGE_SELECTOR: string = '.edge';
    ROBBER_SELECTOR: string = '.robber';
    NS: string = NS;

    constructor(
        private _templates: MapTemplatesService,
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

        let diceCoords = (hex.robbed) ? ROBBED_DICE_OFFSET : {x: 0, y: 0};

        this._dom.setInnerHTML(group,
            this._templates.get('hex-bg') +
            this._templates.get('hex-resource-' + hex.getTypeToString().toLowerCase()) +
            this._templates.get('hex-dice', {
                number: (hex.dice)?hex.dice:'',
                x: diceCoords.x,
                y: diceCoords.y
            })
        );

        hex.onUpdate(() => this.updateHex(canvas, group, hex));
    }

    updateHex(canvas: Element, element: Element, hex: Hex) {
        // Updatable properties of HEX:
        // 1. hex.robbed
        // --

        let hexDice = this._dom.querySelector(element, '.dice');

        if (hex.robbed) {
            this._dom.setAttribute(hexDice, 'transform', 'translate('+ROBBED_DICE_OFFSET.x+', '+ROBBED_DICE_OFFSET.y+')');
            this.updateRobber(canvas, hex);
        } else {
            this._dom.setAttribute(hexDice, 'transform', 'translate(0, 0)');
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
        let robbedHexCoords = this._helper.getHexCoords(robbedHex, HEX_WIDTH, HEX_HEIGHT);
        let offset = (robbedHex.dice) ? ROBBER_RESOURCE_OFFSET : {x: 0, y: 0};
        let coords = {
            x: robbedHexCoords.x + offset.x,
            y: robbedHexCoords.y + offset.y
        };

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
                this.drawSettlement(group, <Point>{x: 0, y: 0}, colorId);
            }
            if (node.hasCity()) {
                this.drawCity(group, <Point>{x: 0, y: 0}, colorId);
            }
        } else {
            this.drawEmptyNode(group, <Point>{x: 0, y: 0});
        }

        if (node.hasPort()) {
            this.drawPort(canvas, coords, this._helper.getPortOffset(node, PORT_DISTANCE), node.getPortToString());
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
                this.drawSettlement(element, <Point>{x: 0, y: 0}, colorId);
            }
            if (node.hasCity()) {
                this.drawCity(element, <Point>{x: 0, y: 0}, colorId);
            }
        } else {
            this.drawEmptyNode(element, <Point>{x: 0, y: 0});
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
            this._templates.get('settlement', {colorId})
        );
    }

    drawCity(canvas: Element, coords: Point, colorId: number) {
        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + coords.x + ',' + coords.y + ')');
        this._dom.setAttribute(group, 'class', 'city');
        this._dom.appendChild(canvas, group);

        this._dom.setInnerHTML(group,
            this._templates.get('city', {colorId})
        );
    }

    drawPort(canvas: Element, nodeCoords: Point, offset: Point, portTypeString: string) {
        let finalCoords = <Point>{
            x: nodeCoords.x + offset.x,
            y: nodeCoords.y + offset.y
        };

        let group = this._dom.createElementNS(NS, 'g');
        this._dom.setAttribute(group, 'transform', 'translate(' + finalCoords.x + ',' + finalCoords.y + ')');
        this._dom.setAttribute(group, 'class', 'port');
        this._dom.appendChild(canvas, group);

        this._dom.setInnerHTML(group,
            this._templates.get('port', {type: portTypeString.toLowerCase()})
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
            this.drawRoad(group, <Point>{x: 0, y: 0}, edge.orientation, colorId);
        } else {
            this.drawEmptyEdge(group, <Point>{x: 0, y: 0}, edge.orientation);
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
            this.drawRoad(element, <Point>{x: 0, y: 0}, edge.orientation, colorId);
        } else {
            this.drawEmptyEdge(element, <Point>{x: 0, y: 0}, edge.orientation);
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
            this._dom.setInnerHTML(group, this._templates.get('road-vertical', {colorId}));
        } else {
            this._dom.setInnerHTML(group, this._templates.get('road-horizontal', {colorId}));
        }
    }

}