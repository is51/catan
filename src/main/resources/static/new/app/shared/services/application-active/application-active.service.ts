import { Injectable } from 'angular2/core';

//TODO: window should be injected

@Injectable()
export class ApplicationActiveService {
    private _isActive: boolean = false;

    constructor () {
        window.onfocus = () => this._isActive = true;
        window.onblur = () => this._isActive = false;
    }

    isActive() {
        return this._isActive;
    }
}