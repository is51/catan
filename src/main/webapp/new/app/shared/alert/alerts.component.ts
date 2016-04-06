import { Component, OnInit, DynamicComponentLoader, ElementRef } from 'angular2/core';
//import { BrowserDomAdapter } from "angular2/src/platform/browser/browser_adapter";

import { AlertService } from 'app/shared/alert/alert.service';
import { AlertComponent } from './alert.component';

@Component({
    selector: 'ct-alerts',
    template: '<div #alerts></div>',
    directives: [AlertComponent]
})

export class AlertsComponent implements OnInit {

    constructor(
        private _alert: AlertService,
        private _loader: DynamicComponentLoader,
        private _element: ElementRef) { }

    ngOnInit() {
        this._alert.onMessage((text) => this._createWindow(text));
    }

    private _createWindow(text: string) {
        this._loader.loadIntoLocation(AlertComponent, this._element, 'alerts')
            .then((res) => {
                res.instance.text = text;
                res.instance.close = () => res.dispose();
            });
    }
}