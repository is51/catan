import { Component, ElementRef, OnInit } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

import { Game } from 'app/shared/domain/game';
import { GameMap } from 'app/shared/domain/game-map/game-map';
import { Node } from 'app/shared/domain/game-map/node';

const HEX_WIDTH = 50;
const HEX_HEIGHT = 25;
const OFFSET_X = 102;
const OFFSET_Y = 80;
const PORT_WIDTH = Math.round(HEX_HEIGHT / 2.2);
const PORT_HEIGHT = Math.round(HEX_HEIGHT / 2.2);

@Component({
    selector: 'ct-game-map-overview',
    template: '',
    //TODO: find way use css like that:  (instead of link in index.html)
    //try just add to all elements attr like "_ngcontent-cgr-10" - same throw all css scope
    //styleUrls: ['app/menu/game-page/game-room/game-map-overview/game-map-overview.component.css'],
    providers: [BrowserDomAdapter],
    inputs: ['game']
})

export class GameMapOverviewComponent implements OnInit {
    game: Game;

    constructor(
        private _element: ElementRef,
        private _dom: BrowserDomAdapter) { }

    ngOnInit() {
        let canvas = this._dom.createElement("div");
        this._dom.addClass(canvas, 'canvas');
        this._dom.appendChild(this._element.nativeElement, canvas);

        this._drawMap(this.game.map, canvas);
    }

    private _drawMap(map: GameMap, canvas: HTMLElement) {
        for (let item of map.hexes) {

            let elem = this._dom.createElement("div");
            this._dom.addClass(elem, 'hex');
            this._dom.addClass(elem, item.getTypeToString().toLowerCase());
            this._dom.setStyle(elem, 'width', HEX_WIDTH + 'px');
            this._dom.setStyle(elem, 'height', HEX_HEIGHT + 'px');
            this._dom.setStyle(elem, 'left', OFFSET_X + MapHelper.getPositionX(item.x, item.y) + 'px');
            this._dom.setStyle(elem, 'top', OFFSET_Y + MapHelper.getPositionY(item.x, item.y) + 'px');
            this._dom.appendChild(canvas, elem);

            let elemCoords = this._dom.createElement("div");
            this._dom.addClass(elemCoords, 'coords');
            this._dom.setStyle(elemCoords, 'width', HEX_WIDTH + 'px');
            this._dom.setStyle(elemCoords, 'height', HEX_HEIGHT + 'px');
            this._dom.setStyle(elemCoords, 'font-size', Math.round(HEX_HEIGHT/3) + 'px');
            this._dom.appendChild(elemCoords, this._dom.createTextNode(item.x + ',' + item.y));
            this._dom.appendChild(elem, elemCoords);

            let elemDice = this._dom.createElement("div");
            this._dom.addClass(elemDice, 'dice');
            this._dom.setStyle(elemDice, 'width', HEX_WIDTH + 'px');
            this._dom.setStyle(elemDice, 'height', HEX_HEIGHT + 'px');
            this._dom.setStyle(elemDice, 'font-size', Math.round(HEX_HEIGHT/2) + 'px');
            this._dom.setStyle(elemDice, 'line-height', HEX_HEIGHT + 'px');
            let elemNumber: HTMLElement | Text;
            if (item.robbed) {
                elemNumber = this._dom.createElement('span');
                this._dom.addClass(elemNumber, 'glyphicon');
                this._dom.addClass(elemNumber, 'glyphicon-fire');
            } else {
                elemNumber = this._dom.createTextNode(<string>item.dice);
            }
            this._dom.appendChild(elemDice, elemNumber);
            this._dom.appendChild(elem, elemDice);
        }

        for (let item of map.nodes) {

            let elem = this._dom.createElement("div");
            this._dom.addClass(elem, 'node');
            this._dom.addClass(elem, 'node-'+item.id);
            this._dom.addClass(elem, item.getPortToString().toLowerCase());
            this._dom.setStyle(elem, 'width', PORT_WIDTH + 'px');
            this._dom.setStyle(elem, 'height', PORT_HEIGHT + 'px');
            this._dom.setStyle(elem, 'left', OFFSET_X + MapHelper.getPositionNodeX(item) + 'px');
            this._dom.setStyle(elem, 'top', OFFSET_Y + MapHelper.getPositionNodeY(item) + 'px');
            if (item.hasPort()) {
                let elemPort = this._dom.createElement('span');
                this._dom.addClass(elemPort, 'glyphicon');
                this._dom.addClass(elemPort, 'glyphicon-plane');
                this._dom.appendChild(elem, elemPort);
            }
            this._dom.appendChild(canvas, elem);
        }
    }
}

class MapHelper {
    static getPosition(where: any, what: any) {
        for (let k in where) {
            if (where[k] === what) {
                return k;
            }
        }
    }

    static getPositionX(x: number, y: number) {
        return x * HEX_WIDTH + y * HEX_WIDTH / 2;
    }

    static getPositionY(x: number, y: number) {
        return y * HEX_HEIGHT;
    }

    static getHexOfNode(item: Node) {
        for (let i in item.hexes) {
            if (item.hexes[i]) {
                return item.hexes[i];
            }
        }
    }

    static getPositionNodeX(node: Node) {
        let hex = this.getHexOfNode(node);
        let position = this.getPosition(hex.nodes, node);
        let hexX = this.getPositionX(hex.x, hex.y);
        let x = hexX - Math.round(PORT_WIDTH/2);

        if (position === "top" || position === "bottom") { x += Math.round(HEX_WIDTH/2); }
        if (position === "topRight" || position === "bottomRight") { x += HEX_WIDTH; }

        return x;
    }

    static getPositionNodeY(node: Node) {
        let hex = this.getHexOfNode(node);
        let position = this.getPosition(hex.nodes, node);
        let hexY = this.getPositionY(hex.x, hex.y);
        let y = hexY - Math.round(PORT_HEIGHT/2);

        if (position === "bottomRight" || position === "bottom" || position === "bottomLeft") { y += HEX_HEIGHT; }

        return y;
    }
}