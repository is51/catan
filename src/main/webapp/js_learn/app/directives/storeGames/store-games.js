(function () {
    angular.module("shop-games")
            .directive("storeGames", function () {
                return {
                    restrict: "E",
                    templateUrl: "app/directives/storeGames/store-games.html",
                    controller: "StoreGamesController"
                }
            });
})();