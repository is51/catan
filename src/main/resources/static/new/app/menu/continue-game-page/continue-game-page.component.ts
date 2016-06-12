import { Component } from 'angular2/core';
import { RouterLink } from 'angular2/router';
import { GamesListComponent } from 'app/menu/shared/games-list/games-list.component';

@Component({
    templateUrl: 'app/menu/continue-game-page/continue-game-page.component.html',
    directives: [
        GamesListComponent,
        RouterLink
    ]
})

export class ContinueGamePageComponent {

}