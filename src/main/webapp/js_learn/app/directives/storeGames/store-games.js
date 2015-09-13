(function () {
    angular.module("shop-games")
            .directive("storeGames", function () {
                return {
                    restrict: "E",
                    scope: {
                        games: "="
                    },
                    templateUrl: "app/directives/storeGames/store-games.html",
                    controller: "StoreGamesController"
                }
            });
})();