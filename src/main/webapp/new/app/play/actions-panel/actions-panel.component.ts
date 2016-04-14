import { Component } from 'angular2/core';

import { PlayService } from 'app/play/shared/services/play.service';
import { ExecuteActionsService } from 'app/play/shared/services/execute-actions.service';
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
        private _modalWindow: ModalWindowService,
        private _actions: ExecuteActionsService) { }

    isActionEnabled(actionCode: string) {
        return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
    }

    isActionGroupEnabled(groupCode: string) {
        return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabledGroup(groupCode);
    }

    isTradeReplyPanelVisible() {
        return this._modalWindow.isVisible('TRADE_REPLY_PANEL');
    }

    isExecuting(actionCode: string) {
        return this._actions.isExecuting(actionCode);
    }

    endTurn() {
        this._actions.execute('END_TURN', this.game);
    }

    throwDice() {
        this._actions.execute('THROW_DICE', this.game);
    }

    kickOffResources() {
        this._actions.execute('KICK_OFF_RESOURCES', this.game);
    }

    build() {
        this._modalWindow.show("BUY_PANEL");
    }

    showCards() {
        this._modalWindow.show("CARDS_PANEL");
    }

    showTradePanel() {
        this._modalWindow.show("TRADE_PANEL");
    }

    showTradeReplyPanel() {
        this._actions.execute('TRADE_REPLY');
    }
}