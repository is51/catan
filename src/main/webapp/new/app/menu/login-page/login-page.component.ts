import { Component } from 'angular2/core';
import { RouteDataService } from 'app/shared/services/route-data/route-data.service';
import { LoginFormComponent } from './login-form/login-form.component';

@Component({
    templateUrl: 'app/menu/login-page/login-page.component.html',
    directives: [LoginFormComponent]
})

export class LoginPageComponent {
    formOnLogin: Function;

    constructor(private _routeData: RouteDataService) {
        this._routeData.fetch();
        this.formOnLogin = this._routeData.get('onLogin');
    }

    goBack() {
        let onBack = this._routeData.get('onBack');
        if (onBack) {
            onBack();
        } else {
            //TODO: default action
        }
    }
}