import { Component } from 'angular2/core';
import { RouterLink } from 'angular2/router';

import { Game } from 'app/shared/domain/game';
import { Player } from 'app/shared/domain/player/player';

@Component({
    selector: 'ct-game-results',
    templateUrl: 'app/menu/game-page/game-results/game-results.component.html',
    styleUrls: ['app/menu/game-page/game-results/game-results.component.css'],
    directives: [RouterLink],
    inputs: ['game']
})

export class GameResultsComponent {
    game: Game;

    playersSorted: Player[];
    winnerName: string;

    ngOnInit() {
        this.playersSorted = this._getPlayersSortedByVictoryPoints();
        this.winnerName = this.playersSorted[0].user.username;
    }

    private _getPlayersSortedByVictoryPoints() {
        return this.game.players
            .map(player => player) //TODO: (it's just one level copy) Use native method
            .sort((a, b) => b.achievements.realVictoryPoints - a.achievements.realVictoryPoints);
    }
}