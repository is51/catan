import { Component } from 'angular2/core';

import { PlayService } from 'app/play/shared/services/play.service';

import { Game } from 'app/shared/domain/game';

import { ResourcesPanelComponent } from 'app/play/resources-panel/resources-panel.component';
import { PlayersPanelComponent } from 'app/play/players-panel/players-panel.component';
import { ActionsPanelComponent } from 'app/play/actions-panel/actions-panel.component';
import { BuyPanelComponent } from 'app/play/buy-panel/buy-panel.component';

@Component({
    selector: 'ct-play',
    templateUrl: 'app/play/play.component.html',
    directives: [
        ResourcesPanelComponent,
        PlayersPanelComponent,
        ActionsPanelComponent,
        BuyPanelComponent
    ],
    providers: [PlayService],
    inputs: ['game']
})

export class PlayComponent {
    game: Game;
}