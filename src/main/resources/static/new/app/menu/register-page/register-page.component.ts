import { Component } from 'angular2/core';
import { Router } from 'angular2/router';
import { RouteDataService } from 'app/shared/services/route-data/route-data.service';
import { RegisterFormComponent } from './register-form/register-form.component';

@Component({
    templateUrl: 'app/menu/register-page/register-page.component.html',
    directives: [RegisterFormComponent]
})

export class RegisterPageComponent {
    formOnRegister: Function;

    constructor(
        private _routeData: RouteDataService,
        private _router: Router) {

        this._routeData.fetch();
        this.formOnRegister = this._routeData.get('onRegister');
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