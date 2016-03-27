import { Injectable } from 'angular2/core';
import { Player } from 'app/shared/domain/player/player';

interface Marking {
    ids: number[];
    player: Player;
}

@Injectable()
export class MarkingService {
    private _markings: Map<string, Marking> = new Map<string, Marking>();

    mark(type: string, ids: number[], player?: number) {
        setTimeout(() => {
            this._markings.set(type, <Marking>{ids, player});
        });
    }

    clear(type: string) {
        this._markings.delete(type);
    }

    get(type: string) {
        return this._markings.get(type);
    }
}