import { Component } from 'angular2/core';
import { Router } from 'angular2/router';

import { RouteDataService } from 'app/shared/services/route-data/route-data.service';
import { RegisterGuestFormComponent } from './register-guest-form/register-guest-form.component';

@Component({
    templateUrl: 'app/menu/register-guest-page/register-guest-page.component.html',
    directives: [RegisterGuestFormComponent]
})

export class RegisterGuestPageComponent {
    formOnRegister: Function;
    onBack: Function;

    constructor(
        private _routeData: RouteDataService,
        private _router: Router) {

        this._routeData.fetch();
        this.formOnRegister = this._routeData.get('onRegister');
        this.onBack = this._routeData.get('onBack');
    }

    goBack() {
        if (this.onBack) {
            this.onBack();
        } else {
            this._router.navigate(['StartPage']);
        }
    }

    login() {
        this._routeData.put({
            onLogin: this.formOnRegister,
            onBack: this.onBack
        });
        this._router.navigate(['LoginPage']);
    }

    registerRegularUser() {
        this._routeData.put({
            onRegister: this.formOnRegister,
            onBack: this.onBack
        });
        this._router.navigate(['RegisterPage']);
    }
}