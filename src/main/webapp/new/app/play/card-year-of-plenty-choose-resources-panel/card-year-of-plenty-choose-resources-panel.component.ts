import { Component } from 'angular2/core';

import { PlayService } from 'app/play/shared/services/play.service';
import { GameService } from 'app/shared/services/game/game.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

import { Game } from 'app/shared/domain/game';

import { ModalWindowDirective } from 'app/shared/modal-window/modal-window.directive';
import { ModalWindowCloseDirective } from 'app/shared/modal-window/modal-window-close.directive';
import { ChooseResourcesComponent } from 'app/play/shared/choose-resources/choose-resources.component';

const PANEL_ID = 'CARD_YEAR_OF_PLENTY';

@Component({
    selector: 'ct-card-year-of-plenty-choose-resources-panel',
    templateUrl: 'app/play/card-year-of-plenty-choose-resources-panel/card-year-of-plenty-choose-resources-panel.component.html',
    styleUrls: [
        'app/play/card-year-of-plenty-choose-resources-panel/card-year-of-plenty-choose-resources-panel.component.css',
        'app/shared/modal-window/modal-window.directive.css'
    ],
    directives: [
        ModalWindowDirective,
        ModalWindowCloseDirective,
        ChooseResourcesComponent
    ],
    inputs: ['game']
})

export class CardYearOfPlentyChooseResourcesPanelComponent {
    game: Game;
    modalWindowId: string = PANEL_ID;

    constructor(private _modalWindow: ModalWindowService) { }

    isModalWindowVisible() {
        return this._modalWindow.isVisible(this.modalWindowId);
    }
}