(function () {
    angular.module("shop-games")
            .controller("SingleGameViewController", ["communicator", "$stateParams", "$scope", function (communicator, $stateParams, $scope) {
                $scope.game = {
                    price: 0
                };
                communicator.getAllGames()
                        .success(function (data) {
                            $scope.game =  data[$stateParams.gameId];
                        });

            }])
})();