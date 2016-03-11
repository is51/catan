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
            }).then(data => {
                if (data && data.token) {

                    this._authToken.set(data.token);
                    this._authUser.load();

                    resolve(data);
                } else {
                    reject(data);
                }
            }, data => {
                reject(data);
            });
        });
    }

    logout() {
        /*var deferred = $q.defer();

        Remote.auth.logout().then(function (response, status, headers, config) {
            AuthToken.delete();
            User.setToGuest();
            deferred.resolve(response, status, headers, config);
        }, function (response, status, headers, config) {
            deferred.reject(response, status, headers, config);
        });

        return deferred.promise;*/
    }

    registerAndLoginGuest(username) {
        /*var deferred = $q.defer();

        Remote.auth.registerAndLoginGuest({
            username: username
        }).then(function (response, status, headers, config) {

            if (response.data && response.data.token) {

                AuthToken.set(response.data.token);
                User.load();

                deferred.resolve(response, status, headers, config);
            } else {
                deferred.reject(response, 400, headers, config);
            }
        }, function (response, status, headers, config) {
            deferred.reject(response, status, headers, config);
        });

        return deferred.promise;*/
    }
}