import { Component } from 'angular2/core';

import { PlayService } from 'app/play/shared/services/play.service';
import { GameService } from 'app/shared/services/game/game.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

import { Game } from 'app/shared/domain/game';

import { ModalWindowDirective } from 'app/shared/modal-window/modal-window.directive';
import { ModalWindowCloseDirective } from 'app/shared/modal-window/modal-window-close.directive';
import { ChooseResourcesComponent } from 'app/play/shared/choose-resources/choose-resources.component';

const PANEL_ID = 'CARD_MONOPOLY';

@Component({
    selector: 'ct-card-monopoly-choose-resource-panel',
    templateUrl: 'app/play/card-monopoly-choose-resource-panel/card-monopoly-choose-resource-panel.component.html',
    styleUrls: [
        'app/play/card-monopoly-choose-resource-panel/card-monopoly-choose-resource-panel.component.css',
        'app/shared/modal-window/modal-window.directive.css'
    ],
    directives: [
        ModalWindowDirective,
        ModalWindowCloseDirective,
        ChooseResourcesComponent
    ],
    inputs: ['game']
})

export class CardMonopolyChooseResourcePanelComponent {
    game: Game;
    modalWindowId: string = PANEL_ID;

    constructor(private _modalWindow: ModalWindowService) { }

    isModalWindowVisible() {
        return this._modalWindow.isVisible(this.modalWindowId);
    }
}