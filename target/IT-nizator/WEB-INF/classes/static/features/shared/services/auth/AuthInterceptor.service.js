'use strict';

angular.module('catan')
    .factory('AuthInterceptor', ['$injector', '$q', function ($injector, $q) {
        return {
            request: function (config) {
                var AuthToken = $injector.get('AuthToken');
                var token = AuthToken.get();

                if (token) {
                    //config.headers['Authorization'] = token;
                    config.data = config.data || {};
                    config.data.token = token;
                }
                return config;
            },

            responseError: function (rejection) {
                if (rejection.status === 403) {
                    console.log('Error 403: Access denied'); // temp
                    //$state.go('start'); // needs showing of login form
                }
                return $q.reject(rejection);
            }
        };
    }]);