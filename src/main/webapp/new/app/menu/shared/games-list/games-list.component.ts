import { Component, OnInit } from 'angular2/core';
import { RouterLink } from 'angular2/router';

import { GameService } from 'app/shared/services/game/game.service';
import { Game } from 'app/shared/domain/game';

@Component({
    selector: 'ct-games-list',
    templateUrl: 'app/menu/shared/games-list/games-list.component.html',
    styleUrls: ['app/menu/shared/games-list/games-list.component.css'],
    directives: [RouterLink],
    inputs: ['typeOfGames']
})

export class GamesListComponent implements OnInit {
    typeOfGames: string;
    items: Game[] = null;

    constructor(private _gameService: GameService) { }

    ngOnInit() {
        this.update();
    }

    update() {
        this._gameService.findAllByType(this.typeOfGames)
            .then(items => {
                this.items = items;
            }, error => {
                alert('Getting Games List Error: ' + ((error && error.errorCode) ? error.errorCode : 'unknown'));
            });
    }
}