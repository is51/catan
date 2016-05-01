import { Component } from 'angular2/core';
import { RouterLink } from 'angular2/router';

import { Game } from 'app/shared/domain/game';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

import { PlayersListComponent } from 'app/menu/shared/players-list/players-list.component';
import { CancelGameButtonDirective } from './cancel-game-button/cancel-game-button.directive';
import { LeaveGameButtonDirective } from './leave-game-button/leave-game-button.directive';
import { ReadyButtonDirective } from './ready-button/ready-button.directive';
import { GameMapOverviewComponent } from './game-map-overview/game-map-overview.component';

@Component({
    selector: 'ct-game-room',
    templateUrl: 'app/menu/game-page/game-room/game-room.component.html',
    directives: [
        PlayersListComponent,
        CancelGameButtonDirective,
        LeaveGameButtonDirective,
        ReadyButtonDirective,
        GameMapOverviewComponent,
        RouterLink
    ],
    inputs: ['game']
})

export class GameRoomComponent {
    game: Game;

    constructor(private _authUser: AuthUserService) { }

    isCurrentUserCreator() {
        return this._authUser.get().id === this.game.creatorId;
    }
}