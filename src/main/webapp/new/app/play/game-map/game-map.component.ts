import { Component, ElementRef, OnInit, OnChanges, DoCheck } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

import { MarkingService } from 'app/play/shared/services/marking.service';
import { SelectService } from 'app/play/shared/services/select.service';
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

export class GameMapComponent implements OnInit, OnChanges, DoCheck {
    game: Game;

    private _canvas: Element;

    //TODO: do this in right way
    prevMarkingHexes;
    prevMarkingNodes;
    prevMarkingEdges;

    constructor(
        private _element: ElementRef,
        private _dom: BrowserDomAdapter,
        private _drawMapService: DrawMapService,
        private _marking: MarkingService,
        private _select: SelectService) { }

    ngOnInit() {
        this._createCanvas();
        this._subscribeOnMapElementsClick();
    }

    private _createCanvas() {
        this._canvas = this._dom.createElementNS(this._drawMapService.NS, 'svg');
        this._dom.setAttribute(this._canvas, 'preserveAspectRatio', CANVAS_PRESERVE_ASPECT_RATIO);
        this._dom.appendChild(this._element.nativeElement, this._canvas);
    }

    private _subscribeOnMapElementsClick() {
        //TODO: Subscribing on click is much complex when it was with jquery (make own dom adapter?)
        this._dom.on(this._canvas, 'click', event => {
            let element = this._getClosestElement(this._canvas, event.target, this._drawMapService.NODE_SELECTOR);
            if (element && this._dom.getAttribute(element, 'marked')) {
                let id = +this._dom.getAttribute(element, 'node-id');
                this._select.select('node', id);
                return;
            }

            let element = this._getClosestElement(this._canvas, event.target, this._drawMapService.EDGE_SELECTOR);
            if (element && this._dom.getAttribute(element, 'marked')) {
                let id = +this._dom.getAttribute(element, 'edge-id');
                this._select.select('edge', id);
                return;
            }

            let element = this._getClosestElement(this._canvas, event.target, this._drawMapService.HEX_SELECTOR);
            if (element && this._dom.getAttribute(element, 'marked')) {
                let id = +this._dom.getAttribute(element, 'hex-id');
                this._select.select('hex', id);
            }
        });
    }

    //TODO: find other way/method or create own dom adapter to avoid this method here
    private _getClosestElement(canvas: Element, element: Element|Node, selector: string) {
        do {
            if (element === canvas) {
                return null;
            }

            if (this.elementMatches(element, selector)) {
                return element;
            }

            element = this._dom.parentElement(element);
        }
        while (element);

        return null;
    }

    //TODO: find other way or create own dom adapter to avoid this method here
    private elementMatches(element: Element|Node, selector: string) {
        var matches = document.querySelectorAll(selector);
        return Array.prototype.some.call(matches, e => e === element);
    }

    ngOnChanges() {

    }

    ngDoCheck() {
        //TODO: it should happen only when something changes in the map
        //TODO: it should be not redrawing but just updating of map -- needs to be totally refactored
        this._updateMap();

        //TODO: it's useless because map is updated on every check, and after every map redrawing marking should be updated
        //it'll stop to be useless when redrawing of the map is changed to updating
        this._checkMarking();
    }

    private _updateMap() {
        this._drawMapService.drawMap(this._canvas, this.game, this.game.map);
        this._updateMapMarking(this._canvas);
    }

    //TODO: do this in right way (probably using differ services)
    private _checkMarking() {
        let isMarkingHexesChanged = this.prevMarkingHexes !== this._marking.get('hexes');
        let isMarkingNodesChanged = this.prevMarkingNodes !== this._marking.get('nodes');
        let isMarkingEdgesChanged = this.prevMarkingEdges !== this._marking.get('edges');

        if (isMarkingHexesChanged || isMarkingNodesChanged || isMarkingEdgesChanged) {
            this._updateMapMarking(this._canvas);
        }

        this.prevMarkingHexes = this._marking.get('hexes');
        this.prevMarkingNodes = this._marking.get('nodes');
        this.prevMarkingEdges = this._marking.get('edges');
    }

    //TODO: move this somewhere
    private _updateMapMarking(element: Element) {
        this._updateMapMarkingByType(element, 'hexes', 'hex');
        this._updateMapMarkingByType(element, 'nodes', 'node');
        this._updateMapMarkingByType(element, 'edges', 'edge');
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