import { Component } from 'angular2/core';
import { RouteDataService } from 'app/shared/services/route-data/route-data.service';
import { CreateGameFormComponent, CreateGameFormData } from './create-game-form/create-game-form.component';

@Component({
    templateUrl: 'app/menu/create-game-page/create-game-page.component.html',
    directives: [CreateGameFormComponent]
})

export class CreateGamePageComponent {
    formData: CreateGameFormData;

    constructor(private _routeData: RouteDataService) {
        this._routeData.fetch();
        this.formData = this._routeData.get('formData');
    }
}