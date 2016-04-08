import { Component, OnInit } from 'angular2/core';
import { RouterLink } from 'angular2/router';

import { GameService } from 'app/shared/services/game/game.service';
import { AlertService } from 'app/shared/alert/alert.service';

import { Game } from 'app/shared/domain/game';

import { PlayersListComponent } from 'app/menu/shared/players-list/players-list.component';
import { JoinPublicGameButtonDirective } from 'app/menu/shared/join-public-game-button/join-public-game-button.directive';

@Component({
    selector: 'ct-games-list',
    templateUrl: 'app/menu/shared/games-list/games-list.component.html',
    styleUrls: ['app/menu/shared/games-list/games-list.component.css'],
    directives: [
        RouterLink,
        PlayersListComponent,
        JoinPublicGameButtonDirective
    ],
    inputs: ['typeOfGames']
})

export class GamesListComponent implements OnInit {
    typeOfGames: string;
    games: Game[] = null;

    constructor(
        private _gameService: GameService,
        private _alert: AlertService) { }

    ngOnInit() {
        this.update();
    }

    update() {
        this._gameService.findAllByType(this.typeOfGames)
            .then(games => this.games = games)
            .catch(data => this._alert.message('Getting Games List Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}