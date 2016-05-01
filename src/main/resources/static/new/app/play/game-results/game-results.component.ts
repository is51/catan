import { Component } from 'angular2/core';
import { RouterLink } from 'angular2/router';

import { Game } from 'app/shared/domain/game';
import { Player } from 'app/shared/domain/player/player';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';
import { ModalWindowDirective } from 'app/shared/modal-window/modal-window.directive';
import { ModalWindowCloseDirective } from 'app/shared/modal-window/modal-window-close.directive';

@Component({
    selector: 'ct-game-results',
    templateUrl: 'app/play/game-results/game-results.component.html',
    styleUrls: [
        'app/play/game-results/game-results.component.css',
        'app/shared/modal-window/modal-window.directive.css'
    ],
    directives: [
        RouterLink,
        ModalWindowDirective,
        ModalWindowCloseDirective
    ],
    inputs: ['game']
})

export class GameResultsComponent {
    game: Game;

    playersSorted: Player[];
    winnerName: string;

    constructor(private _modalWindow: ModalWindowService) {}

    ngOnInit() {
        this.playersSorted = this._getPlayersSortedByVictoryPoints();
        this.winnerName = this.playersSorted[0].user.getDisplayedName();
        this._modalWindow.show('GAME_RESULTS');
    }

    private _getPlayersSortedByVictoryPoints() {
        return this.game.players
            .slice()
            .sort((a, b) => b.achievements.realVictoryPoints - a.achievements.realVictoryPoints);
    }
}