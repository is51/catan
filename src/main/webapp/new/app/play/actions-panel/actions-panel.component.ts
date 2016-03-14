import { Component } from 'angular2/core';

import { PlayService } from 'app/play/shared/services/play.service';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { GameService } from 'app/shared/services/game/game.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

import { Game } from 'app/shared/domain/game';

@Component({
    selector: 'ct-actions-panel',
    templateUrl: 'app/play/actions-panel/actions-panel.component.html',
    styleUrls: ['app/play/actions-panel/actions-panel.component.css'],
    inputs: ['game']
})

export class ActionsPanelComponent {
    game: Game;

    constructor(
        private _authUser: AuthUserService,
        private _play: PlayService,
        private _gameService: GameService,
        private _modalWindow: ModalWindowService) { }

    isActionEnabled(actionCode: string) {
        return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
    }

    isActionGroupEnabled(groupCode: string) {
        return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabledGroup(groupCode);
    }

    endTurn() {
        this._play.endTurn(this.game)
            .then(() => {
                this._gameService.refresh(this.game);
            }, data => {
                alert('End turn error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
            });
    }

    throwDice() {
        this._play.throwDice(this.game)
            .then(() => {
                this._gameService.refresh(this.game);
            }, data => {
                alert('Throw Dice error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
            });
    }

    /*moveRobber() {
        PlayService.moveRobber(this.game).then(function() {
            GameService.refresh(this.game);
        }, function(reason) {
            if (reason !== "CANCELED") {
                alert("Move robber error!");
            }
        });
    }

    choosePlayerToRob() {
        PlayService.choosePlayerToRob(this.game).then(function() {
            GameService.refresh(this.game);
        }, function(reason) {
            if (reason !== "CANCELED") {
                alert("Choose Player To Rob error!");
            }
        });
    }

    kickOffResources() {
        PlayService.kickOffResources(this.game).then(function() {
            GameService.refresh(this.game);
        }, function(reason) {
            if (reason !== "CANCELED") {
                alert("Kick Off Resources error!");
            }
        });
    }*/

    build() {
        this._modalWindow.show("BUY_PANEL");
    }

    showCards() {
        this._modalWindow.show("CARDS_PANEL");
    }

    /*showTradePanel() {
        ModalWindowService.show("TRADE_PANEL");
    }

    showTradeReplyPanel() {
        ModalWindowService.show("TRADE_REPLY_PANEL");
    }*/
}