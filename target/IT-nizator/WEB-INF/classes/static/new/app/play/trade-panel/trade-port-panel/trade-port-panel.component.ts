import { Component, OnInit } from 'angular2/core';
import { Game } from 'app/shared/domain/game';
import { ChooseResourcesComponent } from 'app/play/shared/choose-resources/choose-resources.component';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

@Component({
    selector: 'ct-trade-port-panel',
    templateUrl: 'app/play/trade-panel/trade-port-panel/trade-port-panel.component.html',
    styleUrls: ['app/play/trade-panel/trade-port-panel/trade-port-panel.component.css'],
    directives: [ChooseResourcesComponent],
    inputs: ['game']
})

export class TradePortPanelComponent implements OnInit {
    game: Game;
    ratio;

    constructor(private _authUser: AuthUserService) { }

    ngOnInit() {
        this.ratio = this.game
            .getCurrentPlayer(this._authUser.get())
            .availableActions.getParams("TRADE_PORT");
    }

}