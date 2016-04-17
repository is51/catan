import { Component, OnInit } from 'angular2/core';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';
import { ExecuteActionsService } from 'app/play/shared/services/execute-actions.service';

import { Game } from 'app/shared/domain/game';

import { ModalWindowDirective } from 'app/shared/modal-window/modal-window.directive';
import { ModalWindowCloseDirective } from 'app/shared/modal-window/modal-window-close.directive';

//TODO: use global config for colors
const GRADIENT_COUNT_COLORS = {
    1: ['#d26953', '#e19583'],
    2: ['#5c98d5', '#81b0e3'],
    3: ['#d5c65d', '#e9de88'],
    4: ['#5dd582', '#85e6a4']
};

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

export class CardsPanelComponent implements OnInit {
    game: Game;
    gradientCountColor1: string;
    gradientCountColor2: string;

    constructor(
        private _authUser: AuthUserService,
        private _modalWindow: ModalWindowService,
        private _actions: ExecuteActionsService) { }

    ngOnInit() {
        let colorId = this.game.getCurrentPlayer(this._authUser.get()).colorId;
        this.gradientCountColor1 = GRADIENT_COUNT_COLORS[colorId][0];
        this.gradientCountColor2 = GRADIENT_COUNT_COLORS[colorId][1];
    }

    isActionEnabled(actionCode: string) {
        return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
    }

    cards() {
        return this.game.getCurrentPlayer(this._authUser.get()).developmentCards;;
    }

    useCardYearOfPlenty() {
        if (this.isActionEnabled('USE_CARD_YEAR_OF_PLENTY')) {
            this._modalWindow.hide("CARDS_PANEL");
            this._actions.execute('USE_CARD_YEAR_OF_PLENTY', this.game);
        }
    }

    useCardMonopoly() {
        if (this.isActionEnabled('USE_CARD_MONOPOLY')) {
            this._modalWindow.hide("CARDS_PANEL");
            this._actions.execute('USE_CARD_MONOPOLY', this.game);
        }
    }

    useCardRoadBuilding() {
        if (this.isActionEnabled('USE_CARD_ROAD_BUILDING')) {
            this._modalWindow.hide("CARDS_PANEL");
            this._actions.execute('USE_CARD_ROAD_BUILDING', this.game);
        }
    }

    useCardKnight() {
        if (this.isActionEnabled('USE_CARD_KNIGHT')) {
            this._modalWindow.hide("CARDS_PANEL");
            this._actions.execute('USE_CARD_KNIGHT', this.game);
        }
    }

}