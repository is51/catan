import { Component, OnChanges } from 'angular2/core';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

import { Game } from 'app/shared/domain/game';
import { Player } from 'app/shared/domain/player/player';

@Component({
    selector: 'ct-players-panel',
    templateUrl: 'app/play/players-panel/players-panel.component.html',
    styleUrls: ['app/play/players-panel/players-panel.component.css'],
    inputs: ['game']
})

export class PlayersPanelComponent implements OnChanges {
    game: Game;
    players: Player[];

    constructor(private _authUser: AuthUserService) { }

    ngOnChanges() {
        this._setPlayersSortedByMoveOrderCurrentUserFirst();
    }

    private _setPlayersSortedByMoveOrderCurrentUserFirst() {
        let currentPlayer = this.game.getCurrentPlayer(this._authUser.get());
        let players = this.game.players.slice();
        let playersCount = players.length;

        this.players = players.sort((a, b) => {
            let aMoveOrder = a.moveOrder + ((a !== currentPlayer && a.moveOrder < currentPlayer.moveOrder) ? playersCount : 0);
            let bMoveOrder = b.moveOrder + ((b !== currentPlayer && b.moveOrder < currentPlayer.moveOrder) ? playersCount : 0);

            return aMoveOrder - bMoveOrder;
        });
    }

    isActive(player: Player) {
        return this.game.currentMove === player.moveOrder;
    }

    isCurrentUser(player: Player) {
        return this._authUser.get().id === player.user.id;
    }

    isBiggestArmy(player: Player) {
        return this.game.biggestArmyOwnerId === player.id;
    }

    isLongestWay(player: Player) {
        return this.game.longestWayOwnerId === player.id;
    }
}