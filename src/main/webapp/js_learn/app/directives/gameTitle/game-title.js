(function(){
    angular.module("shop-games")
            .directive("gameTitle", function(){
                return {
                    restrict: "E",
                    templateUrl: "app/directives/gameTitle/game-title.html"
                }
            });
})();