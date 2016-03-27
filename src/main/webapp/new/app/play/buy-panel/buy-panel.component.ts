import { Component } from 'angular2/core';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';
import { PlayService } from 'app/play/shared/services/play.service';
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
        private _gameService: GameService) { }

    isActionEnabled(actionCode: string) {
        return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
    }

    buildSettlement() {
        this._modalWindow.hide("BUY_PANEL");
        this._play.buildSettlement(this.game)
            .then(() => this._gameService.refresh(this.game))
            .catch(errorCode => {
                if (errorCode === "NO_AVAILABLE_PLACES") {
                    alert("NO_AVAILABLE_PLACES");
                } else if (errorCode !== "CANCELED") {
                    alert("Build road error!");
                }
            });
    }

    buildCity() {
        this._modalWindow.hide("BUY_PANEL");
        this._play.buildCity(this.game)
            .then(() => this._gameService.refresh(this.game))
            .catch(errorCode => {
                if (errorCode === "NO_AVAILABLE_PLACES") {
                    alert("NO_AVAILABLE_PLACES");
                } else if (errorCode !== "CANCELED") {
                    alert("Build road error!");
                }
            });
    }

    buildRoad() {
        this._modalWindow.hide("BUY_PANEL");
        this._play.buildRoad(this.game)
            .then(() => this._gameService.refresh(this.game))
            .catch(errorCode => {
                if (errorCode === "NO_AVAILABLE_PLACES") {
                    alert("NO_AVAILABLE_PLACES");
                } else if (errorCode !== "CANCELED") {
                    alert("Build road error!");
                }
            });
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