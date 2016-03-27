import { Component } from 'angular2/core';
import { Game } from 'app/shared/domain/game';

@Component({
    selector: 'ct-dice',
    templateUrl: 'app/play/dice/dice.component.html',
    styleUrls: ['app/play/dice/dice.component.css'],
    inputs: ['game']
})

export class DiceComponent {
    game: Game;
}