import { Component, OnInit } from 'angular2/core';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

import { Game } from 'app/shared/domain/game';
import { Player } from 'app/shared/domain/player/player';

const AVATARS_PATH = '/new/resources/avatars/'; // Depends on #rootpath
const AVATARS_COUNT = 4;

@Component({
    selector: 'ct-players-panel',
    templateUrl: 'app/play/players-panel/players-panel.component.html',
    styleUrls: ['app/play/players-panel/players-panel.component.css'],
    inputs: ['game']
})

export class PlayersPanelComponent implements OnInit {
    game: Game;
    players: Player[];

    displayCompact: boolean = false;

    PLAYER_BLOCK_HEIGHT: number = 111;
    ACTIVE_PLAYER_BLOCK_SCALE: number = 1.12;

    constructor(private _authUser: AuthUserService) { }

    ngOnInit() {
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

    isActivePrevious(index: number) {
        return index > 0 && this.isActive(this.players[index-1]);
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

    isResourcesCountCritical(player: Player) {
        return player.achievements.totalResources > 7; //TODO: put to config? "7" is used in kick-resources panel as well
    }

    getPlayerBlockY(index: number) {

        let isActivePlayerBefore = this.players
            .slice(0, index)
            .some((player, pIndex) => this.isActive(player));

        return index * this.PLAYER_BLOCK_HEIGHT
            + ((isActivePlayerBefore) ? (this.ACTIVE_PLAYER_BLOCK_SCALE - 1) * this.PLAYER_BLOCK_HEIGHT : 0);
    }

    getAvatarUrl(player: Player) {
        let avatarId = player.user.id % AVATARS_COUNT + 1;
        return AVATARS_PATH + 'a' + avatarId + '.svg';
    }

    //TODO: use global config for colors
    getColor(player: Player) {
        let colors = {
            1: '#ab4242',
            2: '#3e77ae',
            3: '#B58B3C',
            4: '#42ab73'
        };
        return colors[player.colorId];
    }

    toggleDisplayCompact() {
        this.displayCompact = !this.displayCompact;
    }
}