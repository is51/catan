(function () {
    angular.module("shop")
            .service("communicator", ['$http', '$log', function ($http, $log) {
                return {
                    getAllGames: function () {
                        $log.log("calls getAllGames");
                        return $http.post("/learn/games");
                    }
                };
            }]);
})();


