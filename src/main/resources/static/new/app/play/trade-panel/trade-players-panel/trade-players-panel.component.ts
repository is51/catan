import { Component } from 'angular2/core';
import { Game } from 'app/shared/domain/game';
import { ChooseResourcesComponent } from 'app/play/shared/choose-resources/choose-resources.component';

@Component({
    selector: 'ct-trade-players-panel',
    templateUrl: 'app/play/trade-panel/trade-players-panel/trade-players-panel.component.html',
    styleUrls: ['app/play/trade-panel/trade-players-panel/trade-players-panel.component.css'],
    directives: [ChooseResourcesComponent],
    inputs: ['game']
})

export class TradePlayersPanelComponent {
    game: Game;
}