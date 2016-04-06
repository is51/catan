import { Directive } from 'angular2/core';
import { Router } from 'angular2/router';

import { RemoteService } from 'app/shared/services/remote/remote.service';
import { AlertService } from 'app/shared/services/alert/alert.service';

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
        private _router: Router,
        private _alert: AlertService) { }

    onClick() {
        this._remote.request('game.cancel', {gameId: this.game.getId()})
            .then(() => this._router.navigate(['StartPage']))
            .catch(data => this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}