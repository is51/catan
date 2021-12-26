System.register(['angular2/core', 'app/shared/services/remote/remote.service', 'app/shared/services/auth/auth-user.service', './select.service', './marking.service', 'app/shared/modal-window/modal-window.service'], function(exports_1, context_1) {
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
    var core_1, remote_service_1, auth_user_service_1, select_service_1, marking_service_1, modal_window_service_1;
    var PlayService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (remote_service_1_1) {
                remote_service_1 = remote_service_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (select_service_1_1) {
                select_service_1 = select_service_1_1;
            },
            function (marking_service_1_1) {
                marking_service_1 = marking_service_1_1;
            },
            function (modal_window_service_1_1) {
                modal_window_service_1 = modal_window_service_1_1;
            }],
        execute: function() {
            PlayService = (function () {
                function PlayService(_remote, _authUser, _select, _marking, _modalWindow) {
                    this._remote = _remote;
                    this._authUser = _authUser;
                    this._select = _select;
                    this._marking = _marking;
                    this._modalWindow = _modalWindow;
                }
                PlayService.prototype.endTurn = function (game) {
                    this._beforeAnyAction();
                    return this._remote.request('play.endTurn', { gameId: game.getId() });
                };
                PlayService.prototype.throwDice = function (game) {
                    this._beforeAnyAction();
                    return this._remote.request('play.throwDice', { gameId: game.getId() });
                };
                PlayService.prototype.buyCard = function (game) {
                    this._beforeAnyAction();
                    return this._remote.request('play.buyCard', { gameId: game.getId() });
                };
                PlayService.prototype.buildSettlement = function (game) {
                    var _this = this;
                    this._beforeAnyAction();
                    return new Promise(function (resolve, reject) {
                        var currentPlayer = game.getCurrentPlayer(_this._authUser.get());
                        var availableNodes = currentPlayer.availableActions.getParams("BUILD_SETTLEMENT").nodeIds;
                        if (availableNodes.length === 0) {
                            reject("NO_AVAILABLE_PLACES");
                            return;
                        }
                        _this._marking.mark('nodes', availableNodes, currentPlayer);
                        _this._select.requestSelection('node')
                            .then(function (nodeId) {
                            _this._remote.request('play.buildSettlement', {
                                gameId: game.getId(),
                                nodeId: nodeId
                            }).then(function (data) {
                                resolve(data);
                                _this._marking.clear('nodes');
                            }).catch(function (data) {
                                reject(data);
                                _this._marking.clear('nodes');
                            });
                        })
                            .catch(function (data) {
                            reject(data);
                            _this._marking.clear('nodes');
                        });
                    });
                };
                PlayService.prototype.buildCity = function (game) {
                    var _this = this;
                    this._beforeAnyAction();
                    return new Promise(function (resolve, reject) {
                        var currentPlayer = game.getCurrentPlayer(_this._authUser.get());
                        var availableNodes = currentPlayer.availableActions.getParams("BUILD_CITY").nodeIds;
                        if (availableNodes.length === 0) {
                            reject("NO_AVAILABLE_PLACES");
                            return;
                        }
                        _this._marking.mark('nodes', availableNodes, currentPlayer);
                        _this._select.requestSelection('node')
                            .then(function (nodeId) {
                            _this._remote.request('play.buildCity', {
                                gameId: game.getId(),
                                nodeId: nodeId
                            }).then(function () {
                                resolve();
                                _this._marking.clear('nodes');
                            }).catch(function (data) {
                                reject(data);
                                _this._marking.clear('nodes');
                            });
                        })
                            .catch(function (data) {
                            reject(data);
                            _this._marking.clear('nodes');
                        });
                    });
                };
                PlayService.prototype.buildRoad = function (game) {
                    var _this = this;
                    this._beforeAnyAction();
                    return new Promise(function (resolve, reject) {
                        var currentPlayer = game.getCurrentPlayer(_this._authUser.get());
                        var availableEdges = currentPlayer.availableActions.getParams("BUILD_ROAD").edgeIds;
                        if (availableEdges.length === 0) {
                            reject("NO_AVAILABLE_PLACES");
                            return;
                        }
                        _this._marking.mark('edges', availableEdges, currentPlayer);
                        _this._select.requestSelection('edge')
                            .then(function (edgeId) {
                            _this._remote.request('play.buildRoad', {
                                gameId: game.getId(),
                                edgeId: edgeId
                            }).then(function () {
                                resolve();
                                _this._marking.clear('edges');
                            }).catch(function (data) {
                                reject(data);
                                _this._marking.clear('edges');
                            });
                        })
                            .catch(function (data) {
                            reject(data);
                            _this._marking.clear('edges');
                        });
                    });
                };
                PlayService.prototype.useCardYearOfPlenty = function (game) {
                    var _this = this;
                    this._beforeAnyAction();
                    return new Promise(function (resolve, reject) {
                        var windowAndSelectionId = "CARD_YEAR_OF_PLENTY";
                        _this._modalWindow.show(windowAndSelectionId);
                        _this._select.requestSelection(windowAndSelectionId)
                            .then(function (response) {
                            _this._remote.request('play.useCardYearOfPlenty', {
                                gameId: game.getId(),
                                firstResource: response.firstResource,
                                secondResource: response.secondResource
                            })
                                .then(function () { return resolve(); })
                                .catch(function (response) { return reject(response); });
                            _this._modalWindow.hide(windowAndSelectionId);
                        })
                            .catch(function (response) {
                            reject(response);
                            _this._modalWindow.hide(windowAndSelectionId);
                        });
                    });
                };
                PlayService.prototype.useCardRoadBuilding = function (game) {
                    this._beforeAnyAction();
                    return this._remote.request('play.useCardRoadBuilding', { gameId: game.getId() });
                };
                PlayService.prototype.useCardMonopoly = function (game) {
                    var _this = this;
                    this._beforeAnyAction();
                    return new Promise(function (resolve, reject) {
                        var windowAndSelectionId = "CARD_MONOPOLY";
                        _this._modalWindow.show(windowAndSelectionId);
                        _this._select.requestSelection(windowAndSelectionId)
                            .then(function (response) {
                            _this._remote.request('play.useCardMonopoly', {
                                gameId: game.getId(),
                                resource: response.resource
                            })
                                .then(function (data) { return resolve(data); })
                                .catch(function (data) { return reject(data); });
                            _this._modalWindow.hide(windowAndSelectionId);
                        })
                            .catch(function (response) {
                            reject(response);
                            _this._modalWindow.hide(windowAndSelectionId);
                        });
                    });
                };
                PlayService.prototype.useCardKnight = function (game) {
                    this._beforeAnyAction();
                    return this._remote.request('play.useCardKnight', { gameId: game.getId() });
                };
                PlayService.prototype.moveRobber = function (game) {
                    var _this = this;
                    this._beforeAnyAction();
                    return new Promise(function (resolve, reject) {
                        var currentPlayer = game.getCurrentPlayer(_this._authUser.get());
                        var availableHexes = currentPlayer.availableActions.getParams("MOVE_ROBBER").hexIds;
                        _this._marking.mark('hexes', availableHexes);
                        _this._select.requestSelection('hex')
                            .then(function (hexId) {
                            _this._remote.request('play.moveRobber', {
                                gameId: game.getId(),
                                hexId: hexId
                            })
                                .then(function () {
                                resolve();
                                _this._marking.clear('hexes');
                            })
                                .catch(function (data) {
                                reject(data);
                                _this._marking.clear('hexes');
                            });
                        })
                            .catch(function (data) {
                            reject(data);
                            _this._marking.clear('hexes');
                        });
                    });
                };
                PlayService.prototype.choosePlayerToRob = function (game) {
                    var _this = this;
                    this._beforeAnyAction();
                    return new Promise(function (resolve, reject) {
                        var currentPlayer = game.getCurrentPlayer(_this._authUser.get());
                        var availableNodes = currentPlayer.availableActions.getParams("CHOOSE_PLAYER_TO_ROB").nodeIds;
                        _this._marking.mark('nodes', availableNodes);
                        _this._select.requestSelection('node')
                            .then(function (nodeId) {
                            var node = game.map.getNodeById(nodeId);
                            if (node.building) {
                                var playerId = node.building.ownerPlayerId;
                                _this._remote.request('play.choosePlayerToRob', {
                                    gameId: game.getId(),
                                    gameUserId: playerId
                                })
                                    .then(function () {
                                    resolve();
                                    _this._marking.clear('nodes');
                                })
                                    .catch(function (data) {
                                    reject(data);
                                    _this._marking.clear('nodes');
                                });
                            }
                            else {
                                reject();
                                _this._marking.clear('nodes');
                            }
                        })
                            .catch(function (data) {
                            reject(data);
                            _this._marking.clear('nodes');
                        });
                    });
                };
                PlayService.prototype.kickOffResources = function (game) {
                    var _this = this;
                    this._beforeAnyAction();
                    return new Promise(function (resolve, reject) {
                        var windowAndSelectionId = "KICK_OFF_RESOURCES";
                        _this._modalWindow.show(windowAndSelectionId);
                        _this._select.requestSelection(windowAndSelectionId)
                            .then(function (data) {
                            _this._remote.request('play.kickOffResources', {
                                gameId: game.getId(),
                                brick: data.brick,
                                wood: data.wood,
                                sheep: data.sheep,
                                wheat: data.wheat,
                                stone: data.stone
                            })
                                .then(function (data) { return resolve(data); })
                                .catch(function (data) { return reject(data); });
                            _this._modalWindow.hide(windowAndSelectionId);
                        })
                            .catch(function (data) {
                            reject(data);
                            _this._modalWindow.hide(windowAndSelectionId);
                        });
                    });
                };
                PlayService.prototype.tradePort = function (game) {
                    var _this = this;
                    this._beforeAnyAction();
                    return new Promise(function (resolve, reject) {
                        var selectionId = "TRADE_PORT";
                        _this._select.requestSelection(selectionId)
                            .then(function (resources) {
                            _this._remote.request('play.tradePort', {
                                gameId: game.getId(),
                                brick: resources.brick,
                                wood: resources.wood,
                                sheep: resources.sheep,
                                wheat: resources.wheat,
                                stone: resources.stone
                            })
                                .then(function (data) { return resolve(data); })
                                .catch(function (data) { return reject(data); });
                        })
                            .catch(function (data) { return reject(data); });
                    });
                };
                PlayService.prototype.tradePropose = function (game) {
                    var _this = this;
                    this._beforeAnyAction();
                    return new Promise(function (resolve, reject) {
                        var selectionId = "TRADE_PROPOSE";
                        _this._select.requestSelection(selectionId)
                            .then(function (resources) {
                            _this._remote.request('play.tradePropose', {
                                gameId: game.getId(),
                                brick: resources.brick,
                                wood: resources.wood,
                                sheep: resources.sheep,
                                wheat: resources.wheat,
                                stone: resources.stone
                            })
                                .then(function (data) { return resolve(data); })
                                .catch(function (data) { return reject(data); });
                        })
                            .catch(function (data) { return reject(data); });
                    });
                };
                PlayService.prototype.tradeAccept = function (game, offerId) {
                    this._beforeAnyAction();
                    return this._remote.request('play.tradeAccept', { gameId: game.getId(), offerId: offerId });
                };
                PlayService.prototype.tradeDecline = function (game, offerId) {
                    this._beforeAnyAction();
                    return this._remote.request('play.tradeDecline', { gameId: game.getId(), offerId: offerId });
                };
                PlayService.prototype._beforeAnyAction = function () {
                    this._select.cancelAllRequestSelections();
                };
                PlayService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof remote_service_1.RemoteService !== 'undefined' && remote_service_1.RemoteService) === 'function' && _a) || Object, (typeof (_b = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _b) || Object, select_service_1.SelectService, marking_service_1.MarkingService, (typeof (_c = typeof modal_window_service_1.ModalWindowService !== 'undefined' && modal_window_service_1.ModalWindowService) === 'function' && _c) || Object])
                ], PlayService);
                return PlayService;
                var _a, _b, _c;
            }());
            exports_1("PlayService", PlayService);
        }
    }
});
