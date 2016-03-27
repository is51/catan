import { Injectable } from 'angular2/core';
import { RemoteService } from 'app/shared/services/remote/remote.service';
import { Game } from 'app/shared/domain/game';

@Injectable()
export class GameService {
    private _refreshingTimeout;

    constructor(
        private _remote: RemoteService) { }

    findAllByType(type: string) {
        // type can be "PUBLIC" or "CURRENT"
        let remoteRequestName = (type === 'CURRENT') ? 'game.listCurrent' : 'game.listPublic';
        return this.findAllByRemoteService(remoteRequestName);
    }

    findAllByRemoteService(requestName: string) {
        return new Promise((resolve, reject) => {
            this._remote.request(requestName)
                .then(data => {
                    let items = data.map(item => new Game(item));
                    resolve(items);
                }, error => {
                    reject(error);
                });
        });
    }

    findById(id: number) {
        return new Promise((resolve, reject) => {
            this._remote.request('game.details', {gameId: id}).then(data => {
                let game = new Game(data);
                resolve(game);
            }, error => {
                reject(error);
            });
        });
    }

    startRefreshing(game: Game, delay: number, onEverySuccess: Function, onEveryError: Function) {
        this.stopRefreshing();

        this._refreshingTimeout = setTimeout(() => {
            this.refresh(game).then(data => {
                let continueUpdating = true;
                if (onEverySuccess) {
                    continueUpdating = onEverySuccess(data) !== false;
                }
                if (continueUpdating) {
                    this.startRefreshing(game, delay, onEverySuccess, onEveryError);
                }
            }, error => {
                var continueUpdating = true;
                if (onEveryError) {
                    continueUpdating = onEveryError(error) !== false;
                }
                if (continueUpdating) {
                    this.startRefreshing(game, delay, onEverySuccess, onEveryError);
                }
            });
        }, delay);
    }

    refresh(game: Game) {
        return new Promise((resolve, reject) => {
            this._remote.request('game.details', {gameId: game.getId()}).then(data => {
                game.update(data);
                resolve(data);
            }, error => {
                reject(error);
            });
        });
    }

    stopRefreshing() {
        clearTimeout(this._refreshingTimeout);
    }

}