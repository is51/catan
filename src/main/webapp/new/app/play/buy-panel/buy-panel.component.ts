import { Component } from 'angular2/core';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
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

    constructor(private _authUser: AuthUserService) { }

    isActionEnabled(actionCode: string) {
        return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
    }

    /*buildSettlement() {
        ModalWindowService.hide("BUY_PANEL");
        PlayService.buildSettlement(scope.game).then(function() {
            GameService.refresh(scope.game);
        }, function(reason) {
            if (reason !== "CANCELED") {
                alert("Build settlement error!");
            }
        });
    }

    buildCity() {
        ModalWindowService.hide("BUY_PANEL");
        PlayService.buildCity(scope.game).then(function() {
            GameService.refresh(scope.game);
        }, function(reason) {
            if (reason !== "CANCELED") {
                alert("Build city error!");
            }
        });
    }

    buildRoad() {
        ModalWindowService.hide("BUY_PANEL");
        PlayService.buildRoad(scope.game).then(function() {
            GameService.refresh(scope.game);
        }, function(reason) {
            if (reason !== "CANCELED") {
                alert("Build road error!");
            }
        });
    }

    buyCard() {
        ModalWindowService.hide("BUY_PANEL");
        PlayService.buyCard(scope.game).then(function(response) {
            alert("Bought card: " + response.data.card);
            GameService.refresh(scope.game);
        }, function(response) {
            alert('Buy Card error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
        });
    }*/
}