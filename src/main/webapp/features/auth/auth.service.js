'use strict';

angular.module('catan')
    .service('Auth', ["$http", "$q", "$window", function ($http, $q, $window) {
        var Auth = {
            getToken: function () {
                return $window.localStorage.getItem('token');
            },

            setToken: function (token) {
                $window.localStorage.setItem('token', token);
            },

            deleteToken: function () {
                $window.localStorage.removeItem('token');
            },

            login: function (username, password) {
                var deferred = $q.defer();

                $http.post('/api/user/login', {
                    login: username,
                    password: password
                }).success(function (response, status, headers, config) {
                    if (response.token) {
                        Auth.setToken(response.token);
                    }

                    deferred.resolve(response, status, headers, config);
                }).error(function (response, status, headers, config) {
                    deferred.reject(response, status, headers, config);
                });

                return deferred.promise;
            },

            logout: function () {
                var deferred = $q.defer();

                $http.post('/api/user/logout').success(function (response, status, headers, config) {
                    Auth.deleteToken();
                    deferred.resolve(response, status, headers, config);
                }).error(function (response, status, headers, config) {
                    deferred.reject(response, status, headers, config);
                });

                return deferred.promise;
            },

            register: function (username, password) {
                var deferred = $q.defer();

                $http.post('/api/user/register', {
                    username: username,
                    password: password
                }).success(function (response, status, headers, config) {
                    deferred.resolve(response, status, headers, config);
                }).error(function (response, status, headers, config) {
                    deferred.reject(response, status, headers, config);
                });

                return deferred.promise;
            }
        };

        return Auth;
    }]);