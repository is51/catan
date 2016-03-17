import { Component } from 'angular2/core';
import { AuthService } from 'app/shared/services/auth/auth.service';

@Component({
    selector: 'ct-register-guest-form',
    templateUrl: 'app/menu/register-guest-page/register-guest-form/register-guest-form.component.html',
    inputs: ['onRegister']
})

export class RegisterGuestFormComponent {
    onRegister: Function;

    //TODO: use variable data with Register..FormData interface
    username: string = "";

    constructor(
        private _auth: AuthService) { }

    submit() {
        this._auth.registerAndLoginGuest(this.username)
            .then(() => {
                if (this.onRegister) {
                    this.onRegister();
                }
            })
            .catch(data => alert('Registration guest error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}