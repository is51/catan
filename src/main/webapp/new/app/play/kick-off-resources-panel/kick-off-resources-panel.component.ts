import { Component } from 'angular2/core';

import { PlayService } from 'app/play/shared/services/play.service';
import { GameService } from 'app/shared/services/game/game.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

import { Game } from 'app/shared/domain/game';

import { ModalWindowDirective } from 'app/shared/modal-window/modal-window.directive';
import { ModalWindowCloseDirective } from 'app/shared/modal-window/modal-window-close.directive';
import { ChooseResourcesComponent } from 'app/play/shared/choose-resources/choose-resources.component';

const PANEL_ID = 'KICK_OFF_RESOURCES';

@Component({
    selector: 'ct-kick-off-resources-panel',
    templateUrl: 'app/play/kick-off-resources-panel/kick-off-resources-panel.component.html',
    styleUrls: [
        'app/play/kick-off-resources-panel/kick-off-resources-panel.component.css',
        'app/shared/modal-window/modal-window.directive.css'
    ],
    directives: [
        ModalWindowDirective,
        ModalWindowCloseDirective,
        ChooseResourcesComponent
    ],
    inputs: ['game']
})

export class KickOffResourcesPanelComponent {
    game: Game;
    modalWindowId: string = PANEL_ID;

    constructor(private _modalWindow: ModalWindowService) { }

    isModalWindowVisible() {
        return this._modalWindow.isVisible(this.modalWindowId);
    }
}