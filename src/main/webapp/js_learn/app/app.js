(function () {
    angular.module('shop-games', []);
    angular.module('shop', [
        'shop-games',
        'ui.router'
    ])
            .config(function ($stateProvider, $urlRouterProvider) {
                $urlRouterProvider.otherwise('/');
                $stateProvider
                        .state('home', {
                            url: '/',
                            templateUrl: 'home.html'
                        })

                        .state('store', {
                            url: '/store/with/list/of/games',
                            templateUrl: 'app/directives/storeGames/store-games.html',
                            controller: "StoreGamesController"
                        })

                        .state('game-details', {
                            url: '/store/details/of/game/{gameId}',
                            templateUrl: 'app/directives/singleGameView/single-game-view.html',
                            controller: "SingleGameViewController"
                        })

                        .state('about', {
                            url: '/about/this/pretty/site',
                            template: 'This is a shop of games, that were developed by young team founded in 2015'
                        });
            });
})();