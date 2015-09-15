(function () {
    angular.module("shop-games")
            .directive("increasePrice", ["$interval", function ($interval) {
                return {
                    restrict: "A",
                    scope: {
                        game: "="
                    },
                    template: " Original price {{game.price && game.price | currency}} - now this shit costs {{updatedPrice | currency}}",
                    link: function (scope) {
                        var set = false;

                        $interval(function () {
                            if (scope.game != undefined) {
                                if (!set) {
                                    scope.updatedPrice = scope.game.price;
                                }

                                scope.updatedPrice += Math.random();
                            }
                        }, 100);
                    }
                };
            }]);
})();