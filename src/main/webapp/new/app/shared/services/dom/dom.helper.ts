import { Injectable } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

@Injectable()
export class DomHelper {

    constructor(private _browserDomAdapter: BrowserDomAdapter) { }

    getClosest(root: Element, element: Element|Node, selector: string) {
        do {
            if (element === root) {
                return null;
            }

            if (this.matches(element, selector)) {
                return element;
            }

            element = this._browserDomAdapter.parentElement(element);
        }
        while (element);

        return null;
    }

    matches(element: Element|Node, selector: string) {
        var matches = document.querySelectorAll(selector);
        return Array.prototype.some.call(matches, e => e === element);
    }
}