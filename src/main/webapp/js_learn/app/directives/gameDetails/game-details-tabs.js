(function () {
    angular.module('shop-games')
            .directive("gameDetailsTabs", function () {
                return {
                    restrict: 'E',
                    templateUrl: "app/directives/gameDetails/game-details-tabs.html",
                    controllerAs: "tab",
                    controller: function () {
                        this.active = 1;

                        this.setActive = function (value) {
                            this.active = value;
                        };

                        this.isActive = function (tab) {
                            return this.active === tab;
                        };
                    }
                };
            });
})();