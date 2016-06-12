import { Component } from 'angular2/core';
import { Router } from 'angular2/router';
import { RouteDataService } from 'app/shared/services/route-data/route-data.service';
import { LoginFormComponent } from './login-form/login-form.component';

@Component({
    templateUrl: 'app/menu/login-page/login-page.component.html',
    directives: [LoginFormComponent]
})

export class LoginPageComponent {
    formOnLogin: Function;

    constructor(
        private _routeData: RouteDataService,
        private _router: Router) {

        this._routeData.fetch();
        this.formOnLogin = this._routeData.get('onLogin');
    }

    goBack() {
        let onBack = this._routeData.get('onBack');
        if (onBack) {
            onBack();
        } else {
            this._router.navigate(['StartPage']);
        }
    }
}