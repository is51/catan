import { Component, DoCheck } from 'angular2/core';

import { PlayService } from 'app/play/shared/services/play.service';
import { GameService } from 'app/shared/services/game/game.service';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

import { Game } from 'app/shared/domain/game';
import { Resources } from 'app/shared/domain/player/resources';
import { Player } from 'app/shared/domain/player/player';

import { ModalWindowDirective } from 'app/shared/modal-window/modal-window.directive';
import { ModalWindowCloseDirective } from 'app/shared/modal-window/modal-window-close.directive';

const PANEL_ID = 'TRADE_REPLY_PANEL';

@Component({
    selector: 'ct-trade-reply-panel',
    templateUrl: 'app/play/trade-reply-panel/trade-reply-panel.component.html',
    styleUrls: [
        'app/play/trade-reply-panel/trade-reply-panel.component.css',
        'app/shared/modal-window/modal-window.directive.css'
    ],
    directives: [
        ModalWindowDirective,
        ModalWindowCloseDirective
    ],
    inputs: ['game']
})

export class TradeReplyPanelComponent implements DoCheck {
    game: Game;

    propositionGive: Resources;
    propositionGet: Resources;
    offerId: number;
    offerIsActive: boolean;
    proposerName: string;
    currentPlayer: Player;

    onShow: Function = () => this._init();

    constructor(
        private _authUser: AuthUserService,
        private _play: PlayService,
        private _gameService: GameService,
        private _modalWindow: ModalWindowService) { }

    private _init() {
        this.currentPlayer = this.game.getCurrentPlayer(this._authUser.get());

        let actionParams = this.currentPlayer.availableActions.getParams('TRADE_REPLY');
        let proposition = actionParams.resources;
        this.propositionGive = new Resources();
        this.propositionGet = new Resources();

        for (let i in proposition) {
            if (proposition[i] > 0) {
                this.propositionGive[i] = proposition[i];
                this.propositionGet[i] = 0;
            } else {
                this.propositionGive[i] = 0;
                this.propositionGet[i] = -proposition[i];
            }
        }

        this.offerId = actionParams.offerId;
        this.offerIsActive = true;

        this.proposerName = this.game.getMovingPlayer().user.getDisplayedName();
    }


    accept() {
        this._play.tradeAccept(this.game, this.offerId)
            .then(() => {
                this._modalWindow.hide(PANEL_ID);
                this._gameService.refresh(this.game);
            })
            .catch(data => {
                alert('Trade Propose Accept error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                if (data.errorCode === "OFFER_ALREADY_ACCEPTED") {
                    this._modalWindow.hide(PANEL_ID);
                    this._gameService.refresh(this.game);
                }
            });
    }

    decline() {
        this._play.tradeDecline(this.game, this.offerId)
            .then(() => {
                this._modalWindow.hide(PANEL_ID);
                this._gameService.refresh(this.game);
            })
            .catch(data => {
                if (data.errorCode === "OFFER_ALREADY_ACCEPTED") {
                    this._modalWindow.hide(PANEL_ID);
                    this._gameService.refresh(this.game);
                } else {
                    alert('Trade Propose Decline error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                }
            });
    }

    acceptDisabled() {
        let currentPlayerResources = this.currentPlayer.resources;
        for (let i in this.propositionGive) {
            if (currentPlayerResources[i] < this.propositionGive[i]) {
                return true;
            }
        }
        return false;
    }

    ngDoCheck() {
        // TODO: it should be done only on changes. Currently - many useless updates
        this._checkIfOfferIsActive();
    }

    private _checkIfOfferIsActive() {
        if (this._modalWindow.isVisible(PANEL_ID) && this.currentPlayer) {
            let offerId = (this.currentPlayer.availableActions.isEnabled('TRADE_REPLY'))
                ? this.currentPlayer.availableActions.getParams('TRADE_REPLY').offerId
                : null;
            this.offerIsActive = this.offerId === offerId;
        }
    }
}