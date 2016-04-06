import { Injectable } from 'angular2/core';

@Injectable()
export class AlertService {
    private _onMessage: Function;

    message(text: string) {
        if (this._onMessage) {
            this._onMessage(text);
        }
    }

    onMessage(onMessage: Function) {
        this._onMessage = onMessage;
    }
}