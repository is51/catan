import { Component } from 'angular2/core';
import { Router } from 'angular2/router';

import { AuthService } from 'app/shared/services/auth/auth.service';
import { RemoteService } from 'app/shared/services/remote/remote.service';
import { AlertService } from 'app/shared/services/alert/alert.service';

@Component({
    selector: 'ct-register-form',
    templateUrl: 'app/menu/register-page/register-form/register-form.component.html',
    inputs: ['onRegister']
})

export class RegisterFormComponent {
    onRegister: Function;

    data: RegisterFormData = <RegisterFormData>{
        username: '',
        password: ''
    };

    constructor(
        private _auth: AuthService,
        private _remote: RemoteService,
        private _router: Router,
        private _alert: AlertService) { }

    submit() {
        this._remote.request('auth.register', {
            username: this.data.username,
            password: this.data.password
        })
            .then(() => {
                this._auth.login(this.data.username, this.data.password)
                    .then(() => {
                        if (this.onRegister) {
                            this.onRegister();
                        } else {
                            this._router.navigate(['StartPage']);
                        }
                    }, (data) => {
                        this._alert.message('Login error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                    });

            }, (data) => {
                this._alert.message('Registration error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
            });
    }
}

export interface RegisterFormData {
    username: string;
    password: string;
}