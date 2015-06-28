'use strict';

angular.module('catan')
    .factory('AuthInterceptor', ['$injector', '$q', '$location', function ($injector, $q, $location) {
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
                    alert('Error 403: Access denied'); // temp
                    $location.path('/'); // needs showing of login form
                }
                return $q.reject(rejection);
            }
        };
    }]);