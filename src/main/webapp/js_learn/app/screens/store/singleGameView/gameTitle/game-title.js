(function(){
    angular.module("shop-games")
            .directive("gameTitle", function(){
                return {
                    restrict: "E",
                    templateUrl: "app/screens/store/singleGameView/gameTitle/game-title.html"
                }
            });
})();