import { Component } from 'angular2/core';
import { Router } from 'angular2/router';

import { AuthService } from 'app/shared/services/auth/auth.service';
import { RemoteService } from 'app/shared/services/remote/remote.service';

@Component({
    selector: 'ct-register-form',
    templateUrl: 'app/menu/register-page/register-form/register-form.component.html',
    inputs: ['onRegister']
})

export class RegisterFormComponent {
    onRegister: Function;

    //TODO: use variable data with Register..FormData interface
    username: string = '';
    password: string = '';

    constructor(
        private _auth: AuthService,
        private _remote: RemoteService,
        private _router: Router) { }

    submit() {
        this._remote.request('auth.register', {
            username: this.username,
            password: this.password
        })
            .then(() => {
                this._auth.login(this.username, this.password)
                    .then(() => {
                        if (this.onRegister) {
                            this.onRegister();
                        } else {
                            this._router.navigate(['StartPage']);
                        }
                    }, (data) => {
                        alert('Login error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                    });

            }, (data) => {
                alert('Registration error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
            });
    }
}