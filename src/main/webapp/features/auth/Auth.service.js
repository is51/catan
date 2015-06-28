'use strict';

angular.module('catan')
        .factory('Auth', ['$q', 'AuthToken', 'Remote', function ($q, AuthToken, Remote) {
            return {
                login: function (username, password) {
                    var deferred = $q.defer();

                    Remote.auth.login({
                        username: username,
                        password: password
                    }).then(function (response, status, headers, config) {

                        if (response.token) {
                            AuthToken.set(response.token);
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
                        deferred.resolve(response, status, headers, config);
                    }, function (response, status, headers, config) {
                        deferred.reject(response, status, headers, config);
                    });

                    return deferred.promise;
                },

                register: function (username, password) {
                    return Remote.auth.register({
                        username: username,
                        password: password
                    });
                }
            };
        }]);