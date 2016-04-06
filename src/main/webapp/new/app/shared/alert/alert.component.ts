import { Component, OnInit } from 'angular2/core';

import { AlertService } from 'app/shared/alert/alert.service';

@Component({
    selector: 'ct-alert',
    template: ''
})

export class AlertComponent implements OnInit {

    constructor(private _alert: AlertService) { }

    ngOnInit() {
        this._alert.onMessage((text) => this._createWindow(text));
    }

    private _createWindow(text: string) {
        alert(text);
    }
}