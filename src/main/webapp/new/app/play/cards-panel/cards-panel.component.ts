import { Component } from 'angular2/core';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
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

    constructor(private _authUser: AuthUserService) { }

    isActionEnabled(actionCode: string) {
        return this.game.getCurrentPlayer(this._authUser.get()).availableActions.isEnabled(actionCode);
    }

    cards() {
        return this.game.getCurrentPlayer(this._authUser.get()).developmentCards;;
    }

    /*useCardYearOfPlenty() {
        ModalWindowService.hide("CARDS_PANEL");
        PlayService.useCardYearOfPlenty(scope.game).then(function() {
            GameService.refresh(scope.game);
        }, function(reason) {
            if (reason !== "CANCELED") {
                alert('Error: ' + ((reason.data.errorCode) ? reason.data.errorCode : 'unknown'));
            }
        });
    }

    useCardMonopoly() {
        ModalWindowService.hide("CARDS_PANEL");
        PlayService.useCardMonopoly(scope.game).then(function(response) {
            var count = response.data.resourcesCount;
            if (count === 0) {
                alert("You received " + count + " resources because players don't have this type of resource");
            } else {
                alert("You received " + count + " resources");
            }
    
            GameService.refresh(scope.game);
        }, function(reason) {
            if (reason !== "CANCELED") {
                alert('Error: ' + ((reason.data.errorCode) ? reason.data.errorCode : 'unknown'));
            }
        });
    }

    useCardRoadBuilding() {
        ModalWindowService.hide("CARDS_PANEL");
        PlayService.useCardRoadBuilding(scope.game).then(function(response) {
            var count = response.data.roadsCount;
            alert("Build " + count + " road" + ((count===1)?"":"s"));
            GameService.refresh(scope.game);
        }, function(reason) {
            alert('Error: ' + ((reason.data.errorCode) ? reason.data.errorCode : 'unknown'));
        });
    }

    useCardKnight() {
        ModalWindowService.hide("CARDS_PANEL");
        PlayService.useCardKnight(scope.game).then(function() {
            GameService.refresh(scope.game);
        }, function(reason) {
            alert('Error: ' + ((reason.data.errorCode) ? reason.data.errorCode : 'unknown'));
        });
    }*/

}