'use strict';

angular.module('catan')
        .factory('GameModel', ['User', 'GameActionsService', 'LinkMapEntitiesHelper', function (User, GameActionsService, LinkMapEntitiesHelper) {

            var GAME_PRIMARY_KEY = "gameId";

            return function (data) {

                angular.copy(data, this);
                LinkMapEntitiesHelper.linkNeighbourEntitiesByIds(this.map);

                this.getId = function () {
                    return this[GAME_PRIMARY_KEY];
                };

                this.isNew = function () {
                    return this.status === "NEW";
                };

                this.isPlaying = function () {
                    return this.status === "PLAYING";
                };

                this.isFinished = function () {
                    return this.status === "FINISHED";
                };

                this.getCurrentUser = function () {
                    for (var i in this.gameUsers) {
                        if (this.gameUsers[i].user.id === User.get().id) {
                            return this.gameUsers[i];
                        }
                    }
                    return null;
                };

                this.isCurrentUserCreator = function () {
                    return this.creatorId === User.get().id;
                };

                this.isCurrentUserReady = function () {
                    var user = this.getCurrentUser();
                    return (user) ? user.ready : false;
                };

                this.isCurrentUserMove = function () {
                    return this.currentMove === this.getCurrentUser().moveOrder;
                };

                this.isActionEnabledForCurrentUser = function (actionCode) {
                    return GameActionsService.isActionEnableForUser(this.getCurrentUser(), actionCode);
                };

                this.isActionGroupEnabledForCurrentUser = function (actionGroupCode) {
                    return GameActionsService.isActionGroupEnableForUser(this.getCurrentUser(), actionGroupCode);
                };

                this.getGameUser = function(gameUserId) {
                    for (var i in this.gameUsers) {
                        if (this.gameUsers[i].id === gameUserId) {
                            return this.gameUsers[i];
                        }
                    }
                    return null;
                };

                this.getMapObjectById = function(type, id) {
                    var arrayName = type + ((type === 'hex') ? 'es' : 's'),
                        array = this.map[arrayName],
                        primaryKey = type + 'Id',
                        elem,
                        i,
                        l;

                    for (i = 0, l = array.length; i < l; i++) {
                        elem = array[i];
                        if (elem[primaryKey] === id) {
                            return elem;
                        }
                    }
                    return null;
                }

            };
        }]);
