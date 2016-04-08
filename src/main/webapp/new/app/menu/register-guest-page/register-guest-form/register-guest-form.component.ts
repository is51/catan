import { Component } from 'angular2/core';

import { AuthService } from 'app/shared/services/auth/auth.service';
import { AlertService } from 'app/shared/alert/alert.service';

@Component({
    selector: 'ct-register-guest-form',
    templateUrl: 'app/menu/register-guest-page/register-guest-form/register-guest-form.component.html',
    inputs: ['onRegister']
})

export class RegisterGuestFormComponent {
    onRegister: Function;

    data: RegisterGuestFormData = <RegisterGuestFormData>{
        username: ''
    };

    constructor(
        private _auth: AuthService,
        private _alert: AlertService) { }

    submit() {
        this._auth.registerAndLoginGuest(this.data.username)
            .then(() => {
                if (this.onRegister) {
                    this.onRegister();
                }
            })
            .catch(data => this._alert.message('Registration guest error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}

export interface RegisterGuestFormData {
    username: string;
}