import { Component } from 'angular2/core';
import { RouterLink } from 'angular2/router';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { LogoutButtonDirective } from './logout-button.directive';

@Component({
    templateUrl: 'app/menu/start-page/start-page.component.html',
    directives: [
        RouterLink,
        LogoutButtonDirective
    ]
})

export class StartPageComponent {
    constructor(public authUser: AuthUserService) { }
}