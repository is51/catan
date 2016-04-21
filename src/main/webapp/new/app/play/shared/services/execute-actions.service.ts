import { Injectable } from 'angular2/core';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';
import { PlayService } from './play.service';
import { GameService } from 'app/shared/services/game/game.service';
import { AlertService } from 'app/shared/alert/alert.service';

import { Game } from 'app/shared/domain/game';

@Injectable()
export class ExecuteActionsService {
    constructor (
        private _play: PlayService,
        private _gameService: GameService,
        private _modalWindow: ModalWindowService,
        private _alert: AlertService) { }

    private _executingActions: Set<string> = new Set<string>();

    private _ACTIONS = {
        'TRADE_REPLY': () => {
            this._modalWindow.show("TRADE_REPLY_PANEL");
        },
        'KICK_OFF_RESOURCES': (code: string, game: Game) => {
            this._executingActions.add(code);
            this._play.kickOffResources(game)
                .then(() => {
                    this._gameService.refresh(game)
                        .then(() => this._executingActions.delete(code))
                        .catch(() => this._executingActions.delete(code));
                })
                .catch(data => {
                    if (data !== "CANCELED") {
                        this._alert.message("Kick Off Resources error!");
                    }
                    this._executingActions.delete(code);
                });
        },
        'MOVE_ROBBER': (code: string, game: Game) => {
            this._play.moveRobber(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => {
                    if (data !== "CANCELED") {
                        this._alert.message("Move robber error!");
                    }
                });
        },
        'CHOOSE_PLAYER_TO_ROB': (code: string, game: Game) => {
            this._play.choosePlayerToRob(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => {
                    if (data !== "CANCELED") {
                        this._alert.message("Choose Player To Rob error!");
                    }
                });
        },
        'BUILD_SETTLEMENT': (code: string, game: Game) => {
            this._play.buildSettlement(game)
                .then(() => this._gameService.refresh(game))
                .catch(errorCode => {
                    if (errorCode === "NO_AVAILABLE_PLACES") {
                        this._alert.message("NO_AVAILABLE_PLACES");
                    } else if (errorCode !== "CANCELED") {
                        this._alert.message("Build road error!");
                    }
                });
        },
        'BUILD_CITY': (code: string, game: Game) => {
            this._play.buildCity(game)
                .then(() => this._gameService.refresh(game))
                .catch(errorCode => {
                    if (errorCode === "NO_AVAILABLE_PLACES") {
                        this._alert.message("NO_AVAILABLE_PLACES");
                    } else if (errorCode !== "CANCELED") {
                        this._alert.message("Build road error!");
                    }
                });
        },
        'BUILD_ROAD': (code: string, game: Game) => {
            this._play.buildRoad(game)
                .then(() => this._gameService.refresh(game))
                .catch(errorCode => {
                    if (errorCode === "NO_AVAILABLE_PLACES") {
                        this._alert.message("NO_AVAILABLE_PLACES");
                    } else if (errorCode !== "CANCELED") {
                        this._alert.message("Build road error!");
                    }
                });
        },
        'BUY_CARD': (code: string, game: Game) => {
            this._play.buyCard(game)
                .then(data => {
                    this._alert.message("Bought card: " + data.card); //TODO: why red?
                    this._gameService.refresh(game);
                })
                .catch(data => this._alert.message('Buy Card error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
        },
        'END_TURN': (code: string, game: Game) => {
            this._executingActions.add(code);
            this._play.endTurn(game)
                .then(() => {
                    this._gameService.refresh(game)
                        .then(() => this._executingActions.delete(code))
                        .catch(() => this._executingActions.delete(code));
                })
                .catch(data => {
                    this._alert.message('End turn error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                    this._executingActions.delete(code);
                });
        },
        'THROW_DICE': (code: string, game: Game) => {
            this._play.throwDice(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => this._alert.message('Throw Dice error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
        },
        'USE_CARD_KNIGHT': (code: string, game: Game) => {
            this._play.useCardKnight(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
        },
        'USE_CARD_ROAD_BUILDING': (code: string, game: Game) => {
            this._play.useCardRoadBuilding(game)
                .then(data => {
                    var count = data.roadsCount; //TODO: fix red?
                    this._alert.message("Build " + count + " road" + ((count===1)?"":"s"));
                    this._gameService.refresh(game);
                })
                .catch(data => {
                    this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                });
        },
        'USE_CARD_MONOPOLY': (code: string, game: Game) => {
            this._play.useCardMonopoly(game)
                .then(data => {
                    let count = data.resourcesCount; //TODO: fix red?
                    if (count === 0) {
                        this._alert.message("You received " + count + " resources because players don't have this type of resource");
                    } else {
                        this._alert.message("You received " + count + " resources");
                    }

                    this._gameService.refresh(game)
                })
                .catch(data => {
                    if (data !== "CANCELED") {
                        this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                    }
                });
        },
        'USE_CARD_YEAR_OF_PLENTY': (code: string, game: Game) => {
            this._play.useCardYearOfPlenty(game)
                .then(() => this._gameService.refresh(game))
                .catch(data => {
                    if (data !== "CANCELED") {
                        this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                    }
                });
        },
        'TRADE_PORT': (code: string, game: Game) => {
            return new Promise((resolve, reject) => {
                this._play.tradePort(game)
                    .then(() => {
                        this._gameService.refresh(game);
                        resolve();
                    })
                    .catch(data => {
                        if (data !== "CANCELED") {
                            this._alert.message('Trade Port error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                        }
                        reject();
                    });
            });
        },
        'TRADE_PLAYERS': (code: string, game: Game) => {
            return new Promise((resolve, reject) => {
                this._play.tradePropose(game)
                    .then(() => {
                        this._gameService.refresh(game);
                        resolve();
                    })
                    .catch(data => {
                        if (data !== "CANCELED") {
                            this._alert.message('Trade Players Propose error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                        }
                        reject();
                    });
            });
        }
    };

    execute(code: string, game?: Game) { //TODO: try to use ES6 spread instead "game?: Game"
        return this._ACTIONS[code](code, game);
    }

    isExecuting(code: string) {
        return this._executingActions.has(code);
    }

    //TODO: It does make sense once all actions are executed with flag isExecuting
    /*isSomeActionExecuting() {
        return this._executingActions.size > 0;
    }*/
}