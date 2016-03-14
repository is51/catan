System.register(['angular2/core', 'angular2/router', 'app/shared/services/auth/auth-user.service', 'app/shared/services/remote/remote.service'], function(exports_1, context_1) {
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
    var core_1, router_1, auth_user_service_1, remote_service_1;
    var CreateGameFormComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (router_1_1) {
                router_1 = router_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (remote_service_1_1) {
                remote_service_1 = remote_service_1_1;
            }],
        execute: function() {
            CreateGameFormComponent = (function () {
                function CreateGameFormComponent(_authUser, _remote, _router) {
                    this._authUser = _authUser;
                    this._remote = _remote;
                    this._router = _router;
                    this.privateGame = true;
                    this.targetVictoryPoints = 12;
                    this.initialBuildingsSetId = 1;
                    this.initialBuildingsSetIdValues = [
                        { value: 1, name: "2 settlements + 2 roads" },
                        { value: 2, name: "1 city + 2 settlements + 3 roads" }
                    ];
                }
                CreateGameFormComponent.prototype.submit = function () {
                    if (this._authUser.isAuthorized()) {
                        if (!this.privateGame && this._authUser.isTypeGuest()) {
                            alert("Guest can't create public game. You should register. Registration from guest to regular user is NOT IMPLEMENTED");
                        }
                        else {
                            this._createGame();
                        }
                    }
                    if (this._authUser.isNotAuthorized()) {
                        if (this.privateGame) {
                            this._router.navigate(['RegisterGuestPage']);
                        }
                        else {
                            this._router.navigate(['LoginPage']);
                        }
                    }
                    return false;
                    // TODO: find a way to avoid "return false".
                    // Remove <form> everywhere and replace <input type=submit> with <button> ???
                };
                /*
                 function goBack() {
                    $state.go('createGame', {data: scope.data});
                 }
            
                 function goBackAndCreateGame() {
                    goBack();
                    createGame();
                 }
                 */
                CreateGameFormComponent.prototype._createGame = function () {
                    var _this = this;
                    this._remote.request('game.create', {
                        privateGame: this.privateGame,
                        targetVictoryPoints: this.targetVictoryPoints,
                        initialBuildingsSetId: this.initialBuildingsSetId
                    })
                        .then(function (data) { return _this._router.navigate(['GamePage', { gameId: data.gameId }]); })
                        .catch(function (data) { return alert('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')); });
                };
                CreateGameFormComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-create-game-form',
                        templateUrl: 'app/menu/create-game-page/create-game-form/create-game-form.component.html'
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object, (typeof (_b = typeof remote_service_1.RemoteService !== 'undefined' && remote_service_1.RemoteService) === 'function' && _b) || Object, router_1.Router])
                ], CreateGameFormComponent);
                return CreateGameFormComponent;
                var _a, _b;
            }());
            exports_1("CreateGameFormComponent", CreateGameFormComponent);
        }
    }
});
