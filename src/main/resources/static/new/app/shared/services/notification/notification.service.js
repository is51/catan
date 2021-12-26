System.register(['angular2/core', 'app/shared/services/application-active/application-active.service'], function(exports_1, context_1) {
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
    var core_1, application_active_service_1;
    var ICONS_PATH, ICONS, NotificationService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (application_active_service_1_1) {
                application_active_service_1 = application_active_service_1_1;
            }],
        execute: function() {
            ICONS_PATH = 'resources/notification/';
            ICONS = {
                DEFAULT: ICONS_PATH + 'default.png',
                THROW_DICE: ICONS_PATH + 'throw-dice.png',
                TRADE_REPLY: ICONS_PATH + 'trade-reply.png',
                KICK_OFF_RESOURCES: ICONS_PATH + 'kick-off-resources.png',
                GAME_IS_STARTED: ICONS_PATH + 'game-is-started.png'
            };
            NotificationService = (function () {
                function NotificationService(_appActive) {
                    this._appActive = _appActive;
                }
                NotificationService.prototype.notify = function (message, iconCode, tag, showOnlyIfAppIsNotFocused) {
                    if (iconCode === void 0) { iconCode = 'DEFAULT'; }
                    if (showOnlyIfAppIsNotFocused === void 0) { showOnlyIfAppIsNotFocused = true; }
                    if (showOnlyIfAppIsNotFocused && this._appActive.isActive()) {
                        return Promise.reject('APP_IS_FOCUSED');
                    }
                    var icon = ICONS[iconCode];
                    return new Promise(function (resolve, reject) {
                        Notification.requestPermission(function (permission) {
                            if (permission === "granted") {
                                var notification_1 = new Notification(message, {
                                    tag: tag,
                                    icon: icon
                                });
                                notification_1.onclick = function () { return notification_1.close(); }; //TODO: make close when user click inside app window or window.focus
                                resolve(notification_1);
                            }
                            else {
                                reject(permission);
                            }
                        });
                    });
                };
                NotificationService.prototype.notifyGlobal = function (message, iconCode) {
                    this.notify(message, iconCode, 'GLOBAL');
                };
                NotificationService.prototype.requestPermission = function () {
                    return Notification.requestPermission();
                };
                NotificationService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof application_active_service_1.ApplicationActiveService !== 'undefined' && application_active_service_1.ApplicationActiveService) === 'function' && _a) || Object])
                ], NotificationService);
                return NotificationService;
                var _a;
            }());
            exports_1("NotificationService", NotificationService);
        }
    }
});
