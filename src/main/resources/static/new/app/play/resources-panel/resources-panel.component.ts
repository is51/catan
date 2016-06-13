import { Component } from 'angular2/core';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { Game } from 'app/shared/domain/game';

@Component({
    selector: 'ct-resources-panel',
    templateUrl: 'app/play/resources-panel/resources-panel.component.html',
    styleUrls: ['app/play/resources-panel/resources-panel.component.css'],
    inputs: ['game']
})

export class ResourcesPanelComponent {
    game: Game;

    constructor(private _authUser: AuthUserService) { }

    resources() {
        return this.game.getCurrentPlayer(this._authUser.get()).resources;
    }
}