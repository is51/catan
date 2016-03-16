import { Injectable } from 'angular2/core';

import { RemoteService } from 'app/shared/services/remote/remote.service';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { SelectService } from './select.service';
import { MarkingService } from './marking.service';

import { Game } from 'app/shared/domain/game';

@Injectable()
export class PlayService {
    constructor (
        private _remote: RemoteService,
        private _authUser: AuthUserService,
        private _select: SelectService,
        private _marking: MarkingService) { }

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