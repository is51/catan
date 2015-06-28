'use strict';

angular.module('catan', [
    'ngRoute',
    'ngCookies'
])

    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/', {templateUrl: 'features/startPage/startPage.html', controller: 'StartPageController'});
        $routeProvider.otherwise({redirectTo: '/'});
    }])

    .config(['$httpProvider', function($httpProvider) {
        $httpProvider.interceptors.push('AuthInterceptor');
    }])

    .config(['RemoteProvider', function(RemoteProvider) {
        RemoteProvider.setRequest('auth', 'register', { method: 'post', url: '/api/user/register'});
        RemoteProvider.setRequest('auth', 'login', { method: 'post', url: '/api/user/login'});
        RemoteProvider.setRequest('auth', 'logout', { method: 'post', url: '/api/user/logout'});
    }]);