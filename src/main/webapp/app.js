'use strict';

angular.module('catan', [
    'ngRoute',
    'ngCookies'
])

    .run(['$rootScope', 'User', function($rootScope, User) {
        $rootScope.User = User;
        User.load();
    }])

    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/', {templateUrl: 'features/startPage/startPage.html', controller: 'StartPageController'});
        $routeProvider.otherwise({redirectTo: '/'});
    }])

    .config(['$httpProvider', function($httpProvider) {
        $httpProvider.interceptors.push('AuthInterceptor');
    }])

    .config(['RemoteProvider', function(RemoteProvider) {
        RemoteProvider.setDefault({
            method: 'post',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            transformRequest: function(data) {
                return (data) ? angular.element.param(data) : data;
            }
        });
        RemoteProvider.setRequest('auth', 'register', { url: '/api/user/register'});
        RemoteProvider.setRequest('auth', 'login', { url: '/api/user/login'});
        RemoteProvider.setRequest('auth', 'logout', { url: '/api/user/logout'});
        RemoteProvider.setRequest('auth', 'details', { url: '/api/user/details'});
    }]);