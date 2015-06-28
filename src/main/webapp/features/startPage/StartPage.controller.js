'use strict';

angular.module('catan')
    .controller('StartPageController', ['$scope', 'AuthToken', function($scope, AuthToken) {

        $scope.AuthToken = AuthToken; //

    }]);