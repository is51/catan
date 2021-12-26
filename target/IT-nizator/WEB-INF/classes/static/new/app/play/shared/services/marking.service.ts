import { Injectable } from 'angular2/core';
import { Player } from 'app/shared/domain/player/player';

interface Marking {
    ids: number[];
    player: Player;
}

@Injectable()
export class MarkingService {
    private _markings: Map<string, Marking> = new Map<string, Marking>();
    private _onUpdate: Map<string, Function> = new Map<string, Function>();

    mark(type: string, ids: number[], player?: number) {
        setTimeout(() => {
            this._markings.set(type, <Marking>{ids, player});
            this.triggerUpdate(type);
        });
    }

    clear(type: string) {
        this._markings.delete(type);
        this.triggerUpdate(type);
    }

    get(type: string) {
        return this._markings.get(type);
    }

    //TODO: try to replace with Subscribable (it's used in game-page.component)
    onUpdate(type: string, onUpdate: Function) {
        this._onUpdate.set(type, onUpdate);
    }
    cancelOnUpdate(type: string) {
        this._onUpdate.delete(type);
    }
    triggerUpdate(type: string) {
        let onUpdate = this._onUpdate.get(type);
        if (onUpdate) {
            onUpdate();
        }
    }
}