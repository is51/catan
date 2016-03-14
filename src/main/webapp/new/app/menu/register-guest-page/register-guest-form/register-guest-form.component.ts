import { Component } from 'angular2/core';
import { AuthService } from 'app/shared/services/auth/auth.service';

@Component({
    selector: 'ct-register-guest-form',
    templateUrl: 'app/menu/register-guest-page/register-guest-form/register-guest-form.component.html'
})

export class RegisterGuestFormComponent {
    username: string = "";

    constructor(
        private _auth: AuthService) { }

    submit() {
        this._auth.registerAndLoginGuest(this.username)
            .then(() => {
                //if ($stateParams.onRegister) {
                //    $stateParams.onRegister();
                //}
            })
            .catch(data => alert('Registration guest error: ' + ((data && data.errorCode) ? data.errorCode : 'unknown')));
    }
}