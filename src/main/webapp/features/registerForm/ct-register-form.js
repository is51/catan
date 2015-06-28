'use strict';

angular.module('catan')
    .directive('ctRegisterForm', ['Auth', function(Auth) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/registerForm/ct-register-form.html",
            link: function(scope) {

                scope.data = {};

                scope.submit = function() {
                    Auth.register(scope.data.username, scope.data.password)
                        .then(function() {
                            alert('Success register');
                        }, function(response) {
                            alert('Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                };

            }
        };
    }]);