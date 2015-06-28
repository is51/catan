'use strict';

angular.module('catan')
        .factory('AuthToken', ['$window', function ($window) {

            var TOKEN_STORAGE_NAME = 'token';

            return {
                get: function () {
                    return $window.localStorage.getItem(TOKEN_STORAGE_NAME);
                },

                set: function (token) {
                    $window.localStorage.setItem(TOKEN_STORAGE_NAME, token);
                },

                delete: function () {
                    $window.localStorage.removeItem(TOKEN_STORAGE_NAME);
                }
            };
        }]);