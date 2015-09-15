(function () {
    angular.module("shop-games")
            .controller("StoreGamesController", ["communicator", "$scope", function (communicator, $scope) {
                $scope.games = [];
                communicator.getAllGames()
                        .success(function (data) {
                            $scope.games = data;
                        });
            }])
})();