import { Component, OnInit, OnDestroy } from 'angular2/core';

import { PlayService } from './shared/services/play.service';
import { SelectService } from './shared/services/select.service';
import { MarkingService } from './shared/services/marking.service';
import { ExecuteActionsService } from './shared/services/execute-actions.service';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { NotificationService } from 'app/shared/services/notification/notification.service';
import { TemplatesService } from './shared/services/templates.service';

import { Game } from 'app/shared/domain/game';
import { AvailableActions, AvailableAction } from 'app/shared/domain/player/available-actions';

import { ResourcesPanelComponent } from './resources-panel/resources-panel.component';
import { PlayersPanelComponent } from './players-panel/players-panel.component';
import { ActionsPanelComponent } from './actions-panel/actions-panel.component';
import { BuyPanelComponent } from './buy-panel/buy-panel.component';
import { GameMapComponent } from './game-map/game-map.component';
import { CardsPanelComponent } from './cards-panel/cards-panel.component';
import { TradePanelComponent } from './trade-panel/trade-panel.component';
import { TradeReplyPanelComponent } from './trade-reply-panel/trade-reply-panel.component';
import { CardYearOfPlentyChooseResourcesPanelComponent } from './card-year-of-plenty-choose-resources-panel/card-year-of-plenty-choose-resources-panel.component';
import { CardMonopolyChooseResourcePanelComponent } from './card-monopoly-choose-resource-panel/card-monopoly-choose-resource-panel.component';
import { KickOffResourcesPanelComponent } from './kick-off-resources-panel/kick-off-resources-panel.component';
import { DiceComponent } from './dice/dice.component';
import { TopMessageComponent } from './top-message/top-message.component';
import { GameResultsComponent } from './game-results/game-results.component';
import { LogPanelComponent } from './log-panel/log-panel.component';
import { LogButtonComponent } from './log-button/log-button.component';

//TODO: revise pointer-events of all map elements (set 'none' most of it)

@Component({
    selector: 'ct-play',
    templateUrl: 'app/play/play.component.html',
    directives: [
        ResourcesPanelComponent,
        PlayersPanelComponent,
        ActionsPanelComponent,
        BuyPanelComponent,
        GameMapComponent,
        CardsPanelComponent,
        TradePanelComponent,
        TradeReplyPanelComponent,
        CardYearOfPlentyChooseResourcesPanelComponent,
        CardMonopolyChooseResourcePanelComponent,
        KickOffResourcesPanelComponent,
        DiceComponent,
        TopMessageComponent,
        GameResultsComponent,
        LogPanelComponent,
        LogButtonComponent
    ],
    providers: [
        PlayService,
        SelectService,
        ExecuteActionsService,
        MarkingService,
        TemplatesService
    ],
    inputs: ['game']
})

export class PlayComponent implements OnInit, OnDestroy {
    game: Game;
    availableActions: AvailableActions;

    templatesLoaded: boolean = false;

    constructor(
        private _notification: NotificationService,
        private _authUser: AuthUserService,
        private _actions: ExecuteActionsService,
        private _templates: TemplatesService) { }

    ngOnInit() {
        this.availableActions = this.game.getCurrentPlayer(this._authUser.get()).availableActions;

        this._checkIfImmediateAndExecute();

        this.availableActions.onUpdate(newActions => {
            this._checkIfHasNotificationAndNotify(newActions);
            this._checkIfImmediateAndExecute();
        });

        this._templates.load()
            .then(() => {

                this.templatesLoaded = true;
            });
    }

    ngOnDestroy() {
        this.availableActions.cancelOnUpdate();
    }

    private _checkIfHasNotificationAndNotify(actions: AvailableAction[]) {
        actions.forEach(action => {
            if (action.notify) {
                this._notification.notifyGlobal(action.notifyMessage, action.code);
            }
        });
    }

    private _checkIfImmediateAndExecute() {
        if (this.availableActions.isImmediate) {
            this._actions.execute(this.availableActions.list[0].code, this.game);
        }
    }
}