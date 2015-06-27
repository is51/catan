'use strict';

angular.module('catan')
    .factory('AuthInterceptor', ['$injector', '$q', '$location', function ($injector, $q, $location) {
        return {
            request: function (config) {
                var AuthToken = $injector.get('AuthToken');
                var token = AuthToken.get();

                if (token) {
                  config.headers['Authorization'] = token;
                }
                return config;
            },

            responseError: function (rejection) {
                if (rejection.status === 403) {
                    $location.path('/'); // needs showing of login form
                }
                return $q.reject(rejection);
            }
        };
    }]);