System.register(['angular2/core', 'app/play/shared/services/select.service', 'app/shared/services/auth/auth-user.service', 'app/shared/domain/player/resources'], function(exports_1, context_1) {
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
    var core_1, select_service_1, auth_user_service_1, resources_1;
    var ChooseResourcesComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (select_service_1_1) {
                select_service_1 = select_service_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (resources_1_1) {
                resources_1 = resources_1_1;
            }],
        execute: function() {
            //TODO: this directive should be refactored
            ChooseResourcesComponent = (function () {
                function ChooseResourcesComponent(_select, _authUser) {
                    this._select = _select;
                    this._authUser = _authUser;
                    this.resources = new resources_1.Resources();
                    this._tradeBalance = 0;
                }
                ChooseResourcesComponent.prototype.ngOnInit = function () {
                    var currentPlayer = this.game.getCurrentPlayer(this._authUser.get());
                    this._playerResources = currentPlayer.resources;
                    if (this.type === "TRADE_PORT") {
                        this._tradePortRatio = new resources_1.Resources(currentPlayer.availableActions.getParams("TRADE_PORT"));
                    }
                    this._maxResourcesCountLimit = this._getMaxResourcesCountLimit(this.type);
                    this._maxResourcesCountForApply = this._getMaxResourcesCountForApply(this.type, this._playerResources);
                    this._minResourcesCountLimit = this._getMinResourcesCountLimit(this.type, this._playerResources);
                    this._minResourcesCountForApply = this._getMinResourcesCountForApply(this.type, this._playerResources);
                    this._removeOtherResourceWhenLimit = this._getRemoveOtherResourceWhenLimit(this.type);
                    this.balanceType = this._getBalanceType(this.type);
                };
                ChooseResourcesComponent.prototype.addResource = function (resourceType) {
                    var step = 1;
                    if (this.type === "TRADE_PORT" && this.resources[resourceType] < 0) {
                        step = this._tradePortRatio[resourceType];
                    }
                    if (this.resources.getTotalCount() + step <= this._maxResourcesCountLimit || this._maxResourcesCountLimit === null) {
                        this.resources[resourceType] += step;
                    }
                    else if (this._removeOtherResourceWhenLimit && step === 1) {
                        if (this.removeOtherResource(resourceType)) {
                            this.resources[resourceType]++;
                        }
                    }
                    if (this.type === "TRADE_PORT") {
                        this._tradeBalance = this._recalculateTradeBalance(this.resources, this._tradePortRatio);
                    }
                };
                ChooseResourcesComponent.prototype.removeResource = function (resourceType) {
                    var step = 1;
                    if (this.type === "TRADE_PORT" && this.resources[resourceType] <= 0) {
                        step = this._tradePortRatio[resourceType];
                    }
                    if (this.resources.getTotalCount() - step >= this._minResourcesCountLimit &&
                        this._playerResources[resourceType] - step + this.resources[resourceType] >= 0) {
                        this.resources[resourceType] -= step;
                    }
                    if (this.type === "TRADE_PORT") {
                        this._tradeBalance = this._recalculateTradeBalance(this.resources, this._tradePortRatio);
                    }
                };
                ChooseResourcesComponent.prototype.removeOtherResource = function (resourceType) {
                    for (var i in this.resources) {
                        if (i !== resourceType && this.resources[i] > 0) {
                            this.resources[i]--;
                            return true;
                        }
                    }
                    return false;
                };
                ChooseResourcesComponent.prototype.okDisabled = function () {
                    var count = this.resources.getTotalCount();
                    if (this.type === "TRADE_PROPOSE" && this._noNegativeOrPositiveResources(this.resources)) {
                        return true;
                    }
                    return this.resources.areAllCountsZero()
                        || count < this._minResourcesCountForApply
                        || (count > this._maxResourcesCountForApply && this._maxResourcesCountForApply !== null)
                        || this._tradeBalance !== 0;
                };
                ChooseResourcesComponent.prototype.cancel = function () {
                    this._select.cancelRequestSelection(this.type);
                };
                ChooseResourcesComponent.prototype.ok = function () {
                    this._select.select(this.type, this._convertResourcesToSelection(this.type, this.resources));
                };
                ChooseResourcesComponent.prototype._convertResourcesToSelection = function (type, resources) {
                    switch (type) {
                        case "CARD_YEAR_OF_PLENTY":
                            var firstResource = void 0;
                            var secondResource = void 0;
                            for (var i in resources) {
                                if (resources[i] > 0) {
                                    firstResource = i.toUpperCase();
                                    resources[i]--;
                                    break;
                                }
                            }
                            for (var i in resources) {
                                if (resources[i] > 0) {
                                    secondResource = i.toUpperCase();
                                }
                            }
                            return {
                                firstResource: firstResource,
                                secondResource: secondResource
                            };
                        case "CARD_MONOPOLY":
                            for (var i in resources) {
                                if (resources[i] > 0) {
                                    return { resource: i.toUpperCase() };
                                }
                            }
                            return;
                        case "KICK_OFF_RESOURCES":
                            return {
                                brick: -resources.brick,
                                wood: -resources.wood,
                                sheep: -resources.sheep,
                                wheat: -resources.wheat,
                                stone: -resources.stone
                            };
                        case "TRADE_PORT":
                        case "TRADE_PROPOSE":
                            return {
                                brick: resources.brick,
                                wood: resources.wood,
                                sheep: resources.sheep,
                                wheat: resources.wheat,
                                stone: resources.stone
                            };
                    }
                };
                ChooseResourcesComponent.prototype._getMaxResourcesCountLimit = function (type) {
                    switch (type) {
                        case "CARD_YEAR_OF_PLENTY":
                            return 2;
                        case "CARD_MONOPOLY":
                            return 1;
                        case "KICK_OFF_RESOURCES":
                            return 0;
                        case "TRADE_PORT":
                            return null; //unlimited
                        case "TRADE_PROPOSE":
                            return null; //unlimited
                    }
                };
                ChooseResourcesComponent.prototype._getMaxResourcesCountForApply = function (type, playerResources) {
                    switch (type) {
                        case "CARD_YEAR_OF_PLENTY":
                            return 2;
                        case "CARD_MONOPOLY":
                            return 1;
                        case "KICK_OFF_RESOURCES":
                            return -this._calculateCountForKickOff(playerResources);
                        case "TRADE_PORT":
                            return null; //unlimited
                        case "TRADE_PROPOSE":
                            return null; //unlimited
                    }
                };
                ChooseResourcesComponent.prototype._getMinResourcesCountLimit = function (type, playerResources) {
                    switch (type) {
                        case "CARD_YEAR_OF_PLENTY":
                            return 0;
                        case "CARD_MONOPOLY":
                            return 0;
                        case "KICK_OFF_RESOURCES":
                            return -this._calculateCountForKickOff(playerResources);
                        case "TRADE_PORT":
                            return -playerResources.getTotalCount();
                        case "TRADE_PROPOSE":
                            return -playerResources.getTotalCount();
                    }
                };
                ChooseResourcesComponent.prototype._getMinResourcesCountForApply = function (type, playerResources) {
                    switch (type) {
                        case "CARD_YEAR_OF_PLENTY":
                            return 2;
                        case "CARD_MONOPOLY":
                            return 1;
                        case "KICK_OFF_RESOURCES":
                            return -this._calculateCountForKickOff(playerResources);
                        case "TRADE_PORT":
                            return -playerResources.getTotalCount();
                        case "TRADE_PROPOSE":
                            return -playerResources.getTotalCount();
                    }
                };
                ChooseResourcesComponent.prototype._getRemoveOtherResourceWhenLimit = function (type) {
                    switch (type) {
                        case "CARD_YEAR_OF_PLENTY":
                            return true; //TODO: or 'false' for 'year of plenty'?
                        case "CARD_MONOPOLY":
                            return true;
                        case "KICK_OFF_RESOURCES":
                            return false;
                        case "TRADE_PORT":
                            return false;
                        case "TRADE_PROPOSE":
                            return false;
                    }
                };
                ChooseResourcesComponent.prototype._getBalanceType = function (type) {
                    switch (type) {
                        case "CARD_YEAR_OF_PLENTY":
                            return "POSITIVE";
                        case "CARD_MONOPOLY":
                            return "POSITIVE";
                        case "KICK_OFF_RESOURCES":
                            return "NEGATIVE";
                        case "TRADE_PORT":
                            return "BOTH";
                        case "TRADE_PROPOSE":
                            return "BOTH";
                    }
                };
                ChooseResourcesComponent.prototype._calculateCountForKickOff = function (resources) {
                    return Math.floor(resources.getTotalCount() / 2);
                };
                ChooseResourcesComponent.prototype._recalculateTradeBalance = function (resources, tradePortRatio) {
                    var tradeBalance = 0;
                    for (var i in resources) {
                        var count = resources[i];
                        var ratio = tradePortRatio[i];
                        if (count > 0) {
                            tradeBalance += count;
                        }
                        if (count < 0) {
                            tradeBalance += count / ratio;
                        }
                    }
                    return tradeBalance;
                };
                ChooseResourcesComponent.prototype._noNegativeOrPositiveResources = function (resources) {
                    var isNegative = false;
                    var isPositive = false;
                    for (var i in resources) {
                        if (resources[i] > 0) {
                            isPositive = true;
                        }
                        if (resources[i] < 0) {
                            isNegative = true;
                        }
                    }
                    return !isPositive || !isNegative;
                };
                ChooseResourcesComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-choose-resources',
                        templateUrl: 'app/play/shared/choose-resources/choose-resources.component.html',
                        styleUrls: ['app/play/shared/choose-resources/choose-resources.component.css'],
                        inputs: ['game', 'type']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof select_service_1.SelectService !== 'undefined' && select_service_1.SelectService) === 'function' && _a) || Object, (typeof (_b = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _b) || Object])
                ], ChooseResourcesComponent);
                return ChooseResourcesComponent;
                var _a, _b;
            }());
            exports_1("ChooseResourcesComponent", ChooseResourcesComponent);
        }
    }
});
