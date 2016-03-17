import { Component } from 'angular2/core';
import { Router } from 'angular2/router';
import { AuthService } from 'app/shared/services/auth/auth.service';

@Component({
    selector: 'ct-login-form',
    templateUrl: 'app/menu/login-page/login-form/login-form.component.html',
    inputs: ['onLogin']
})

export class LoginFormComponent {
    onLogin: Function;

    //TODO: think about "private data: LoginFormData" interface. To avoid execution of ngOnChanges()
    username: string = '';
    password: string = '';

    constructor(
        private _auth: AuthService,
        private _router: Router) { }

    submit() {
        this._auth.login(this.username, this.password)
            .then(() => {
                if (this.onLogin) {
                    this.onLogin();
                } else {
                    this._router.navigate(['StartPage']);
                }
            })
            .catch(data => alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}