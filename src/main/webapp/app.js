'use strict';

angular.module('catan', [
    'ui.router'
])

    .config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise('/');

        $stateProvider

            .state('start', {
                url: '/',
                templateUrl: 'features/screens/start/view.html'
            })

            .state('game', {
                url: '/game/{gameId}',
                templateUrl: 'features/screens/game/view.html',
                controller: 'GameController'
            })

            .state('registerGuest', {
                    templateUrl: 'features/screens/registerGuest/view.html',
                    controller: 'RegisterGuestController',
                    params: {onRegister: null, onBack: null}
            })

            .state('login', {
                    templateUrl: 'features/screens/login/view.html',
                    params: {onLogin: null, onBack: null}
            })

            .state('register', {
                    templateUrl: 'features/screens/register/view.html',
                    params: {onRegister: null, onBack: null}
            })

            .state('createGame', {templateUrl: 'features/screens/createGame/view.html', params: {data: null}})
            .state('joinGame', {templateUrl: 'features/screens/joinGame/view.html'})
            .state('joinPublicGame', {templateUrl: 'features/screens/joinPublicGame/view.html'})
            .state('joinPrivateGame', {templateUrl: 'features/screens/joinPrivateGame/view.html', params: {data: null}})
            .state('continueGame', {templateUrl: 'features/screens/continueGame/view.html'});
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
        RemoteProvider.setRequest('auth', 'registerAndLoginGuest', { url: '/api/user/register/guest'});
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
    }])

    .run(['$rootScope', 'User', function($rootScope, User) {
        $rootScope.User = User;
        User.load();
    }])

    .run(['$rootScope', '$state', function($rootScope, $state) {
        $rootScope.$state = $state;

        // hack for tracking previous state
        $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState) {
            $state.previous = fromState;
        });
        $state.goPrevious = function(params) {
            $state.go($state.previous.name, params);
        };
    }]);