'use strict';

angular.module('catan')
    .directive('ctLogoutButton', ['Auth', function(Auth) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {

                element.on('click', function() {
                    Auth.logout()
                        .then(function(response) {
                            alert('success');
                            console.log(response);
                        }, function(response) {
                            alert('error');
                            console.log(response);
                        });

                    return false;
                });

            }
        };
    }]);