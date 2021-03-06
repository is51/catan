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

                .state('createGame', {
                    templateUrl: 'features/screens/createGame/view.html',
                    params: {data: null}
                })

                .state('joinGame', {
                    templateUrl: 'features/screens/joinGame/view.html'
                })

                .state('joinPublicGame', {
                    templateUrl: 'features/screens/joinPublicGame/view.html'
                })

                .state('joinPrivateGame', {
                    templateUrl: 'features/screens/joinPrivateGame/view.html',
                    params: {data: null}
                })

                .state('continueGame', {
                    templateUrl: 'features/screens/continueGame/view.html'
                });
    }])

    .config(['$httpProvider', function($httpProvider) {
        $httpProvider.interceptors.push('AuthInterceptor');
    }])

    .config(['RemoteProvider', function(RemoteProvider) {
        RemoteProvider

            .setDefault({
                method: 'post',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Accept': 'application/json'
                },
                transformRequest: function(data) {
                    return (data) ? angular.element.param(data) : data;
                }
            })

            .setRequest('auth', 'register', { url: '/api/user/register'})
            .setRequest('auth', 'registerAndLoginGuest', { url: '/api/user/register/guest'})
            .setRequest('auth', 'login', { url: '/api/user/login'})
            .setRequest('auth', 'logout', { url: '/api/user/logout'})
            .setRequest('auth', 'details', { url: '/api/user/details'})

            .setRequest('game', 'create', { url: '/api/game/create'})
            .setRequest('game', 'listCurrent', { url: '/api/game/list/current'})
            .setRequest('game', 'listPublic', { url: '/api/game/list/public'})
            .setRequest('game', 'joinPublic', { url: '/api/game/join/public'})
            .setRequest('game', 'joinPrivate', { url: '/api/game/join/private'})
            .setRequest('game', 'details', { url: '/api/game/details'})
            .setRequest('game', 'leave', { url: '/api/game/leave'})
            .setRequest('game', 'cancel', { url: '/api/game/cancel'})
            .setRequest('game', 'ready', { url: '/api/game/ready'})
            .setRequest('game', 'notReady', { url: '/api/game/not-ready'})

            .setRequest('play', 'endTurn', { url: '/api/play/end-turn'})
            .setRequest('play', 'buildSettlement', { url: '/api/play/build/settlement'})
            .setRequest('play', 'buildCity', { url: '/api/play/build/city'})
            .setRequest('play', 'buildRoad', { url: '/api/play/build/road'})
            .setRequest('play', 'buyCard', { url: '/api/play/buy/card'})
            .setRequest('play', 'throwDice', { url: '/api/play/throw-dice'})
            .setRequest('play', 'useCardYearOfPlenty', { url: '/api/play/use-card/year-of-plenty'})
            .setRequest('play', 'useCardRoadBuilding', { url: '/api/play/use-card/road-building'})
            .setRequest('play', 'useCardMonopoly', { url: '/api/play/use-card/monopoly'})
            .setRequest('play', 'useCardKnight', { url: '/api/play/use-card/knight'})
            .setRequest('play', 'moveRobber', { url: '/api/play/robbery/move-robber'})
            .setRequest('play', 'choosePlayerToRob', { url: '/api/play/robbery/choose-player-to-rob'})
            .setRequest('play', 'kickOffResources', { url: '/api/play/robbery/kick-off-resources'})
            .setRequest('play', 'tradePort', { url: '/api/play/trade/port'})
            .setRequest('play', 'tradePropose', { url: '/api/play/trade/propose'})
            .setRequest('play', 'tradeAccept', { url: '/api/play/trade/reply/accept'})
            .setRequest('play', 'tradeDecline', { url: '/api/play/trade/reply/decline'});
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