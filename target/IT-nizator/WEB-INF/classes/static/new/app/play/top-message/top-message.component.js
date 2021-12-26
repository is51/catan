System.register(['angular2/core', 'app/shared/services/auth/auth-user.service'], function(exports_1, context_1) {
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
    var core_1, auth_user_service_1;
    var DISPLAYING_MESSAGES_INTERVAL, TopMessageComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            }],
        execute: function() {
            DISPLAYING_MESSAGES_INTERVAL = 2000;
            TopMessageComponent = (function () {
                function TopMessageComponent(_authUser) {
                    this._authUser = _authUser;
                    this.text = null;
                    this.messageBoxWidth = 0;
                    this.viewBoxWidth = 700;
                    this._displayedMessage = null;
                    this._messagesQueue = [];
                    this._isDisplayingCycleExecuting = false;
                }
                TopMessageComponent.prototype.ngOnInit = function () {
                    var _this = this;
                    var currentPlayer = this.game.getCurrentPlayer(this._authUser.get());
                    var lastLog = (currentPlayer.log.length) ? currentPlayer.log[0] : null;
                    if (lastLog && lastLog.displayedOnTop) {
                        this._addMessageToQueue(lastLog.message);
                        if (currentPlayer.displayedMessage) {
                            this._displayedMessage = currentPlayer.displayedMessage;
                        }
                    }
                    else if (currentPlayer.displayedMessage) {
                        this._show(currentPlayer.displayedMessage);
                    }
                    currentPlayer.onDisplayedMessageUpdate(function (text) {
                        _this._displayedMessage = text;
                        if (!_this._isDisplayingCycleExecuting) {
                            _this._show(_this._displayedMessage);
                        }
                    }, function () {
                        _this._displayedMessage = null;
                        if (!_this._isDisplayingCycleExecuting) {
                            _this._hide();
                        }
                    });
                    currentPlayer.onDisplayedLogUpdate(function (newLogItems) {
                        newLogItems.forEach(function (item) { return _this._addMessageToQueue(item.message); });
                    });
                };
                TopMessageComponent.prototype._addMessageToQueue = function (text) {
                    this._messagesQueue.push(text);
                    if (!this._isDisplayingCycleExecuting) {
                        this._executeDisplayingCycle();
                    }
                };
                TopMessageComponent.prototype._executeDisplayingCycle = function () {
                    var _this = this;
                    if (this._messagesQueue.length) {
                        this._isDisplayingCycleExecuting = true;
                        var message = this._messagesQueue.shift();
                        this._show(message);
                        setTimeout(function () { return _this._executeDisplayingCycle(); }, DISPLAYING_MESSAGES_INTERVAL);
                    }
                    else {
                        this._isDisplayingCycleExecuting = false;
                        if (this._displayedMessage) {
                            this._show(this._displayedMessage);
                        }
                    }
                };
                TopMessageComponent.prototype._show = function (text) {
                    this.text = text;
                    this.messageBoxWidth = text.length * 14 + 10;
                };
                TopMessageComponent.prototype._hide = function () {
                    this.text = null;
                };
                TopMessageComponent.prototype.ngOnDestroy = function () {
                    var currentPlayer = this.game.getCurrentPlayer(this._authUser.get());
                    currentPlayer.cancelOnDisplayedMessageUpdate();
                    currentPlayer.cancelOnDisplayedLogUpdate();
                };
                TopMessageComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-top-message',
                        templateUrl: 'app/play/top-message/top-message.component.html',
                        styleUrls: ['app/play/top-message/top-message.component.css'],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _a) || Object])
                ], TopMessageComponent);
                return TopMessageComponent;
                var _a;
            }());
            exports_1("TopMessageComponent", TopMessageComponent);
        }
    }
});
