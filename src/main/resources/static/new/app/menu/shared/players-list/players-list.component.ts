import { Component, DoCheck } from 'angular2/core';
import { Game } from 'app/shared/domain/game';

@Component({
    selector: 'ct-players-list',
    templateUrl: 'app/menu/shared/players-list/players-list.component.html',
    styleUrls: ['app/menu/shared/players-list/players-list.component.css'],
    inputs: ['game', 'showReadyStatus']
})

export class PlayersListComponent implements DoCheck {
    game: Game;
    showReadyStatus: string;

    vacantPlaces: any[];

    ngDoCheck() {
        //TODO: not optimal (needs to be updated only on changes)
        this._calculateVacantPlaces();
    }

    private _calculateVacantPlaces() {
        let vacantPlacesCount = this.game.maxPlayers - this.game.players.length;
        this.vacantPlaces = new Array(vacantPlacesCount);
    }
}