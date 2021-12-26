import { Injectable } from 'angular2/core';
import { AuthTokenService } from 'app/shared/services/auth/auth-token.service';
import { RemoteService } from 'app/shared/services/remote/remote.service';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

@Injectable()
export class AuthService {
    constructor(
        private _authToken: AuthTokenService,
        private _remote: RemoteService,
        private _authUser: AuthUserService) { }

    login(username: string, password: string) {
        return new Promise((resolve, reject) => {
            this._remote.request('auth.login', {
                username: username,
                password: password
            })
                .then(data => {
                    if (data && data.token) {
                        this._authToken.set(data.token);
                        this._authUser.load();
                        resolve(data);
                    } else {
                        reject(data);
                    }
                })
                .catch(data => reject(data));
        });
    }

    logout() {
        return new Promise((resolve, reject) => {
            this._remote.request('auth.logout')
                .then(data => {
                    this._authToken.delete();
                    this._authUser.setToGuest();
                    resolve(data);
                })
                .catch(data => reject(data));
        });
    }

    registerAndLoginGuest(username) {
        return new Promise((resolve, reject) => {
            this._remote.request('auth.registerAndLoginGuest', {username: username})
                .then(data => {
                    if (data.token) {
                        this._authToken.set(data.token);
                        this._authUser.load();
                        resolve(data);
                    } else {
                        reject(data);
                    }
                })
                .catch(data => reject(data));
        });
    }
}