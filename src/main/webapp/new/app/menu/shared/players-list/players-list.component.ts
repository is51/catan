import { Component, OnChanges } from 'angular2/core';
import { Game } from 'app/shared/domain/game';

@Component({
    selector: 'ct-players-list',
    templateUrl: 'app/menu/shared/players-list/players-list.component.html',
    styleUrls: ['app/menu/shared/players-list/players-list.component.css'],
    inputs: ['game', 'showReadyStatus']
})

export class PlayersListComponent implements OnChanges {
    game: Game;
    showReadyStatus: string;

    vacantPlaces: any[];

    ngOnChanges() {
        this._calculateVacantPlaces();
    }

    private _calculateVacantPlaces() {
        let vacantPlacesCount = this.game.maxPlayers - this.game.players.length;
        this.vacantPlaces = new Array(vacantPlacesCount);
    }
}