System.register(['angular2/core', 'app/shared/modal-window/modal-window.service', './play.service', 'app/shared/services/game/game.service', 'app/shared/alert/alert.service'], function(exports_1, context_1) {
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
    var core_1, modal_window_service_1, play_service_1, game_service_1, alert_service_1;
    var ExecuteActionsService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (modal_window_service_1_1) {
                modal_window_service_1 = modal_window_service_1_1;
            },
            function (play_service_1_1) {
                play_service_1 = play_service_1_1;
            },
            function (game_service_1_1) {
                game_service_1 = game_service_1_1;
            },
            function (alert_service_1_1) {
                alert_service_1 = alert_service_1_1;
            }],
        execute: function() {
            ExecuteActionsService = (function () {
                function ExecuteActionsService(_play, _gameService, _modalWindow, _alert) {
                    var _this = this;
                    this._play = _play;
                    this._gameService = _gameService;
                    this._modalWindow = _modalWindow;
                    this._alert = _alert;
                    this._executingActions = new Set();
                    this._ACTIONS = {
                        'TRADE_REPLY': function () {
                            _this._modalWindow.show("TRADE_REPLY_PANEL");
                        },
                        'KICK_OFF_RESOURCES': function (code, game) {
                            _this._executingActions.add(code);
                            _this._play.kickOffResources(game)
                                .then(function () {
                                _this._gameService.refresh(game)
                                    .then(function () { return _this._executingActions.delete(code); })
                                    .catch(function () { return _this._executingActions.delete(code); });
                            })
                                .catch(function (data) {
                                if (data !== "CANCELED") {
                                    _this._alert.message("Kick Off Resources error!");
                                }
                                _this._executingActions.delete(code);
                            });
                        },
                        'MOVE_ROBBER': function (code, game) {
                            _this._play.moveRobber(game)
                                .then(function () { return _this._gameService.refresh(game); })
                                .catch(function (data) {
                                if (data !== "CANCELED") {
                                    _this._alert.message("Move robber error!");
                                }
                            });
                        },
                        'CHOOSE_PLAYER_TO_ROB': function (code, game) {
                            _this._play.choosePlayerToRob(game)
                                .then(function () { return _this._gameService.refresh(game); })
                                .catch(function (data) {
                                if (data !== "CANCELED") {
                                    _this._alert.message("Choose Player To Rob error!");
                                }
                            });
                        },
                        'BUILD_SETTLEMENT': function (code, game) {
                            _this._play.buildSettlement(game)
                                .then(function (data) {
                                if (data.limitReached) {
                                    _this._alert.message("Settlements limit has just been reached. Build city to increase settlements count");
                                }
                                _this._gameService.refresh(game);
                            })
                                .catch(function (error) {
                                if (error === "NO_AVAILABLE_PLACES") {
                                    _this._alert.message("NO_AVAILABLE_PLACES");
                                }
                                else if (error !== "CANCELED") {
                                    _this._alert.message('Build settlement error: ' + ((error.errorCode) ? error.errorCode : 'unknown'));
                                }
                            });
                        },
                        'BUILD_CITY': function (code, game) {
                            _this._play.buildCity(game)
                                .then(function () { return _this._gameService.refresh(game); })
                                .catch(function (errorCode) {
                                if (errorCode === "NO_AVAILABLE_PLACES") {
                                    _this._alert.message("NO_AVAILABLE_PLACES");
                                }
                                else if (errorCode !== "CANCELED") {
                                    _this._alert.message("Build road error!");
                                }
                            });
                        },
                        'BUILD_ROAD': function (code, game) {
                            _this._play.buildRoad(game)
                                .then(function () { return _this._gameService.refresh(game); })
                                .catch(function (errorCode) {
                                if (errorCode === "NO_AVAILABLE_PLACES") {
                                    _this._alert.message("NO_AVAILABLE_PLACES");
                                }
                                else if (errorCode !== "CANCELED") {
                                    _this._alert.message("Build road error!");
                                }
                            });
                        },
                        'BUY_CARD': function (code, game) {
                            _this._play.buyCard(game)
                                .then(function (data) {
                                _this._alert.message("Bought card: " + data.card); //TODO: why red?
                                _this._gameService.refresh(game);
                            })
                                .catch(function (data) { return _this._alert.message('Buy Card error: ' + ((data.errorCode) ? data.errorCode : 'unknown')); });
                        },
                        'END_TURN': function (code, game) {
                            _this._executingActions.add(code);
                            _this._play.endTurn(game)
                                .then(function () {
                                _this._gameService.refresh(game)
                                    .then(function () { return _this._executingActions.delete(code); })
                                    .catch(function () { return _this._executingActions.delete(code); });
                            })
                                .catch(function (data) {
                                _this._alert.message('End turn error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                                _this._executingActions.delete(code);
                            });
                        },
                        'THROW_DICE': function (code, game) {
                            _this._play.throwDice(game)
                                .then(function () { return _this._gameService.refresh(game); })
                                .catch(function (data) { return _this._alert.message('Throw Dice error: ' + ((data.errorCode) ? data.errorCode : 'unknown')); });
                        },
                        'USE_CARD_KNIGHT': function (code, game) {
                            _this._play.useCardKnight(game)
                                .then(function () { return _this._gameService.refresh(game); })
                                .catch(function (data) { return _this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown')); });
                        },
                        'USE_CARD_ROAD_BUILDING': function (code, game) {
                            _this._play.useCardRoadBuilding(game)
                                .then(function (data) {
                                var count = data.roadsCount; //TODO: fix red?
                                _this._alert.message("Build " + count + " road" + ((count === 1) ? "" : "s"));
                                _this._gameService.refresh(game);
                            })
                                .catch(function (data) {
                                _this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                            });
                        },
                        'USE_CARD_MONOPOLY': function (code, game) {
                            _this._play.useCardMonopoly(game)
                                .then(function (data) {
                                var count = data.resourcesCount; //TODO: fix red?
                                if (count === 0) {
                                    _this._alert.message("You received " + count + " resources because players don't have this type of resource");
                                }
                                else {
                                    _this._alert.message("You received " + count + " resources");
                                }
                                _this._gameService.refresh(game);
                            })
                                .catch(function (data) {
                                if (data !== "CANCELED") {
                                    _this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                                }
                            });
                        },
                        'USE_CARD_YEAR_OF_PLENTY': function (code, game) {
                            _this._play.useCardYearOfPlenty(game)
                                .then(function () { return _this._gameService.refresh(game); })
                                .catch(function (data) {
                                if (data !== "CANCELED") {
                                    _this._alert.message('Error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                                }
                            });
                        },
                        'TRADE_PORT': function (code, game) {
                            return new Promise(function (resolve, reject) {
                                _this._play.tradePort(game)
                                    .then(function () {
                                    _this._gameService.refresh(game);
                                    resolve();
                                })
                                    .catch(function (data) {
                                    if (data !== "CANCELED") {
                                        _this._alert.message('Trade Port error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                                    }
                                    reject();
                                });
                            });
                        },
                        'TRADE_PLAYERS': function (code, game) {
                            return new Promise(function (resolve, reject) {
                                _this._play.tradePropose(game)
                                    .then(function () {
                                    _this._gameService.refresh(game);
                                    resolve();
                                })
                                    .catch(function (data) {
                                    if (data !== "CANCELED") {
                                        _this._alert.message('Trade Players Propose error: ' + ((data.errorCode) ? data.errorCode : 'unknown'));
                                    }
                                    reject();
                                });
                            });
                        }
                    };
                }
                ExecuteActionsService.prototype.execute = function (code, game) {
                    return this._ACTIONS[code](code, game);
                };
                ExecuteActionsService.prototype.isExecuting = function (code) {
                    return this._executingActions.has(code);
                };
                ExecuteActionsService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [play_service_1.PlayService, (typeof (_a = typeof game_service_1.GameService !== 'undefined' && game_service_1.GameService) === 'function' && _a) || Object, (typeof (_b = typeof modal_window_service_1.ModalWindowService !== 'undefined' && modal_window_service_1.ModalWindowService) === 'function' && _b) || Object, (typeof (_c = typeof alert_service_1.AlertService !== 'undefined' && alert_service_1.AlertService) === 'function' && _c) || Object])
                ], ExecuteActionsService);
                return ExecuteActionsService;
                var _a, _b, _c;
            }());
            exports_1("ExecuteActionsService", ExecuteActionsService);
        }
    }
});
