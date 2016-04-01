import { Component } from 'angular2/core';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';
import { PlayService } from 'app/play/shared/services/play.service';
import { ActionService } from 'app/play/shared/services/action.service';
import { GameService } from 'app/shared/services/game/game.service';

import { Game } from 'app/shared/domain/game';
import { ModalWindowDirective } from 'app/shared/modal-window/modal-window.directive';
import { ModalWindowCloseDirective } from 'app/shared/modal-window/modal-window-close.directive';

@Component({
    selector: 'ct-buy-panel',
    templateUrl: 'app/play/buy-panel/buy-panel.component.html',
    styleUrls: [
        'app/play/buy-panel/buy-panel.component.css',
        'app/shared/modal-window/modal-window.directive.css'
    ],
    directives: [
        ModalWindowDirective,
        ModalWindowCloseDirective,
    ],
    inputs: ['game']
})

export class BuyPanelComponent {
    game: Game;

    constructor(
        private _authUser: AuthUserService,
        private _modalWindow: ModalWindowService,
        private _play: PlayService,
        private _gameService: GameService,
        private _action: ActionService) { }

    isActionEnabled(actionCode: string) {
        return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
    }

    buildSettlement() {
        this._modalWindow.hide("BUY_PANEL");
        this._action.run('BUILD_SETTLEMENT', this.game);
    }

    buildCity() {
        this._modalWindow.hide("BUY_PANEL");
        this._action.run('BUILD_CITY', this.game);
    }

    buildRoad() {
        this._modalWindow.hide("BUY_PANEL");
        this._action.run('BUILD_ROAD', this.game);
    }

    buyCard() {
        this._modalWindow.hide("BUY_PANEL");
        this._play.buyCard(this.game)
            .then(data => {
                alert("Bought card: " + data.card); //TODO: why red?
                this._gameService.refresh(this.game);
            })
            .catch(data => alert('Buy Card error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}