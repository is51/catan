import { Component, ElementRef, OnInit } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

import { MarkingService } from 'app/play/shared/services/marking.service';
import { SelectService } from 'app/play/shared/services/select.service';
import { DomHelper } from 'app/shared/services/dom/dom.helper';
import { DrawMapService } from './services/draw-map.service';
import { DrawMapMarkingService } from './services/draw-map-marking.service';
import { MapAnimationService } from './services/map-animation.service';
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
        DrawMapMarkingService,
        DrawMapHelper,
        DomHelper,
        MapAnimationService
    ],
    inputs: ['game']
})

export class GameMapComponent implements OnInit {
    game: Game;

    private _canvas: Element;

    constructor(
        private _element: ElementRef,
        private _dom: BrowserDomAdapter,
        private _domHelper: DomHelper,
        private _drawMapService: DrawMapService,
        private _drawMapMarkingService: DrawMapMarkingService,
        private _marking: MarkingService,
        private _select: SelectService,
        private _animation: MapAnimationService) { }

    ngOnInit() {
        this._createCanvas();
        this._subscribeOnMapElementsClick();
        this._drawMapService.drawMap(this._canvas, this.game, this.game.map);
        this._subscribeOnMarkingChanging();
        this._subscribeOnDiceThrowing();
    }

    private _createCanvas() {
        this._canvas = this._dom.createElementNS(this._drawMapService.NS, 'svg');
        this._dom.setAttribute(this._canvas, 'preserveAspectRatio', CANVAS_PRESERVE_ASPECT_RATIO);
        this._dom.appendChild(this._element.nativeElement, this._canvas);
    }

    private _subscribeOnMapElementsClick() {
        this._domHelper.on(this._canvas, 'click', this._drawMapService.NODE_SELECTOR, element => {
            if (this._dom.getAttribute(element, 'marked')) {
                let id = +this._dom.getAttribute(element, 'node-id');
                this._select.select('node', id);
            }
        });

        this._domHelper.on(this._canvas, 'click', this._drawMapService.EDGE_SELECTOR, element => {
            if (this._dom.getAttribute(element, 'marked')) {
                let id = +this._dom.getAttribute(element, 'edge-id');
                this._select.select('edge', id);
            }
        });

        this._domHelper.on(this._canvas, 'click', this._drawMapService.HEX_SELECTOR, element => {
            if (this._dom.getAttribute(element, 'marked')) {
                let id = +this._dom.getAttribute(element, 'hex-id');
                this._select.select('hex', id);
            }
        });
    }

    private _subscribeOnMarkingChanging() {
        let types = [
            ['hexes', 'hex'],
            ['nodes', 'node'],
            ['edges', 'edge']
        ];

        types.forEach(type => {
            this._drawMapMarkingService.updateByType(this._canvas, type[0], type[1]);
            this._marking.onUpdate(type[0], () => this._drawMapMarkingService.updateByType(this._canvas, type[0], type[1]));
        });
    }

    private _subscribeOnDiceThrowing() {
        this.game.dice.onThrow(value => {
            if (value === 7) {
                this._animation.robberThrown(this._canvas);
            } else {
                this._animation.hexDiceThrown(this._canvas, this.game.map, value);
            }
        });
    }
}