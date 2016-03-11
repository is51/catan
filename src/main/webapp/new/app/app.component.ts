import { Component } from 'angular2/core';
import { RouteConfig, RouterOutlet, ROUTER_PROVIDERS } from 'angular2/router';
import { HTTP_PROVIDERS } from 'angular2/http';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { AuthService } from 'app/shared/services/auth/auth.service';
import { AuthTokenService } from 'app/shared/services/auth/auth-token.service';
import { RemoteService } from 'app/shared/services/remote/remote.service';
import { GameService } from 'app/shared/services/game/game.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

import { StartPageComponent } from 'app/menu/start-page/start-page.component';
import { LoginPageComponent } from 'app/menu/login-page/login-page.component';
import { ContinueGamePageComponent } from 'app/menu/continue-game-page/continue-game-page.component';
import { GamePageComponent } from 'app/menu/game-page/game-page.component';

@Component({
    selector: 'ct-app',
    template: '<router-outlet></router-outlet>',
    directives: [RouterOutlet],
    providers: [
        ROUTER_PROVIDERS,
        HTTP_PROVIDERS,

        AuthUserService,
        AuthService,
        AuthTokenService,
        RemoteService,
        GameService,
        ModalWindowService
    ]
})

@RouteConfig([
    {
        path: '/',
        name: 'StartPage',
        component: StartPageComponent,
        useAsDefault: true
    },
    {
        path: '/login',
        name: 'LoginPage',
        component: LoginPageComponent
    },
    {
        path: '/continue',
        name: 'ContinueGamePage',
        component: ContinueGamePageComponent
    },
    {
        path: '/game/:gameId',
        name: 'GamePage',
        component: GamePageComponent
    }
])

export class AppComponent {
    constructor(private _authUser: AuthUserService) {
        this._authUser.load();
    }
}