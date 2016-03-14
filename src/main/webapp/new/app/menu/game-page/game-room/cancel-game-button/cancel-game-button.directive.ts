import { Directive } from 'angular2/core';
import { Router } from 'angular2/router';

import { RemoteService } from 'app/shared/services/remote/remote.service';
import { Game } from 'app/shared/domain/game';

@Directive({
    selector: '[ct-cancel-game-button]',
    host: {
        '(click)': 'onClick($event)',
    },
    inputs: ['game']
})

export class CancelGameButtonDirective {
    game: Game;

    constructor(
        private _remote: RemoteService,
        private _router: Router) { }

    onClick() {
        this._remote.request('game.cancel', {gameId: this.game.getId()})
            .then(() => this._router.navigate(['StartPage']))
            .catch(data => alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}