System.register(['angular2/core', 'angular2/router', 'angular2/http', 'app/shared/services/auth/auth-user.service', 'app/shared/services/auth/auth.service', 'app/shared/services/auth/auth-token.service', 'app/shared/services/remote/remote.service', 'app/shared/services/game/game.service', 'app/shared/modal-window/modal-window.service', 'app/shared/services/route-data/route-data.service', 'app/shared/services/notification/notification.service', 'app/shared/services/application-active/application-active.service', 'app/shared/alert/alert.service', 'app/menu/start-page/start-page.component', 'app/menu/login-page/login-page.component', 'app/menu/register-page/register-page.component', 'app/menu/register-guest-page/register-guest-page.component', 'app/menu/create-game-page/create-game-page.component', 'app/menu/continue-game-page/continue-game-page.component', 'app/menu/join-game-page/join-game-page.component', 'app/menu/join-public-game-page/join-public-game-page.component', 'app/menu/join-private-game-page/join-private-game-page.component', 'app/menu/game-page/game-page.component', 'app/shared/alert/alerts.component'], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, router_1, http_1, auth_user_service_1, auth_service_1, auth_token_service_1, remote_service_1, game_service_1, modal_window_service_1, route_data_service_1, notification_service_1, application_active_service_1, alert_service_1, start_page_component_1, login_page_component_1, register_page_component_1, register_guest_page_component_1, create_game_page_component_1, continue_game_page_component_1, join_game_page_component_1, join_public_game_page_component_1, join_private_game_page_component_1, game_page_component_1, alerts_component_1;
    var AppComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (auth_service_1_1) {
                auth_service_1 = auth_service_1_1;
            },
            function (auth_token_service_1_1) {
                auth_token_service_1 = auth_token_service_1_1;
            },
            function (remote_service_1_1) {
                remote_service_1 = remote_service_1_1;
            },
            function (game_service_1_1) {
                game_service_1 = game_service_1_1;
            },
            function (modal_window_service_1_1) {
                modal_window_service_1 = modal_window_service_1_1;
            },
            function (route_data_service_1_1) {
                route_data_service_1 = route_data_service_1_1;
            },
            function (notification_service_1_1) {
                notification_service_1 = notification_service_1_1;
            },
            function (application_active_service_1_1) {
                application_active_service_1 = application_active_service_1_1;
            },
            function (alert_service_1_1) {
                alert_service_1 = alert_service_1_1;
            },
            function (start_page_component_1_1) {
                start_page_component_1 = start_page_component_1_1;
            },
            function (login_page_component_1_1) {
                login_page_component_1 = login_page_component_1_1;
            },
            function (register_page_component_1_1) {
                register_page_component_1 = register_page_component_1_1;
            },
            function (register_guest_page_component_1_1) {
                register_guest_page_component_1 = register_guest_page_component_1_1;
            },
            function (create_game_page_component_1_1) {
                create_game_page_component_1 = create_game_page_component_1_1;
            },
            function (continue_game_page_component_1_1) {
                continue_game_page_component_1 = continue_game_page_component_1_1;
            },
            function (join_game_page_component_1_1) {
                join_game_page_component_1 = join_game_page_component_1_1;
            },
            function (join_public_game_page_component_1_1) {
                join_public_game_page_component_1 = join_public_game_page_component_1_1;
            },
            function (join_private_game_page_component_1_1) {
                join_private_game_page_component_1 = join_private_game_page_component_1_1;
            },
            function (game_page_component_1_1) {
                game_page_component_1 = game_page_component_1_1;
            },
            function (alerts_component_1_1) {
                alerts_component_1 = alerts_component_1_1;
            }],
        execute: function() {
            AppComponent = (function () {
                function AppComponent(_authUser) {
                    this._authUser = _authUser;
                }
                AppComponent.prototype.ngOnInit = function () {
                    this._authUser.load();
                };
                AppComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-app',
                        template: "\n        <router-outlet></router-outlet>\n        <ct-alerts></ct-alerts>",
                        directives: [
                            router_1.RouterOutlet,
                            alerts_component_1.AlertsComponent
                        ],
                        providers: [
                            http_1.HTTP_PROVIDERS,
                            auth_user_service_1.AuthUserService,
                            auth_service_1.AuthService,
                            auth_token_service_1.AuthTokenService,
                            remote_service_1.RemoteService,
                            game_service_1.GameService,
                            modal_window_service_1.ModalWindowService,
                            route_data_service_1.RouteDataService,
                            notification_service_1.NotificationService,
                            application_active_service_1.ApplicationActiveService,
                            alert_service_1.AlertService
                        ]
                    }),
                    router_1.RouteConfig([
                        {
                            path: '/',
                            name: 'StartPage',
                            component: start_page_component_1.StartPageComponent,
                            useAsDefault: true
                        },
                        {
                            path: '/login',
                            name: 'LoginPage',
                            component: login_page_component_1.LoginPageComponent
                        },
                        {
                            path: '/register',
                            name: 'RegisterPage',
                            component: register_page_component_1.RegisterPageComponent,
                        },
                        {
                            path: '/register-guest',
                            name: 'RegisterGuestPage',
                            component: register_guest_page_component_1.RegisterGuestPageComponent,
                        },
                        {
                            path: '/create-game',
                            name: 'CreateGamePage',
                            component: create_game_page_component_1.CreateGamePageComponent
                        },
                        {
                            path: '/join',
                            name: 'JoinGamePage',
                            component: join_game_page_component_1.JoinGamePageComponent
                        },
                        {
                            path: '/join-public',
                            name: 'JoinPublicGamePage',
                            component: join_public_game_page_component_1.JoinPublicGamePageComponent
                        },
                        {
                            path: '/join-private',
                            name: 'JoinPrivateGamePage',
                            component: join_private_game_page_component_1.JoinPrivateGamePageComponent
                        },
                        {
                            path: '/continue',
                            name: 'ContinueGamePage',
                            component: continue_game_page_component_1.ContinueGamePageComponent
                        },
                        {
                            path: '/game/:gameId',
                            name: 'GamePage',
                            component: game_page_component_1.GamePageComponent
                        }
                    ]), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object])
                ], AppComponent);
                return AppComponent;
                var _a;
            }());
            exports_1("AppComponent", AppComponent);
        }
    }
});
