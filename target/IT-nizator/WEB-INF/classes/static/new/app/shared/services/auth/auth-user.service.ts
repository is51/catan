import { Injectable } from 'angular2/core';
import { AuthTokenService } from 'app/shared/services/auth/auth-token.service';
import { RemoteService } from 'app/shared/services/remote/remote.service';
import { User } from 'app/shared/domain/user';

enum Status {
    PENDING,
    NOT_AUTHORIZED,
    AUTHORIZED
}

@Injectable()
export class AuthUserService {
    private _status : Status = Status.PENDING;
    private _details : User;

    constructor(
        private _authToken: AuthTokenService,
        private _remote: RemoteService) { }

    isAuthorized() {
        return this._status === Status.AUTHORIZED;
    }

    isNotAuthorized() {
        return this._status === Status.NOT_AUTHORIZED;
    }

    isPending() {
        return this._status === Status.PENDING;
    }

    isTypeGuest() {
        return this._status === Status.AUTHORIZED && this._details.guest;
    }

    get() {
        return this._details;
    }

    load() {
        return new Promise((resolve, reject) => {
            if (this._authToken.get()) {
                this._remote.request('auth.details').then(data => {
                    this._details = new User(data);
                    this._status = Status.AUTHORIZED;
                    resolve();
                }, () => {
                    this._authToken.delete();
                    this._status = Status.NOT_AUTHORIZED;
                    reject();
                });
            } else {
                this._status = Status.NOT_AUTHORIZED;
                reject();
            }
        });
    }

    setToGuest() {
        this._status = Status.NOT_AUTHORIZED;
        this._details = null;
    }

}