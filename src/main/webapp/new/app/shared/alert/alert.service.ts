import { Injectable } from 'angular2/core';

@Injectable()
export class AlertService {
    private _onMessage: Function = (text: string, resolve: Function) => {};

    message(text: string) {
        return new Promise((resolve) => {
            this._onMessage(text, resolve);
        });
    }

    onMessage(onMessage: Function) {
        this._onMessage = onMessage;
    }
}