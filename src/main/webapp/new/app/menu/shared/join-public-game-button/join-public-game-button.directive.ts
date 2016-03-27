import { Directive } from 'angular2/core';
import { Router } from 'angular2/router';

import { RemoteService } from 'app/shared/services/remote/remote.service';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

import { Game } from 'app/shared/domain/game';

@Directive({
    selector: '[ct-join-public-game-button]',
    host: {
        '(click)': 'onClick($event)',
    },
    inputs: ['game']
})

export class JoinPublicGameButtonDirective {
    game: Game;

    constructor(
        private _remote: RemoteService,
        private _authUser: AuthUserService,
        private _router: Router) { }

    onClick() {
        if (this._authUser.isAuthorized()) {
            this._joinPublicGame();
        }

        if (this._authUser.isNotAuthorized()) {
            /*$state.go('registerGuest', {
                onRegister: function() {
                    goBack();
                    joinPublicGame();
                },
                onBack: goBack
            });*/
            this._router.navigate(['RegisterGuestPage']);
        }
    }

    /*function goBack() {
        $state.go('joinPublicGame');
    }*/

    private _joinPublicGame() {
        this._remote.request('game.joinPublic', {gameId: this.game.getId()})
            .then(() => this._router.navigate(['GamePage', {gameId: this.game.getId()}]))
            .catch(data => alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}