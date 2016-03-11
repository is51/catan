import { Injectable } from 'angular2/core';

import { RemoteService } from 'app/shared/services/remote/remote.service';
import { Game } from 'app/shared/domain/game';

@Injectable()
export class PlayService {
    constructor (private _remote: RemoteService) { }

    endTurn(game: Game) {
        return this._remote.request('play.endTurn', {gameId: game.getId()});
    }

    throwDice(game: Game) {
        return this._remote.request('play.throwDice', {gameId: game.getId()});
    }

    buyCard(game: Game) {
        return this._remote.request('play.buyCard', {gameId: game.getId()});
    }
}