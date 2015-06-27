'use strict';

angular.module('catan')
    .directive('ctLoginForm', ['Auth', function(Auth) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/loginForm/ct-login-form.html",
            link: function(scope) {

                scope.data = {};

                scope.submit = function() {
                    Auth.login(scope.data.username, scope.data.password)
                        .then(function(response) {
                            alert('success');
                            console.log(response);
                        }, function(response) {
                            alert('error');
                            console.log(response);
                        });
                };

            }
        };
    }]);