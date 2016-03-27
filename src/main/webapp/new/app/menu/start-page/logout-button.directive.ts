import { Directive } from 'angular2/core';
import { AuthService } from 'app/shared/services/auth/auth.service';

@Directive({
    selector: '[ct-logout-button]',
    host: {
        '(click)': 'onClick($event)',
    }
})

export class LogoutButtonDirective {
    constructor(private _auth: AuthService) { }

    onClick() {
        this._auth.logout()
            .catch(data => alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')));
    }
}