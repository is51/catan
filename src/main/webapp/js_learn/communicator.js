(function () {
    angular.module("shop-http", [])
            .factory("communicator", ['$http', '$log', function ($http, $log) {
                return {
                    getAllGames: function (populateMethodWithoutLostContext, contextOfController) {
                        $log.log("calls getAllGames");
                        $http.post("/learn/games")
                                .success(function (data) {
                                    $log.log(data);
                                    populateMethodWithoutLostContext(data);
                                    //contextOfController.games = data;
                                });

                    }
                };
            }]);
})();


