import { Component, OnInit, OnDestroy } from 'angular2/core';
import { RouteParams, Router } from 'angular2/router';

import { GameService } from 'app/shared/services/game/game.service';
import { Game } from 'app/shared/domain/game';

import { PlayComponent } from 'app/play/play.component';
import { GameRoomComponent } from './game-room/game-room.component';
import { GameResultsComponent } from './game-results/game-results.component';

const GAME_UPDATE_DELAY = 5000;

@Component({
    templateUrl: 'app/menu/game-page/game-page.component.html',
    directives: [
        PlayComponent,
        GameRoomComponent,
        GameResultsComponent
    ]
})

export class GamePageComponent implements OnInit, OnDestroy {
    private _gameId: number;
    public game: Game = null;

    constructor(
        private _gameService: GameService,
        private _routeParams: RouteParams,
        private _router: Router) {

        this._gameId = +this._routeParams.get('gameId');
    }

    ngOnInit() {
        this._loadGameAndStartRefreshing();
    }

    private _loadGameAndStartRefreshing() {
        this._gameService.findById(this._gameId)
            .then(game => {
                this.game = game;

                this._gameService.startRefreshing(this.game, GAME_UPDATE_DELAY, null, () => {
                    alert('Getting Game Details Error. Probably there is a connection problem');
                    return false;
                });

            }, () => {
                alert('Getting Game Details Error');
                this._router.navigate(['StartPage']);
            });
    }

    ngOnDestroy() {
        this._gameService.stopRefreshing();
    }
}