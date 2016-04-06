import { Component } from 'angular2/core';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';
import { ExecuteActionsService } from 'app/play/shared/services/execute-actions.service';

import { Game } from 'app/shared/domain/game';

import { ModalWindowDirective } from 'app/shared/modal-window/modal-window.directive';
import { ModalWindowCloseDirective } from 'app/shared/modal-window/modal-window-close.directive';

@Component({
    selector: 'ct-cards-panel',
    templateUrl: 'app/play/cards-panel/cards-panel.component.html',
    styleUrls: [
        'app/play/cards-panel/cards-panel.component.css',
        'app/shared/modal-window/modal-window.directive.css'
    ],
    directives: [
        ModalWindowDirective,
        ModalWindowCloseDirective,
    ],
    inputs: ['game']
})

export class CardsPanelComponent {
    game: Game;

    constructor(
        private _authUser: AuthUserService,
        private _modalWindow: ModalWindowService,
        private _actions: ExecuteActionsService) { }

    isActionEnabled(actionCode: string) {
        return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
    }

    cards() {
        return this.game.getCurrentPlayer(this._authUser.get()).developmentCards;;
    }

    useCardYearOfPlenty() {
        this._modalWindow.hide("CARDS_PANEL");
        this._actions.execute('USE_CARD_YEAR_OF_PLENTY', this.game);
    }

    useCardMonopoly() {
        this._modalWindow.hide("CARDS_PANEL");
        this._actions.execute('USE_CARD_MONOPOLY', this.game);
    }

    useCardRoadBuilding() {
        this._modalWindow.hide("CARDS_PANEL");
        this._actions.execute('USE_CARD_ROAD_BUILDING', this.game);
    }

    useCardKnight() {
        this._modalWindow.hide("CARDS_PANEL");
        this._actions.execute('USE_CARD_KNIGHT', this.game);
    }

}