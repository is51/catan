System.register(['app/shared/domain/player/player', 'app/shared/domain/game-map/game-map', 'app/shared/domain/dice'], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var player_1, game_map_1, dice_1;
    var Game;
    return {
        setters:[
            function (player_1_1) {
                player_1 = player_1_1;
            },
            function (game_map_1_1) {
                game_map_1 = game_map_1_1;
            },
            function (dice_1_1) {
                dice_1 = dice_1_1;
            }],
        execute: function() {
            Game = (function () {
                function Game(params) {
                    this.gameId = params.gameId;
                    this.creatorId = params.creatorId;
                    this.privateGame = params.privateGame;
                    this.privateCode = params.privateCode;
                    this.dateCreated = params.dateCreated;
                    this.maxPlayers = params.maxPlayers;
                    this.minPlayers = params.minPlayers;
                    this.targetVictoryPoints = params.targetVictoryPoints;
                    this.status = params.status;
                    this.dateStarted = params.dateStarted;
                    this.currentMove = params.currentMove;
                    this.biggestArmyOwnerId = params.biggestArmyOwnerId;
                    this.longestWayOwnerId = params.longestWayOwnerId;
                    this.players = params.gameUsers.map(function (playerParams) { return new player_1.Player(playerParams); });
                    this.dice = new dice_1.Dice(params.dice);
                    if (params.map) {
                        this.map = new game_map_1.GameMap(params.map);
                    }
                }
                Game.prototype.update = function (params) {
                    //TODO: revise this method
                    //this.gameId = params.gameId;
                    var _this = this;
                    this.creatorId = params.creatorId;
                    this.privateGame = params.privateGame;
                    this.privateCode = params.privateCode;
                    this.dateCreated = params.dateCreated;
                    this.maxPlayers = params.maxPlayers;
                    this.minPlayers = params.minPlayers;
                    this.targetVictoryPoints = params.targetVictoryPoints;
                    this.dateStarted = params.dateStarted;
                    this.currentMove = params.currentMove;
                    this.biggestArmyOwnerId = params.biggestArmyOwnerId;
                    this.longestWayOwnerId = params.longestWayOwnerId;
                    params.gameUsers.forEach(function (playerParams) {
                        var player = _this.players.filter(function (player) { return player.id === playerParams.id; })[0];
                        if (player) {
                            player.update(playerParams);
                        }
                        else {
                            _this.players.push(new player_1.Player(playerParams));
                        }
                    });
                    var _loop_1 = function(i) {
                        var playerParams = params.gameUsers.filter(function (playerParams) { return playerParams.id === _this.players[i].id; })[0];
                        if (!playerParams) {
                            this_1.players.splice(i, 1);
                            i--;
                        }
                        out_i_1 = i;
                    };
                    var out_i_1;
                    var this_1 = this;
                    for (var i = 0; i < this.players.length; i++) {
                        _loop_1(i);
                        i = out_i_1;
                    }
                    this.dice.update(params.dice);
                    if (params.map) {
                        if (this.map) {
                            this.map.update(params.map);
                        }
                        else {
                            this.map = new game_map_1.GameMap(params.map);
                        }
                    }
                    else {
                        this.map = null;
                    }
                    if (this.status !== params.status) {
                        this.status = params.status;
                        if (this.isPlaying()) {
                            this.triggerStartPlaying();
                        }
                    }
                };
                Game.prototype.getId = function () {
                    return this.gameId;
                };
                Game.prototype.isNew = function () {
                    return this.status === "NEW";
                };
                Game.prototype.isPlaying = function () {
                    return this.status === "PLAYING";
                };
                Game.prototype.isFinished = function () {
                    return this.status === "FINISHED";
                };
                Game.prototype.getCurrentPlayer = function (currentUser) {
                    return this.players.filter(function (player) { return player.user.id === currentUser.id; })[0];
                };
                Game.prototype.getMovingPlayer = function () {
                    var _this = this;
                    return this.players.filter(function (player) { return player.moveOrder === _this.currentMove; })[0];
                };
                Game.prototype.getPlayer = function (playerId) {
                    return this.players.filter(function (player) { return player.id === playerId; })[0];
                };
                //TODO: try to replace with Subscribable
                Game.prototype.onStartPlaying = function (onStartPlaying) {
                    this._onStartPlaying = onStartPlaying;
                };
                Game.prototype.cancelOnStartPlaying = function () {
                    this._onStartPlaying = undefined;
                };
                Game.prototype.triggerStartPlaying = function () {
                    if (this._onStartPlaying) {
                        this._onStartPlaying();
                    }
                };
                return Game;
            }());
            exports_1("Game", Game);
        }
    }
});
