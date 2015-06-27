'use strict';

angular.module('catan')
    .directive('ctRegisterForm', ['Auth', function(Auth) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/registerForm/ct-register-form.html",
            link: function(scope, element, attrs) {

                scope.data = {};

                scope.submit = function() {
                    Auth.register(scope.data.username, scope.data.password)
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