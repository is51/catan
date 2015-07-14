'use strict';

angular.module('catan')
    .directive('ctRegisterForm', ['Auth', function(Auth) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/registerForm/ct-register-form.html",
            link: function(scope) {

                scope.data = {};

                scope.submit = function(autologin) {
                    Auth.register(scope.data.username, scope.data.password)
                        .then(function() {

                            if (autologin) {
                                Auth.login(scope.data.username, scope.data.password)
                                    .then(function() {
                                        // do nothing
                                    }, function(response) {
                                        alert('Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                                    });
                            } else {
                                alert('Successful registration');
                            }

                        }, function(response) {
                            alert('Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                };

            }
        };
    }]);