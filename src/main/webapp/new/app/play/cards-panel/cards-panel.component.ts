import { Component } from 'angular2/core';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';
import { GameService } from 'app/shared/services/game/game.service';
import { PlayService } from 'app/play/shared/services/play.service';

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
        private _play: PlayService,
        private _gameService: GameService) { }

    isActionEnabled(actionCode: string) {
        return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
    }

    cards() {
        return this.game.getCurrentPlayer(this._authUser.get()).developmentCards;;
    }

    useCardYearOfPlenty() {
        this._modalWindow.hide("CARDS_PANEL");
        this._play.useCardYearOfPlenty(this.game)
            .then(() => this._gameService.refresh(this.game))
            .catch(data => {
                if (data !== "CANCELED") {
                    alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                }
            });
    }

    useCardMonopoly() {
        this._modalWindow.hide("CARDS_PANEL");
        this._play.useCardMonopoly(this.game)
            .then(data => {
                let count = data.resourcesCount; //TODO: fix red?
                if (count === 0) {
                    alert("You received " + count + " resources because players don't have this type of resource");
                } else {
                    alert("You received " + count + " resources");
                }

                this._gameService.refresh(this.game)
            })
            .catch(data => {
                if (data !== "CANCELED") {
                    alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                }
            });
    }

    useCardRoadBuilding() {
        this._modalWindow.hide("CARDS_PANEL");
        this._play.useCardRoadBuilding(this.game)
            .then(data => {
                var count = data.roadsCount; //TODO: fix red?
                alert("Build " + count + " road" + ((count===1)?"":"s"));
                this._gameService.refresh(this.game);
            })
            .catch(data => {
                alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
            });
    }

    useCardKnight() {
        this._modalWindow.hide("CARDS_PANEL");
        this._play.useCardKnight(this.game)
            .then(() => this._gameService.refresh(this.game))
            .catch(data => alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }

}