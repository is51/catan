'use strict';

angular.module('catan')
        .factory('GameModel', ['$q', 'Remote', 'User', '$timeout', function ($q, Remote, User, $timeout) {

            var GAME_PRIMARY_KEY = "gameId";

            var STATUS_NOT_LOADED = 0,
                STATUS_LOADED = 1;

            return function(idOrDetails) {

                var id,
                    status = STATUS_NOT_LOADED,
                    details = {},
                    updatingTimeout = null;

                if (typeof idOrDetails === "number") {
                    id = idOrDetails;
                } else if (typeof idOrDetails === "object") {
                    id = idOrDetails[GAME_PRIMARY_KEY];
                    details = idOrDetails;
                    status = STATUS_LOADED;
                }

                return {
                    isLoaded: function() {
                        return status === STATUS_LOADED;
                    },
                    load: function() {
                        var deferred = $q.defer();

                        Remote.game.details({gameId: id}).then(function(response) {
                            details = response.data;
                            status = STATUS_LOADED;
                            deferred.resolve();
                        }, function() {
                            deferred.reject();
                        });

                        return deferred.promise;
                    },
                    startUpdating: function(delay, onEverySuccess, onEveryError) {
                        var self = this;
                        this.stopUpdating();

                        (function runTimeout() {
                            updatingTimeout = $timeout(function() {
                                self.load().then(function() {
                                    if (onEverySuccess) {
                                        onEverySuccess();
                                    }
                                    runTimeout();
                                }, function() {
                                    if (onEveryError) {
                                        onEveryError();
                                    }
                                    runTimeout();
                                });
                            }, delay);
                        })();
                    },
                    stopUpdating: function() {
                        $timeout.cancel(updatingTimeout);
                    },
                    get: function() {
                        return details;
                    },
                    getId: function() {
                        return id;
                    },
                    isNew: function() {
                        return details.status === "NEW";
                    },
                    isPlaying: function() {
                        return details.status === "PLAYING";
                    },
                    isCurrentUserCreator: function() {
                        return details.creatorId === User.get().id;
                    },
                    getCurrentUser: function() {
                        for (var i in details.gameUsers) {
                            if (details.gameUsers[i].user.id === User.get().id) {
                                return details.gameUsers[i];
                            }
                        }
                        return false;
                    },
                    isCurrentUserReady: function() {
                        var user = this.getCurrentUser();
                        return (user) ? user.ready : undefined;
                    }
                };
            };
        }]);
