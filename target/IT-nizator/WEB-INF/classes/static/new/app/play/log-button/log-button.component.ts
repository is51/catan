import { Component } from 'angular2/core';
import { Game } from 'app/shared/domain/game';

import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

@Component({
    selector: 'ct-log-button',
    templateUrl: 'app/play/log-button/log-button.component.html',
    styleUrls: ['app/play/log-button/log-button.component.css'],
})

export class LogButtonComponent {

    constructor(private _modalWindow: ModalWindowService) { }

    showLog() {
        this._modalWindow.show('LOG_PANEL');
    }

}