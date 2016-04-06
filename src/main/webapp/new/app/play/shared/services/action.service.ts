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
        'BUILD_SETTLEMENT': (game: Game) => {
            this._play.buildSettlement(game)
                .then(() => this._gameService.refresh(game))
                .catch(errorCode => {
                    if (errorCode === "NO_AVAILABLE_PLACES") {
                        alert("NO_AVAILABLE_PLACES");
                    } else if (errorCode !== "CANCELED") {
                        alert("Build road error!");
                    }
                });
        },
        'BUILD_CITY': (game: Game) => {
            this._play.buildCity(game)
                .then(() => this._gameService.refresh(game))
                .catch(errorCode => {
                    if (errorCode === "NO_AVAILABLE_PLACES") {
                        alert("NO_AVAILABLE_PLACES");
                    } else if (errorCode !== "CANCELED") {
                        alert("Build road error!");
                    }
                });
        },
        'BUILD_ROAD': (game: Game) => {
            this._play.buildRoad(game)
                .then(() => this._gameService.refresh(game))
                .catch(errorCode => {
                    if (errorCode === "NO_AVAILABLE_PLACES") {
                        alert("NO_AVAILABLE_PLACES");
                    } else if (errorCode !== "CANCELED") {
                        alert("Build road error!");
                    }
                });
        },
        'BUY_CARD': (game: Game) => {
            this._play.buyCard(game)
                .then(data => {
                    alert("Bought card: " + data.card); //TODO: why red?
                    this._gameService.refresh(game);
                })
                .catch(data => alert('Buy Card error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
        },
        'END_TURN': (game: Game) => {
            this._play.endTurn(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => alert('End turn error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
        },
        'THROW_DICE': (game: Game) => {
            this._play.throwDice(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => alert('Throw Dice error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
        },
        'USE_CARD_KNIGHT': (game: Game) => {
            this._play.useCardKnight(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
        },
        'USE_CARD_ROAD_BUILDING': (game: Game) => {
            this._play.useCardRoadBuilding(game)
                .then(data => {
                    var count = data.roadsCount; //TODO: fix red?
                    alert("Build " + count + " road" + ((count===1)?"":"s"));
                    this._gameService.refresh(game);
                })
                .catch(data => {
                    alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                });
        },
        'USE_CARD_MONOPOLY': (game: Game) => {
            this._play.useCardMonopoly(game)
                .then(data => {
                    let count = data.resourcesCount; //TODO: fix red?
                    if (count === 0) {
                        alert("You received " + count + " resources because players don't have this type of resource");
                    } else {
                        alert("You received " + count + " resources");
                    }

                    this._gameService.refresh(game)
                })
                .catch(data => {
                    if (data !== "CANCELED") {
                        alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                    }
                });
        },
        'USE_CARD_YEAR_OF_PLENTY': (game: Game) => {
            this._play.useCardYearOfPlenty(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => {
                    if (data !== "CANCELED") {
                        alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                    }
                });
        },
        'TRADE_PORT': (game: Game) => {
            return new Promise((resolve, reject) => {
                this._play.tradePort(game)
                    .then(() => {
                        this._gameService.refresh(game);
                        resolve();
                    })
                    .catch(data => {
                        if (data !== "CANCELED") {
                            alert('Trade Port error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                        }
                        reject();
                    });
            });
        },
        'TRADE_PLAYERS': (game: Game) => {
            return new Promise((resolve, reject) => {
                this._play.tradePropose(game)
                    .then(() => {
                        this._gameService.refresh(game);
                        resolve();
                    })
                    .catch(data => {
                        if (data !== "CANCELED") {
                            alert('Trade Players Propose error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                        }
                        reject();
                    });
            });
        }
    };

    execute(code: string, game?: Game) { //TODO: try to use ES6 spread instead "game?: Game"
        return this._ACTIONS[code](game);
    }
}