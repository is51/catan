import { Component } from 'angular2/core';
import { RouterLink } from 'angular2/router';
import { GamesListComponent } from 'app/menu/shared/games-list/games-list.component';

@Component({
    templateUrl: 'app/menu/join-public-game-page/join-public-game-page.component.html',
    directives: [
        RouterLink,
        GamesListComponent
    ]
})

export class JoinPublicGamePageComponent {

}