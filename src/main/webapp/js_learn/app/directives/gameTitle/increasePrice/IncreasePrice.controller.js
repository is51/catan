(function(){
    angular.module("shop-games")
            .controller("IncreasePriceController", ['$interval', "$scope", function($interval, $scope){
                $scope.updatedPrice = $scope.priceOfGame;

                $interval(function() {
                    $scope.updatedPrice += Math.random();
                }, 1000);
                //this.updatedPrice = 10;//$scope.updatedPrice;
            }]);
})();