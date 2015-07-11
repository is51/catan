'use strict';

angular.module('catan')
    .controller('GamePageController', ['$scope', '$routeParams', '$location', 'Remote', function($scope, $routeParams, $location, Remote) {

        $scope.display = {};
        $scope.gameDetails = null;

        $scope.updateGameDetails = function() {
            Remote.game.details({gameId: $routeParams.gameId})
                .then(function(response) {
                    $scope.gameDetails = response.data;
                }, function(response) {
                    alert('Getting Game Details Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                    $location.path("/");
                });
        };

        $scope.updateGameDetails();

    }]);