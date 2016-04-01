import { Injectable } from 'angular2/core';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';
import { PlayService } from './play.service';
import { GameService } from 'app/shared/services/game/game.service';

import { Game } from 'app/shared/domain/game';

@Injectable()
export class ActionService {
    constructor (
        private _play: PlayService,
        private _gameService: GameService,
        private _modalWindow: ModalWindowService) { }

    private _ACTIONS = {
        'TRADE_REPLY': () => {
            this._modalWindow.show("TRADE_REPLY_PANEL");
        },
        'KICK_OFF_RESOURCES': (game: Game) => {
            this._play.kickOffResources(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => {
                    if (data !== "CANCELED") {
                        alert("Kick Off Resources error!");
                    }
                });
        },
        'MOVE_ROBBER': (game: Game) => {
            this._play.moveRobber(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => {
                    if (data !== "CANCELED") {
                        alert("Move robber error!");
                    }
                });
        },
        'CHOOSE_PLAYER_TO_ROB': (game: Game) => {
            this._play.choosePlayerToRob(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => {
                    if (data !== "CANCELED") {
                        alert("Choose Player To Rob error!");
                    }
                });
        },
        'BUILD_SETTLEMENT': () => {

        },
        'BUILD_CITY': () => {

        },
        'BUILD_ROAD': () => {

        },
        'END_TURN': () => {

        }
    };

    run(code: string, param?: any) { //TODO: try to use ES6 spread instead "param?: any"
        this._ACTIONS[code](param);
    }
}