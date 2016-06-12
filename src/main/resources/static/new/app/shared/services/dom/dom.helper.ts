import { Injectable } from 'angular2/core';
import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

//TODO: compare with http://blog.wearecolony.com/a-year-without-jquery/

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

    on(root: Element, eventName: string, selector: string, action: Function) {
        this._browserDomAdapter.on(root, eventName, event => {
            let element = this.getClosest(root, event.target, selector);
            if (element) {
                action(element, event);
            }
        });
    }
}