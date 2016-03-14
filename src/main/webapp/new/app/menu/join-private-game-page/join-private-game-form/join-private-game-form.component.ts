import { Component } from 'angular2/core';
import { Router } from 'angular2/router';

import { AuthService } from 'app/shared/services/auth/auth.service';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { RemoteService } from 'app/shared/services/remote/remote.service';

@Component({
    selector: 'ct-join-private-game-form',
    templateUrl: 'app/menu/join-private-game-page/join-private-game-form/join-private-game-form.component.html'
})

export class JoinPrivateGameFormComponent {
    privateCode: string = "";

    constructor(
        private _remote: RemoteService,
        private _authUser: AuthUserService,
        private _router: Router) { }

    submit() {
        if (this._authUser.isAuthorized()) {
            this._joinPrivateGame();
        }

        if (this._authUser.isNotAuthorized()) {
            /*$state.go('registerGuest', {
                onRegister: function() {
                    goBack();
                    joinPrivateGame();
                },
                onBack: goBack
            });*/
            this._router.navigate(['RegisterGuestPage']);
        }

        return false;
        // TODO: avoid this? remove form and replace input-submit
    }

    private _joinPrivateGame() {
        this._remote.request('game.joinPrivate', {'privateCode': this.privateCode})
            .then(data => this._router.navigate(['GamePage', {gameId: data.gameId}]))
            .catch(data => alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }

    /*function goBack() {
        $state.go('joinPrivateGame', {data: scope.data});
    }*/
}