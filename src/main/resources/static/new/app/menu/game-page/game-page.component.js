System.register(['angular2/core', 'angular2/router', 'app/shared/services/notification/notification.service', 'app/shared/services/game/game.service', 'app/shared/alert/alert.service', 'app/play/play.component', './game-room/game-room.component'], function(exports_1, context_1) {
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
    var core_1, router_1, notification_service_1, game_service_1, alert_service_1, play_component_1, game_room_component_1;
    var GAME_UPDATE_DELAY, GamePageComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (notification_service_1_1) {
                notification_service_1 = notification_service_1_1;
            },
            function (game_service_1_1) {
                game_service_1 = game_service_1_1;
            },
            function (alert_service_1_1) {
                alert_service_1 = alert_service_1_1;
            },
            function (play_component_1_1) {
                play_component_1 = play_component_1_1;
            },
            function (game_room_component_1_1) {
                game_room_component_1 = game_room_component_1_1;
            }],
        execute: function() {
            GAME_UPDATE_DELAY = 4000;
            GamePageComponent = (function () {
                function GamePageComponent(_gameService, _routeParams, _router, _notification, _alert) {
                    this._gameService = _gameService;
                    this._routeParams = _routeParams;
                    this._router = _router;
                    this._notification = _notification;
                    this._alert = _alert;
                    this.game = null;
                    this._gameId = +this._routeParams.get('gameId');
                }
                GamePageComponent.prototype.ngOnInit = function () {
                    this._loadGameAndStartRefreshing();
                    this._notification.requestPermission();
                };
                GamePageComponent.prototype._loadGameAndStartRefreshing = function () {
                    var _this = this;
                    this._gameService.findById(this._gameId)
                        .then(function (game) {
                        _this.game = game;
                        _this._subscribeOnGameStarting();
                        _this._gameService.startRefreshing(_this.game, GAME_UPDATE_DELAY, null, function () {
                            _this._alert.message('Getting Game Details Error. Probably there is a connection problem');
                            return false;
                        });
                    }, function () {
                        _this._alert.message('Getting Game Details Error')
                            .then(function () { return _this._router.navigate(['StartPage']); });
                    });
                };
                GamePageComponent.prototype._subscribeOnGameStarting = function () {
                    var _this = this;
                    this.game.onStartPlaying(function () {
                        //TODO: revise this temp notification (probably it will be done using log)
                        _this._notification.notifyGlobal('Game is started!', 'GAME_IS_STARTED');
                    });
                };
                GamePageComponent.prototype.ngOnDestroy = function () {
                    this._gameService.stopRefreshing();
                };
                GamePageComponent = __decorate([
                    core_1.Component({
                        templateUrl: 'app/menu/game-page/game-page.component.html',
                        directives: [
                            play_component_1.PlayComponent,
                            game_room_component_1.GameRoomComponent
                        ]
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof game_service_1.GameService !== 'undefined' && game_service_1.GameService) === 'function' && _a) || Object, router_1.RouteParams, router_1.Router, (typeof (_b = typeof notification_service_1.NotificationService !== 'undefined' && notification_service_1.NotificationService) === 'function' && _b) || Object, (typeof (_c = typeof alert_service_1.AlertService !== 'undefined' && alert_service_1.AlertService) === 'function' && _c) || Object])
                ], GamePageComponent);
                return GamePageComponent;
                var _a, _b, _c;
            }());
            exports_1("GamePageComponent", GamePageComponent);
        }
    }
});
