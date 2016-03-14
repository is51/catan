import { Directive } from 'angular2/core';

import { RemoteService } from 'app/shared/services/remote/remote.service';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { GameService } from 'app/shared/services/game/game.service';

import { Game } from 'app/shared/domain/game';

@Directive({
    selector: '[ct-ready-button]',
    host: {
        '(click)': 'onClick($event)',
        '[class.btn-success]': 'isCurrentPlayerReady()'
    },
    inputs: ['game']
})

export class ReadyButtonDirective {
    game: Game;

    constructor(
        private _remote: RemoteService,
        private _authUser: AuthUserService,
        private _gameService: GameService) { }

    onClick() {
        let requestName = (this.isCurrentPlayerReady()) ? "game.notReady" : "game.ready";
        this._remote.request(requestName, {gameId: this.game.getId()})
            .then(() => this._gameService.refresh(this.game))
            .catch(data => alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }

    isCurrentPlayerReady() {
        return this.game.getCurrentPlayer(this._authUser.get()).ready;
    }
}