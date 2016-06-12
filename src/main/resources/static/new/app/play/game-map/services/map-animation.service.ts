import { Injectable } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

import { DrawMapService } from '../services/draw-map.service';
import { GameMap } from 'app/shared/domain/game-map/game-map';

const HEX_ANIMATED_CLASS = 'hex-animated';
const HEX_ANIMATED_DURATION = 3000;
const ROBBER_ANIMATED_CLASS = 'robber-animated';
const ROBBER_ANIMATED_DURATION = 3000;

@Injectable()
export class MapAnimationService {

    constructor(
        private _dom: BrowserDomAdapter,
        private _drawMapService: DrawMapService) { }

    hexDiceThrown(canvas: Element, map: GameMap, dice: number) {
        let hexesToAnimate = map.hexes
            .filter(hex => hex.dice === dice && !hex.robbed)
            .map(hex => this._dom.querySelector(canvas, this._drawMapService.HEX_SELECTOR + '[hex-id="'+hex.id+'"]'));

        hexesToAnimate.forEach(element => {
            this._dom.addClass(element, HEX_ANIMATED_CLASS);
        });

        setTimeout(() => {
            hexesToAnimate.forEach(element => {
                this._dom.removeClass(element, HEX_ANIMATED_CLASS);
            });
        }, HEX_ANIMATED_DURATION);
    }

    robberThrown(canvas: Element) {
        let element = this._dom.querySelector(canvas, this._drawMapService.ROBBER_SELECTOR);

        this._dom.addClass(element, ROBBER_ANIMATED_CLASS);

        setTimeout(() => {
            this._dom.removeClass(element, ROBBER_ANIMATED_CLASS);
        }, ROBBER_ANIMATED_DURATION);
    }
}