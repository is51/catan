(function () {
    angular.module("shop-games")
            .controller("SingleGameViewController", ["communicator", "$stateParams", "$scope", function (communicator, $stateParams, $scope) {
                this.game = {
                    price: 0
                };
                this.populate = function (arrayOfGames) {
                    this.game =  arrayOfGames[$stateParams.gameId];
                };
                var populateMethodWithoutLostContext = this.populate.bind($scope);
                communicator.getAllGames(populateMethodWithoutLostContext, $scope);

            }])
})();