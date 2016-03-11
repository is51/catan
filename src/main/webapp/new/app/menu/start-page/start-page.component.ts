import { Component } from 'angular2/core';
import { RouterLink } from 'angular2/router';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

@Component({
    templateUrl: 'app/menu/start-page/start-page.component.html',
    directives: [RouterLink]
})

export class StartPageComponent {
    constructor(public authUser: AuthUserService) { }
}