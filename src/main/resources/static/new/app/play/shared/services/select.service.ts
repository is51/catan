import { Injectable } from 'angular2/core';

interface PromiseCallbacks {
    resolve: Function;
    reject: Function;
}

@Injectable()
export class SelectService {
    private _requests: Map<string, PromiseCallbacks> = new Map<string, PromiseCallbacks>();

    requestSelection(type: string) {
        this.cancelRequestSelection(type);
        return new Promise((resolve, reject) => {
            this._requests.set(type, <PromiseCallbacks>{resolve, reject});
        });
    }

    select(type: string, objectId: number) {
        if (this._requests.has(type)) {
            this._requests.get(type).resolve(objectId);
        }
    }

    cancelRequestSelection(type: string) {
        if (this._requests.has(type)) {
            this._requests.get(type).reject("CANCELED");
            this._requests.delete(type);
        }
    }

    cancelAllRequestSelections() {
        this._requests.forEach((callbacks, type) => this.cancelRequestSelection(type));
    }
}