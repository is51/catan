import { Component } from 'angular2/core';

import { PlayService } from './shared/services/play.service';
import { SelectService } from './shared/services/select.service';
import { MarkingService } from './shared/services/marking.service';

import { Game } from 'app/shared/domain/game';

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
        DiceComponent
    ],
    providers: [
        PlayService,
        SelectService,
        MarkingService
    ],
    inputs: ['game']
})

export class PlayComponent {
    game: Game;
}