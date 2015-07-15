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
        $routeProvider.when('/game/:gameId', {templateUrl: 'features/gamePage/gamePage.html', controller: 'GamePageController'});
        $routeProvider.otherwise({redirectTo: '/'});
    }])

    .config(['$httpProvider', function($httpProvider) {
        $httpProvider.interceptors.push('AuthInterceptor');
    }])

    .config(['RemoteProvider', function(RemoteProvider) {
        RemoteProvider.setDefault({
            method: 'post',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Accept': 'application/json'
            },
            transformRequest: function(data) {
                return (data) ? angular.element.param(data) : data;
            }
        });

        RemoteProvider.setRequest('auth', 'register', { url: '/api/user/register'});
        RemoteProvider.setRequest('auth', 'login', { url: '/api/user/login'});
        RemoteProvider.setRequest('auth', 'logout', { url: '/api/user/logout'});
        RemoteProvider.setRequest('auth', 'details', { url: '/api/user/details'});

        RemoteProvider.setRequest('game', 'create', { url: '/api/game/create'});
        RemoteProvider.setRequest('game', 'listCurrent', { url: '/api/game/list/current'});
        RemoteProvider.setRequest('game', 'listPublic', { url: '/api/game/list/public'});
        RemoteProvider.setRequest('game', 'joinPublic', { url: '/api/game/join/public'});
        RemoteProvider.setRequest('game', 'joinPrivate', { url: '/api/game/join/private'});
        RemoteProvider.setRequest('game', 'details', { url: '/api/game/details'});
        RemoteProvider.setRequest('game', 'leave', { url: '/api/game/leave'});
        RemoteProvider.setRequest('game', 'cancel', { url: '/api/game/cancel'});
    }]);