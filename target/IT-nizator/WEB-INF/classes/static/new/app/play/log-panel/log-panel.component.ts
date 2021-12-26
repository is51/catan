import { Component, DoCheck } from 'angular2/core';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

import { Game } from 'app/shared/domain/game';
import { ModalWindowDirective } from 'app/shared/modal-window/modal-window.directive';
import { ModalWindowCloseDirective } from 'app/shared/modal-window/modal-window-close.directive';

@Component({
    selector: 'ct-log-panel',
    templateUrl: 'app/play/log-panel/log-panel.component.html',
    styleUrls: [
        'app/play/log-panel/log-panel.component.css',
        'app/shared/modal-window/modal-window.directive.css'
    ],
    directives: [
        ModalWindowDirective,
        ModalWindowCloseDirective,
    ],
    inputs: ['game']
})

export class LogPanelComponent implements DoCheck {
    game: Game;
    log: any[];
    count: number;

    onShow: Function = () => {
        this._updateLog();
    };

    constructor(private _authUser: AuthUserService) { }

    //TODO: 'ngDoCheck' = bad performance (use subscribe)
    ngDoCheck() {
        this._updateLog();
    }

    private _updateLog() {
        this.log = Object.assign([], this.game.getCurrentPlayer(this._authUser.get()).log);
        this.log.reverse();
        this.count = this.log.length;
    }

}