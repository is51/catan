import { Component, ElementRef, OnInit } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

import { DrawMapService } from './services/draw-map.service';
import { DrawMapHelper } from './helpers/draw-map.helper';

import { Game } from 'app/shared/domain/game';

const CANVAS_PRESERVE_ASPECT_RATIO = "xMidYMid meet";

@Component({
    selector: 'ct-game-map',
    template: '',
    //TODO: find way use css like that:  (instead of link in index.html)
    //try just add to all elements attr like "_ngcontent-cgr-10" - same throw all css scope
    //styleUrls: ['app/play/game-map/game-map.component.css'],
    providers: [
        BrowserDomAdapter,
        DrawMapService,
        DrawMapHelper
    ],
    inputs: ['game']
})

export class GameMapComponent implements OnInit {
    game: Game;

    private _canvas: Element;

    constructor(
        private _element: ElementRef,
        private _dom: BrowserDomAdapter,
        private _drawMapService: DrawMapService) { }

    ngOnInit() {
        this._createCanvas();

        //TODO: do this on every update (later - don't redraw, just update some elements)
        this._drawMapService.drawMap(this._canvas, this.game, this.game.map);
    }

    private _createCanvas() {
        //this._canvas = this._dom.createElement('svg');
        this._canvas = this._dom.createElementNS(this._drawMapService.NS, 'svg');
        this._dom.setAttribute(this._canvas, 'preserveAspectRatio', CANVAS_PRESERVE_ASPECT_RATIO);
        this._dom.appendChild(this._element.nativeElement, this._canvas);
    }
}