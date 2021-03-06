import { Component, OnInit, OnDestroy } from 'angular2/core';
import { RouteParams, Router } from 'angular2/router';

import { NotificationService } from 'app/shared/services/notification/notification.service';
import { GameService } from 'app/shared/services/game/game.service';
import { AlertService } from 'app/shared/alert/alert.service';

import { Game } from 'app/shared/domain/game';

import { PlayComponent } from 'app/play/play.component';
import { GameRoomComponent } from './game-room/game-room.component';

const GAME_UPDATE_DELAY = 4000;

@Component({
    templateUrl: 'app/menu/game-page/game-page.component.html',
    directives: [
        PlayComponent,
        GameRoomComponent
    ]
})

export class GamePageComponent implements OnInit, OnDestroy {
    private _gameId: number;
    public game: Game = null;

    constructor(
        private _gameService: GameService,
        private _routeParams: RouteParams,
        private _router: Router,
        private _notification: NotificationService,
        private _alert: AlertService) {

        this._gameId = +this._routeParams.get('gameId');
    }

    ngOnInit() {
        this._loadGameAndStartRefreshing();
        this._notification.requestPermission();
    }

    private _loadGameAndStartRefreshing() {
        this._gameService.findById(this._gameId)
            .then(game => {
                this.game = game;

                this._subscribeOnGameStarting();

                this._gameService.startRefreshing(this.game, GAME_UPDATE_DELAY, null, () => {
                    this._alert.message('Getting Game Details Error. Probably there is a connection problem');
                    return false;
                });

            }, () => {
                this._alert.message('Getting Game Details Error')
                    .then(() => this._router.navigate(['StartPage']));
            });
    }

    private _subscribeOnGameStarting() {
        this.game.onStartPlaying(() => {
            //TODO: revise this temp notification (probably it will be done using log)
            this._notification.notifyGlobal('Game is started!', 'GAME_IS_STARTED');
        });
    }

    ngOnDestroy() {
        this._gameService.stopRefreshing();
    }
}