import { Component, OnInit } from 'angular2/core';
import { Router } from 'angular2/router';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { RemoteService } from 'app/shared/services/remote/remote.service';
import { RouteDataService } from 'app/shared/services/route-data/route-data.service';
import { AlertService } from 'app/shared/alert/alert.service';

@Component({
    selector: 'ct-create-game-form',
    templateUrl: 'app/menu/create-game-page/create-game-form/create-game-form.component.html',
    inputs: ['data']
})

export class CreateGameFormComponent implements OnInit {
    data: CreateGameFormData;

    initialBuildingsSetIdValues = [ //TODO: probably should be gotten from back-end
        {value: 1, name: "2 settlements + 2 roads"},
        {value: 2, name: "1 city + 2 settlements + 3 roads"}
    ];

    constructor(
        private _authUser: AuthUserService,
        private _remote: RemoteService,
        private _router: Router,
        private _routeData: RouteDataService,
        private _alert: AlertService) { }

    ngOnInit() {
        if (!this.data) {
            this._setDefaultData();
        }
    }

    private _setDefaultData() {
        this.data = <CreateGameFormData>{
            privateGame: true,
            targetVictoryPoints: 12,
            initialBuildingsSetId: 1,
        };
    }

    submit() {
        if (this._authUser.isAuthorized()) {
            if (!this.data.privateGame && this._authUser.isTypeGuest()) {
                this._alert.message("Guest can't create public game. You should register. Registration from guest to regular user is NOT IMPLEMENTED");
            } else {
                this._createGame();
            }
        }

        if (this._authUser.isNotAuthorized()) {
            if (this.data.privateGame) {
                this._routeData.put({
                    onRegister: () => {
                        this._goCreateGamePage();
                        this._createGame();
                    },
                    onBack: () => this._goCreateGamePage()
                });
                this._router.navigate(['RegisterGuestPage']);
            } else {
                this._routeData.put({
                    onLogin: () => {
                        this._goCreateGamePage();
                        this._createGame();
                    },
                    onBack: () => this._goCreateGamePage()
                });
                this._router.navigate(['LoginPage']);
            }
        }
    }


    private _goCreateGamePage() {
        this._routeData.put({formData: this.data});
        this._router.navigate(['CreateGamePage']);
    }

    private _createGame() {
        this._remote.request('game.create', {
            privateGame: this.data.privateGame,
            targetVictoryPoints: this.data.targetVictoryPoints,
            initialBuildingsSetId: this.data.initialBuildingsSetId
        })
            .then(data => this._router.navigate(['GamePage', {gameId: data.gameId}]))
            .catch(data => this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}

export interface CreateGameFormData {
    privateGame: boolean;
    targetVictoryPoints: number;
    initialBuildingsSetId: number;
}