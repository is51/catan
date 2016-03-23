import { Component, ElementRef, OnInit } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

import { MarkingService } from 'app/play/shared/services/marking.service';
import { SelectService } from 'app/play/shared/services/select.service';
import { DomHelper } from 'app/shared/services/dom/dom.helper';
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
        DrawMapHelper,
        DomHelper
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
        private _marking: MarkingService,
        private _select: SelectService) { }

    ngOnInit() {
        this._createCanvas();
        this._subscribeOnMapElementsClick();
        this._drawMapService.drawMap(this._canvas, this.game, this.game.map);
        this._subscribeOnMarkingChanging();
    }

    private _createCanvas() {
        this._canvas = this._dom.createElementNS(this._drawMapService.NS, 'svg');
        this._dom.setAttribute(this._canvas, 'preserveAspectRatio', CANVAS_PRESERVE_ASPECT_RATIO);
        this._dom.appendChild(this._element.nativeElement, this._canvas);
    }

    private _subscribeOnMapElementsClick() {
        //TODO: Subscribing on click is much complex when it was with jquery (make it in dom helper?)
        this._dom.on(this._canvas, 'click', event => {
            let element = this._domHelper.getClosest(this._canvas, event.target, this._drawMapService.NODE_SELECTOR);
            if (element && this._dom.getAttribute(element, 'marked')) {
                let id = +this._dom.getAttribute(element, 'node-id');
                this._select.select('node', id);
                return;
            }

            let element = this._domHelper.getClosest(this._canvas, event.target, this._drawMapService.EDGE_SELECTOR);
            if (element && this._dom.getAttribute(element, 'marked')) {
                let id = +this._dom.getAttribute(element, 'edge-id');
                this._select.select('edge', id);
                return;
            }

            let element = this._domHelper.getClosest(this._canvas, event.target, this._drawMapService.HEX_SELECTOR);
            if (element && this._dom.getAttribute(element, 'marked')) {
                let id = +this._dom.getAttribute(element, 'hex-id');
                this._select.select('hex', id);
            }
        });
    }

    private _subscribeOnMarkingChanging() {
        this._marking.onUpdate('hexes', () => this._updateMapMarkingByType(this._canvas, 'hexes', 'hex'));
        this._marking.onUpdate('nodes', () => this._updateMapMarkingByType(this._canvas, 'nodes', 'node'));
        this._marking.onUpdate('edges', () => this._updateMapMarkingByType(this._canvas, 'edges', 'edge'));
    }

    private _updateMapMarkingByType(element: Element, type: string, typeClass: string) {
        let previousMarked = this._dom.querySelectorAll(element, '.' + typeClass + '[marked]');
        for (let elem of previousMarked) {
            this._dom.removeAttribute(elem, 'marked');
            this._dom.removeAttribute(elem, 'player-color');
        }

        let marking = this._marking.get(type);
        if (marking) {
            marking.ids.forEach(id => {
                let elem = this._dom.querySelector(element, '.' + typeClass + '[' + typeClass + '-id="' + id + '"]');
                this._dom.setAttribute(elem, 'marked', "true");
                if (marking.player) {
                    this._dom.setAttribute(elem, 'player-color', <string>marking.player.colorId);
                }
            });
        }
    }
}