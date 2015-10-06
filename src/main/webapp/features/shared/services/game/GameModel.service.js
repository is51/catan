'use strict';

angular.module('catan')
        .factory('GameModel', ['User', 'GameActionsService', function (User, GameActionsService) {

            var GAME_PRIMARY_KEY = "gameId";

            return function (data) {

                angular.copy(data, this);

                this.getId = function () {
                    return this[GAME_PRIMARY_KEY];
                };

                this.isNew = function () {
                    return this.status === "NEW";
                };

                this.isPlaying = function () {
                    return this.status === "PLAYING";
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

            };
        }]);
