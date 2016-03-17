import { Injectable } from 'angular2/core';
import { Http, Headers, RequestOptions } from 'angular2/http';
import { AuthTokenService } from 'app/shared/services/auth/auth-token.service';

@Injectable()
export class RemoteService {
    private _defaultOptions: RequestOptions;
    private _requestsOptions: Map<string, RequestOptions> = new Map<string, RequestOptions>();

    constructor (
        private _http: Http,
        private _authToken: AuthTokenService) {

        this._setConfig();
    }

    private _setConfig() {
        //TODO: create and use @RemoteConfig in app.component

        this._setDefaultOptions(new RequestOptions({
            headers: new Headers({
                'Content-Type': 'application/x-www-form-urlencoded',
                'Accept': 'application/json'
            }),
            method: 'post'
        }));

        this._setRequestOptionsUrlOnly('auth.register', '/api/user/register');
        this._setRequestOptionsUrlOnly('auth.registerAndLoginGuest', '/api/user/register/guest');
        this._setRequestOptionsUrlOnly('auth.login', '/api/user/login');
        this._setRequestOptionsUrlOnly('auth.logout', '/api/user/logout');
        this._setRequestOptionsUrlOnly('auth.details', '/api/user/details');

        this._setRequestOptionsUrlOnly('game.create', '/api/game/create');
        this._setRequestOptionsUrlOnly('game.listCurrent', '/api/game/list/current');
        this._setRequestOptionsUrlOnly('game.listPublic', '/api/game/list/public');
        this._setRequestOptionsUrlOnly('game.joinPublic', '/api/game/join/public');
        this._setRequestOptionsUrlOnly('game.joinPrivate', '/api/game/join/private');
        this._setRequestOptionsUrlOnly('game.details', '/api/game/details');
        this._setRequestOptionsUrlOnly('game.leave', '/api/game/leave');
        this._setRequestOptionsUrlOnly('game.cancel', '/api/game/cancel');
        this._setRequestOptionsUrlOnly('game.ready', '/api/game/ready');
        this._setRequestOptionsUrlOnly('game.notReady', '/api/game/not-ready');

        this._setRequestOptionsUrlOnly('play.endTurn', '/api/play/end-turn');
        this._setRequestOptionsUrlOnly('play.buildSettlement', '/api/play/build/settlement');
        this._setRequestOptionsUrlOnly('play.buildCity', '/api/play/build/city');
        this._setRequestOptionsUrlOnly('play.buildRoad', '/api/play/build/road');
        this._setRequestOptionsUrlOnly('play.buyCard', '/api/play/buy/card');
        this._setRequestOptionsUrlOnly('play.throwDice', '/api/play/throw-dice');
        this._setRequestOptionsUrlOnly('play.useCardYearOfPlenty', '/api/play/use-card/year-of-plenty');
        this._setRequestOptionsUrlOnly('play.useCardRoadBuilding', '/api/play/use-card/road-building');
        this._setRequestOptionsUrlOnly('play.useCardMonopoly', '/api/play/use-card/monopoly');
        this._setRequestOptionsUrlOnly('play.useCardKnight', '/api/play/use-card/knight');
        this._setRequestOptionsUrlOnly('play.moveRobber', '/api/play/robbery/move-robber');
        this._setRequestOptionsUrlOnly('play.choosePlayerToRob', '/api/play/robbery/choose-player-to-rob');
        this._setRequestOptionsUrlOnly('play.kickOffResources', '/api/play/robbery/kick-off-resources');
        this._setRequestOptionsUrlOnly('play.tradePort', '/api/play/trade/port');
        this._setRequestOptionsUrlOnly('play.tradePropose', '/api/play/trade/propose');
        this._setRequestOptionsUrlOnly('play.tradeAccept', '/api/play/trade/reply/accept');
        this._setRequestOptionsUrlOnly('play.tradeDecline', '/api/play/trade/reply/decline');;
    }

    private _setDefaultOptions(options: RequestOptions) {
        this._defaultOptions = options;
    }

    private _setRequestOptionsUrlOnly(requestName: string, url: string) {
        this._setRequestOptions(requestName, new RequestOptions({ url }));
    }

    private _setRequestOptions(requestName: string, options: RequestOptions) {
        this._requestsOptions.set(requestName, options);
    }

    private _getRequestOptionsMerged(requestName: string) {
        return this._defaultOptions.merge(this._requestsOptions.get(requestName));
    }

    request(requestName: string, params = null) {

        let token = this._authToken.get();

        let fullParams = this._mergeParamsWithToken(params, token);
        let body = this._convertToBody(fullParams);

        let options = this._getRequestOptionsMerged(requestName)
            .merge(new RequestOptions({ body }));

        return new Promise((resolve, reject) => {
            this._http.request('', options).subscribe(
                    response => {
                        let data = (response.text() === '') ? {} : response.json();
                        resolve(data);
                    },
                    response => {
                        let data = (response.text() === '') ? {} : response.json();
                        reject(data);
                    }
            );
        });
    }

    private _convertToBody(params: Object) {
        let body = '';
        for (let key in params) {
            if (body !== '') {
                body += '&';
            }
            body += key + '=' + params[key];
        }
        return body;
    }

    private _mergeParamsWithToken(params, token) {
        let merged = {};

        if (params) {
            for (let i in params) {
                merged[i] = params[i];
            }
        }

        if (token) {
            merged['token'] = token;
        }

        return merged;
    }
}