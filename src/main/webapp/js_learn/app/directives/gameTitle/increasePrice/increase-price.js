(function(){
    angular.module("shop-games")
            .directive("increasePrice",["$interval", function($interval){
                return {
                    restrict: "A",
                    scope: {
                        game: "="
                    },
                    template: " Original price {{game.price | currency}} - now this shit costs {{updatedPrice | currency}}",
                    link: function(scope) {

                        scope.updatedPrice = scope.game.price;

                        $interval(function() {
                            scope.updatedPrice += Math.random();
                        }, 1000);

                    }
                };
            }]);
})();