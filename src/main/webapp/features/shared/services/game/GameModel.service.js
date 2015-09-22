'use strict';

angular.module('catan')
        .factory('GameModel', ['User', function (User) {

            var GAME_PRIMARY_KEY = "gameId";

            return function(data) {

                angular.copy(data, this);

                this.getId = function() {
                    return this[GAME_PRIMARY_KEY];
                };

                this.isNew = function() {
                    return this.status === "NEW";
                };

                this.isPlaying = function() {
                    return this.status === "PLAYING";
                };

                this.isCurrentUserCreator = function() {
                    return this.creatorId === User.get().id;
                };

                this.getCurrentUser = function() {
                    for (var i in this.gameUsers) {
                        if (this.gameUsers[i].user.id === User.get().id) {
                            return this.gameUsers[i];
                        }
                    }
                    return null;
                };

                this.isCurrentUserReady = function() {
                    var user = this.getCurrentUser();
                    return (user) ? user.ready : false;
                };
            };
        }]);
