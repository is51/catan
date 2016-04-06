import { Directive } from 'angular2/core';
import { AuthService } from 'app/shared/services/auth/auth.service';
import { AlertService } from 'app/shared/services/alert/alert.service';

@Directive({
    selector: '[ct-logout-button]',
    host: {
        '(click)': 'onClick($event)',
    }
})

export class LogoutButtonDirective {
    constructor(
        private _auth: AuthService,
        private _alert: AlertService) { }

    onClick() {
        this._auth.logout()
            .catch(data => this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}