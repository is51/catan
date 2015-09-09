(function () {
    angular.module('shop-games', []);
    angular.module('shop', ['shop-games']);

    angular.module('shop')
            .controller("StoreController", ["communicator", "$scope", function (communicator, $scope) {
                $scope.games = [];
                $scope.populate = function (array) {
                    $scope.games = array;
                };
                var populateMethodWithoutLostContext = $scope.populate.bind($scope);
                communicator.getAllGames(populateMethodWithoutLostContext, $scope);
            }]);
})();