import { Component, OnInit } from 'angular2/core';
import { RouteConfig, RouterOutlet } from 'angular2/router';
import { HTTP_PROVIDERS } from 'angular2/http';

import { AuthUserService } from 'app/shared/services/auth/auth-user.service';
import { AuthService } from 'app/shared/services/auth/auth.service';
import { AuthTokenService } from 'app/shared/services/auth/auth-token.service';
import { RemoteService } from 'app/shared/services/remote/remote.service';
import { GameService } from 'app/shared/services/game/game.service';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';
import { RouteDataService } from 'app/shared/services/route-data/route-data.service';
import { NotificationService } from 'app/shared/services/notification/notification.service';
import { ApplicationActiveService } from 'app/shared/services/application-active/application-active.service';
import { AlertService } from 'app/shared/services/alert/alert.service';

import { StartPageComponent } from 'app/menu/start-page/start-page.component';
import { LoginPageComponent } from 'app/menu/login-page/login-page.component';
import { RegisterPageComponent } from 'app/menu/register-page/register-page.component';
import { RegisterGuestPageComponent } from 'app/menu/register-guest-page/register-guest-page.component';
import { CreateGamePageComponent } from 'app/menu/create-game-page/create-game-page.component';
import { ContinueGamePageComponent } from 'app/menu/continue-game-page/continue-game-page.component';
import { JoinGamePageComponent } from 'app/menu/join-game-page/join-game-page.component';
import { JoinPublicGamePageComponent } from 'app/menu/join-public-game-page/join-public-game-page.component';
import { JoinPrivateGamePageComponent } from 'app/menu/join-private-game-page/join-private-game-page.component';
import { GamePageComponent } from 'app/menu/game-page/game-page.component';

@Component({
    selector: 'ct-app',
    template: '<router-outlet></router-outlet>',
    directives: [RouterOutlet],
    providers: [
        HTTP_PROVIDERS,

        AuthUserService,
        AuthService,
        AuthTokenService,
        RemoteService,
        GameService,
        ModalWindowService,
        RouteDataService,
        NotificationService,
        ApplicationActiveService,
        AlertService
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
        path: '/register',
        name: 'RegisterPage',
        component: RegisterPageComponent,
    },
    {
        path: '/register-guest',
        name: 'RegisterGuestPage',
        component: RegisterGuestPageComponent,
    },
    {
        path: '/create-game',
        name: 'CreateGamePage',
        component: CreateGamePageComponent
    },
    {
        path: '/join',
        name: 'JoinGamePage',
        component: JoinGamePageComponent
    },
    {
        path: '/join-public',
        name: 'JoinPublicGamePage',
        component: JoinPublicGamePageComponent
    },
    {
        path: '/join-private',
        name: 'JoinPrivateGamePage',
        component: JoinPrivateGamePageComponent
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

export class AppComponent implements OnInit {
    constructor(private _authUser: AuthUserService) { }

    ngOnInit() {
        this._authUser.load();
    }
}