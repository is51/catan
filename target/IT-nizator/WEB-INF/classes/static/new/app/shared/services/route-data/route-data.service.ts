import { Injectable } from 'angular2/core';

@Injectable()
export class RouteDataService {
    constructor () { }

    private _preparedData: any = null;
    private _data: any = null;

    get(key: string) {
        if (this._data && this._data[key]) {
            return this._data[key];
        }
        return null;
    }

    fetch() {
        this._data = this._preparedData;
        this._preparedData = null;
        return this._data;
    }

    put(data: any) {
        this._preparedData = data;
    }

}