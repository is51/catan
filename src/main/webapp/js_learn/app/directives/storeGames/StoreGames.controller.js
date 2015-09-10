(function () {
    angular.module("shop-games")
            .controller("StoreGamesController", ["communicator", "$scope", function(communicator, $scope) {
                $scope.games = [];
                $scope.populate = function (array) {
                    $scope.games = array;
                };
                var populateMethodWithoutLostContext = $scope.populate.bind($scope);
                communicator.getAllGames(populateMethodWithoutLostContext, $scope);
            }])
})();