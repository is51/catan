import { Injectable } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

import { MarkingService } from 'app/play/shared/services/marking.service';

@Injectable()
export class DrawMapMarkingService {

    constructor(
        private _marking: MarkingService,
        private _dom: BrowserDomAdapter) {}

    updateByType(element: Element, type: string, typeClass: string) {
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