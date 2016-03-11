import { Component } from 'angular2/core';
import { Router } from 'angular2/router';
import { AuthService } from 'app/shared/services/auth/auth.service';

@Component({
    selector: 'ct-login-form',
    templateUrl: 'app/menu/login-page/login-form/login-form.component.html'
})

export class LoginFormComponent {
    username: string = "";
    password: string = "";

    constructor(
        private _auth: AuthService,
        private _router: Router) { }

    submit() {
        this._auth.login(this.username, this.password)
            .then(() => {
                //if ($stateParams.onLogin) {
                //    $stateParams.onLogin();
                //} else {
                    this._router.navigate(['StartPage']);
                //}
            }, data => {
                alert('Error: ' + ((data && data.errorCode) ? data.errorCode : 'unknown'));
            });
    }
}