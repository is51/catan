'use strict';

angular.module('catan')
    .factory('Auth', ['$q', 'AuthToken', 'Remote', 'User', function ($q, AuthToken, Remote, User) {
        return {
            login: function (username, password) {
                var deferred = $q.defer();

                Remote.auth.login({
                    username: username,
                    password: password
                }).then(function (response, status, headers, config) {

                    if (response.data && response.data.token) {

                        AuthToken.set(response.data.token);
                        User.load();

                        deferred.resolve(response, status, headers, config);
                    } else {
                        deferred.reject(response, 400, headers, config);
                    }
                }, function (response, status, headers, config) {
                    deferred.reject(response, status, headers, config);
                });

                return deferred.promise;
            },

            logout: function () {
                var deferred = $q.defer();

                Remote.auth.logout().then(function (response, status, headers, config) {
                    AuthToken.delete();
                    User.setToGuest();
                    deferred.resolve(response, status, headers, config);
                }, function (response, status, headers, config) {
                    deferred.reject(response, status, headers, config);
                });

                return deferred.promise;
            },

            registerAndLoginGuest: function(username) {
                var deferred = $q.defer();

                Remote.auth.registerAndLoginGuest({
                    username: username
                }).then(function (response, status, headers, config) {

                    if (response.data && response.data.token) {

                        AuthToken.set(response.data.token);
                        User.load();

                        deferred.resolve(response, status, headers, config);
                    } else {
                        deferred.reject(response, 400, headers, config);
                    }
                }, function (response, status, headers, config) {
                    deferred.reject(response, status, headers, config);
                });

                return deferred.promise;
            },

            // TODO: remove this method and replace everywhere with Remote
            register: function (username, password) {
                return Remote.auth.register({
                    username: username,
                    password: password
                });
            }
        };

    }]);