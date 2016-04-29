import { Injectable } from 'angular2/core';

import { RemoteService } from 'app/shared/services/remote/remote.service';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { SelectService } from './select.service';
import { MarkingService } from './marking.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

import { Game } from 'app/shared/domain/game';

@Injectable()
export class PlayService {
    constructor (
        private _remote: RemoteService,
        private _authUser: AuthUserService,
        private _select: SelectService,
        private _marking: MarkingService,
        private _modalWindow: ModalWindowService) { }

    endTurn(game: Game) {
        this._beforeAnyAction();
        return this._remote.request('play.endTurn', {gameId: game.getId()});
    }

    throwDice(game: Game) {
        this._beforeAnyAction();
        return this._remote.request('play.throwDice', {gameId: game.getId()});
    }

    buyCard(game: Game) {
        this._beforeAnyAction();
        return this._remote.request('play.buyCard', {gameId: game.getId()});
    }

    buildSettlement(game: Game) {
        this._beforeAnyAction();

        return new Promise((resolve, reject) => {
            let currentPlayer = game.getCurrentPlayer(this._authUser.get());
            let availableNodes = currentPlayer.availableActions.getParams("BUILD_SETTLEMENT").nodeIds;

            if (availableNodes.length === 0) {
                reject("NO_AVAILABLE_PLACES");
                return;
            }

            this._marking.mark('nodes', availableNodes, currentPlayer);

            this._select.requestSelection('node')
                .then(nodeId => {
                    this._remote.request('play.buildSettlement', {
                        gameId: game.getId(),
                        nodeId
                    }).then(data => {
                        resolve(data);
                        this._marking.clear('nodes');
                    }).catch(data => {
                        reject(data);
                        this._marking.clear('nodes');
                    });
                })
                .catch(data => {
                    reject(data);
                    this._marking.clear('nodes');
                }
            );
        });
    }

    buildCity(game: Game) {
        this._beforeAnyAction();

        return new Promise((resolve, reject) => {
            let currentPlayer = game.getCurrentPlayer(this._authUser.get());
            let availableNodes = currentPlayer.availableActions.getParams("BUILD_CITY").nodeIds;

            if (availableNodes.length === 0) {
                reject("NO_AVAILABLE_PLACES");
                return;
            }

            this._marking.mark('nodes', availableNodes, currentPlayer);

            this._select.requestSelection('node')
                .then(nodeId => {
                    this._remote.request('play.buildCity', {
                        gameId: game.getId(),
                        nodeId
                    }).then(() => {
                        resolve();
                        this._marking.clear('nodes');
                    }).catch(data => {
                        reject(data);
                        this._marking.clear('nodes');
                    });
                })
                .catch(data => {
                    reject(data);
                    this._marking.clear('nodes');
                }
            );
        });
    }

    buildRoad(game: Game) {
        this._beforeAnyAction();

        return new Promise((resolve, reject) => {
            let currentPlayer = game.getCurrentPlayer(this._authUser.get());
            let availableEdges = currentPlayer.availableActions.getParams("BUILD_ROAD").edgeIds;

            if (availableEdges.length === 0) {
                reject("NO_AVAILABLE_PLACES");
                return;
            }

            this._marking.mark('edges', availableEdges, currentPlayer);

            this._select.requestSelection('edge')
                .then(edgeId => {
                    this._remote.request('play.buildRoad', {
                        gameId: game.getId(),
                        edgeId
                    }).then(() => {
                        resolve();
                        this._marking.clear('edges');
                    }).catch(data => {
                        reject(data);
                        this._marking.clear('edges');
                    });
                })
                .catch(data => {
                    reject(data);
                    this._marking.clear('edges');
                }
            );
        });
    }

    useCardYearOfPlenty(game: Game) {
        this._beforeAnyAction();

        return new Promise((resolve, reject) => {
            let windowAndSelectionId = "CARD_YEAR_OF_PLENTY";

            this._modalWindow.show(windowAndSelectionId);

            this._select.requestSelection(windowAndSelectionId)
                .then(response => {
                    this._remote.request('play.useCardYearOfPlenty', {
                        gameId: game.getId(),
                        firstResource: response.firstResource,
                        secondResource: response.secondResource
                    })
                        .then(() => resolve())
                        .catch(response => reject(response));

                    this._modalWindow.hide(windowAndSelectionId);
                })
                .catch(response => {
                    reject(response);
                    this._modalWindow.hide(windowAndSelectionId);
                });
        });
    }

    useCardRoadBuilding(game: Game) {
        this._beforeAnyAction();
        return this._remote.request('play.useCardRoadBuilding', {gameId: game.getId()});
    }

    useCardMonopoly(game: Game) {
        this._beforeAnyAction();

        return new Promise((resolve, reject) => {
            let windowAndSelectionId = "CARD_MONOPOLY";

            this._modalWindow.show(windowAndSelectionId);

            this._select.requestSelection(windowAndSelectionId)
                .then(response => {
                    this._remote.request('play.useCardMonopoly', {
                        gameId: game.getId(),
                        resource: response.resource
                    })
                        .then(data => resolve(data))
                        .catch(data => reject(data));

                    this._modalWindow.hide(windowAndSelectionId);
                })
                .catch(response => {
                    reject(response);
                    this._modalWindow.hide(windowAndSelectionId);
                });
        });
    }

    useCardKnight(game: Game) {
        this._beforeAnyAction();
        return this._remote.request('play.useCardKnight', {gameId: game.getId()});
    }


    moveRobber(game: Game) {
        this._beforeAnyAction();

        return new Promise((resolve, reject) => {
            let currentPlayer = game.getCurrentPlayer(this._authUser.get());
            let availableHexes = currentPlayer.availableActions.getParams("MOVE_ROBBER").hexIds;

            this._marking.mark('hexes', availableHexes);

            this._select.requestSelection('hex')
                .then(hexId => {
                    this._remote.request('play.moveRobber', {
                        gameId: game.getId(),
                        hexId
                    })
                        .then(() => {
                            resolve();
                            this._marking.clear('hexes');
                        })
                        .catch(data => {
                            reject(data);
                            this._marking.clear('hexes');
                        });
                })
                .catch(data => {
                    reject(data);
                    this._marking.clear('hexes');
                }
            );
        });
    }

    choosePlayerToRob(game: Game) {
        this._beforeAnyAction();

        return new Promise((resolve, reject) => {
            let currentPlayer = game.getCurrentPlayer(this._authUser.get());
            let availableNodes = currentPlayer.availableActions.getParams("CHOOSE_PLAYER_TO_ROB").nodeIds;

            this._marking.mark('nodes', availableNodes);

            this._select.requestSelection('node')
                .then(nodeId => {
                    let node = game.map.getNodeById(nodeId);

                    if (node.building) {
                        let playerId = node.building.ownerPlayerId;

                        this._remote.request('play.choosePlayerToRob', {
                            gameId: game.getId(),
                            gameUserId: playerId
                        })
                            .then(() => {
                                resolve();
                                this._marking.clear('nodes');
                            })
                            .catch(data => {
                                reject(data);
                                this._marking.clear('nodes');
                            });
                    } else {
                        reject();
                        this._marking.clear('nodes');
                    }
                })
                .catch(data => {
                    reject(data);
                    this._marking.clear('nodes');
                }
            );
        });
    }

    kickOffResources(game: Game) {
        this._beforeAnyAction();

        return new Promise((resolve, reject) => {
            let windowAndSelectionId = "KICK_OFF_RESOURCES";

            this._modalWindow.show(windowAndSelectionId);

            this._select.requestSelection(windowAndSelectionId)
                .then(data => {
                    this._remote.request('play.kickOffResources', {
                        gameId: game.getId(),
                        brick: data.brick,
                        wood: data.wood,
                        sheep: data.sheep,
                        wheat: data.wheat,
                        stone: data.stone
                    })
                        .then(data => resolve(data))
                        .catch(data => reject(data));

                    this._modalWindow.hide(windowAndSelectionId);
                })
                .catch(data => {
                    reject(data);
                    this._modalWindow.hide(windowAndSelectionId);
                });
        });
    }

    tradePort(game: Game) {
        this._beforeAnyAction();

        return new Promise((resolve, reject) => {
            let selectionId = "TRADE_PORT";

            this._select.requestSelection(selectionId)
                .then(resources => {
                    this._remote.request('play.tradePort', {
                        gameId: game.getId(),
                        brick: resources.brick,
                        wood: resources.wood,
                        sheep: resources.sheep,
                        wheat: resources.wheat,
                        stone: resources.stone
                    })
                        .then(data => resolve(data))
                        .catch(data => reject(data));

                })
                .catch(data => reject(data));
        });
    }

    tradePropose(game: Game) {
        this._beforeAnyAction();

        return new Promise((resolve, reject) => {
            let selectionId = "TRADE_PROPOSE";

            this._select.requestSelection(selectionId)
                .then(resources => {
                    this._remote.request('play.tradePropose', {
                        gameId: game.getId(),
                        brick: resources.brick,
                        wood: resources.wood,
                        sheep: resources.sheep,
                        wheat: resources.wheat,
                        stone: resources.stone
                    })
                        .then(data => resolve(data))
                        .catch(data => reject(data));

                })
                .catch(data => reject(data));
        });
    }

    tradeAccept(game: Game, offerId: number) {
        this._beforeAnyAction();
        return this._remote.request('play.tradeAccept', {gameId: game.getId(), offerId: offerId});
    }

    tradeDecline(game: Game, offerId: number) {
        this._beforeAnyAction();
        return this._remote.request('play.tradeDecline', {gameId: game.getId(), offerId: offerId});
    }

    private _beforeAnyAction() {
        this._select.cancelAllRequestSelections();
    }
}