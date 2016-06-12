import { Component } from 'angular2/core';
import { RouterLink } from 'angular2/router';
import { RouteDataService } from 'app/shared/services/route-data/route-data.service';
import { JoinPrivateGameFormComponent, JoinPrivateGameFormData } from './join-private-game-form/join-private-game-form.component';

@Component({
    templateUrl: 'app/menu/join-private-game-page/join-private-game-page.component.html',
    directives: [
        RouterLink,
        JoinPrivateGameFormComponent
    ]
})

export class JoinPrivateGamePageComponent {
    formData: JoinPrivateGameFormData;

    constructor(private _routeData: RouteDataService) {
        this._routeData.fetch();
        this.formData = this._routeData.get('formData');
    }
}