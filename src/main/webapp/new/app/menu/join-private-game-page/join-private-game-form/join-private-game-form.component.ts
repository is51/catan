import { Component, OnInit } from 'angular2/core';
import { Router } from 'angular2/router';

import { AuthService } from 'app/shared/services/auth/auth.service';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { RemoteService } from 'app/shared/services/remote/remote.service';
import { RouteDataService } from 'app/shared/services/route-data/route-data.service';

@Component({
    selector: 'ct-join-private-game-form',
    templateUrl: 'app/menu/join-private-game-page/join-private-game-form/join-private-game-form.component.html',
    inputs: ['data']
})

export class JoinPrivateGameFormComponent {
    data: JoinPrivateGameFormData;

    constructor(
        private _remote: RemoteService,
        private _authUser: AuthUserService,
        private _router: Router,
        private _routeData: RouteDataService) { }

    ngOnInit() {
        if (!this.data) {
            this._setDefaultData();
        }
    }

    private _setDefaultData() {
        this.data = <JoinPrivateGameFormData>{
            privateCode: ''
        };
    }

    submit() {
        if (this._authUser.isAuthorized()) {
            this._joinPrivateGame();
        }

        if (this._authUser.isNotAuthorized()) {
            this._routeData.put({
                onRegister: () => {
                    this._goJoinPrivateGamePage();
                    this._joinPrivateGame();
                },
                onBack: () => this._goJoinPrivateGamePage()
            });
            this._router.navigate(['RegisterGuestPage']);
        }
    }

    private _joinPrivateGame() {
        this._remote.request('game.joinPrivate', {'privateCode': this.data.privateCode})
            .then(data => this._router.navigate(['GamePage', {gameId: data.gameId}]))
            .catch(data => alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }

    private _goJoinPrivateGamePage() {
        this._routeData.put({formData: this.data});
        this._router.navigate(['JoinPrivateGamePage']);
    }
}

export interface JoinPrivateGameFormData {
    privateCode: string;
}