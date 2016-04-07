import { Component } from 'angular2/core';

import { ExecuteActionsService } from 'app/play/shared/services/execute-actions.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

import { Game } from 'app/shared/domain/game';

import { ModalWindowDirective } from 'app/shared/modal-window/modal-window.directive';
import { ModalWindowCloseDirective } from 'app/shared/modal-window/modal-window-close.directive';
import { TradePlayersPanelComponent } from './trade-players-panel/trade-players-panel.component';
import { TradePortPanelComponent } from './trade-port-panel/trade-port-panel.component';
import { ChooseResourcesCancelDirective } from 'app/play/shared/choose-resources/choose-resources-cancel.directive';

const PANEL_ID = 'TRADE_PANEL';

@Component({
    selector: 'ct-trade-panel',
    templateUrl: 'app/play/trade-panel/trade-panel.component.html',
    styleUrls: [
        'app/play/trade-panel/trade-panel.component.css',
        'app/shared/modal-window/modal-window.directive.css'
    ],
    directives: [
        ModalWindowDirective,
        ModalWindowCloseDirective,
        TradePlayersPanelComponent,
        TradePortPanelComponent,
        ChooseResourcesCancelDirective
    ],
    inputs: ['game']
})

export class TradePanelComponent {
    game: Game;

    isVisibleTradePortPanel: boolean = false;
    isVisibleTradePlayersPanel: boolean = false;

    onShow: Function = () => this.showTradePort();
    onHide: Function = () => {
        this.isVisibleTradePortPanel = false;
        this.isVisibleTradePlayersPanel = false;
    };

    constructor(
        private _actions: ExecuteActionsService,
        private _modalWindow: ModalWindowService) { }

    showTradePort() {
        this.isVisibleTradePortPanel = true;
        this.isVisibleTradePlayersPanel = false;

        this._actions.execute('TRADE_PORT', this.game)
            .then(() => {
                this._modalWindow.hide(PANEL_ID);
            })
            .catch(() => {
                if (!this.isVisibleTradePlayersPanel) {
                    this._modalWindow.hide(PANEL_ID);
                }
            });
    }

    showTradePlayers() {
        this.isVisibleTradePortPanel = false;
        this.isVisibleTradePlayersPanel = true;

        this._actions.execute('TRADE_PLAYERS', this.game)
            .then(() => {
                this._modalWindow.hide(PANEL_ID);
            })
            .catch(() => {
                if (!this.isVisibleTradePortPanel) {
                    this._modalWindow.hide(PANEL_ID);
                }
            });
    }
}