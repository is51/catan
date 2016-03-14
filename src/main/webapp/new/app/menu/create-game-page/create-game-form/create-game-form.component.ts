import { Component } from 'angular2/core';
import { Router } from 'angular2/router';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { RemoteService } from 'app/shared/services/remote/remote.service';

@Component({
    selector: 'ct-create-game-form',
    templateUrl: 'app/menu/create-game-page/create-game-form/create-game-form.component.html'
})

export class CreateGameFormComponent {
    privateGame: boolean = true;
    targetVictoryPoints: number = 12;
    initialBuildingsSetId: number = 1;

    initialBuildingsSetIdValues = [ //TODO: probably should be gotten from back-end
        {value: 1, name: "2 settlements + 2 roads"},
        {value: 2, name: "1 city + 2 settlements + 3 roads"}
    ];

    constructor(
        private _authUser: AuthUserService,
        private _remote: RemoteService,
        private _router: Router) { }

    submit() {
        if (this._authUser.isAuthorized()) {
            if (!this.privateGame && this._authUser.isTypeGuest()) {
                alert("Guest can't create public game. You should register. Registration from guest to regular user is NOT IMPLEMENTED");
            } else {
                this._createGame();
            }
        }

        if (this._authUser.isNotAuthorized()) {
            if (this.privateGame) {
                this._router.navigate(['RegisterGuestPage']);
                //TODO: try to do it using Promise
                /*$state.go('registerGuest', {
                    onRegister: goBackAndCreateGame,
                    onBack: goBack
                });*/
            } else {
                this._router.navigate(['LoginPage']);
                /*$state.go('login', {
                    onLogin: goBackAndCreateGame,
                    onBack: goBack
                });*/
            }
        }

        return false;
        // TODO: find a way to avoid "return false".
        // Remove <form> everywhere and replace <input type=submit> with <button> ???
    }

    /*
     function goBack() {
        $state.go('createGame', {data: scope.data});
     }

     function goBackAndCreateGame() {
        goBack();
        createGame();
     }
     */

    private _createGame() {
        this._remote.request('game.create', {
            privateGame: this.privateGame,
            targetVictoryPoints: this.targetVictoryPoints,
            initialBuildingsSetId: this.initialBuildingsSetId
        })
            .then(data => this._router.navigate(['GamePage', {gameId: data.gameId}]))
            .catch(data => alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}