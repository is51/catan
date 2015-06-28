'use strict';

angular.module('catan')
    .directive('ctLogoutButton', ['Auth', function(Auth) {
        return {
            restrict: 'A',
            link: function(scope, element) {

                element.on('click', function() {
                    Auth.logout()
                        .then(function() {
                            alert('Success logout');
                        }, function(response) {
                            alert('Error: ' + ((response.data && response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });

                    return false;
                });

            }
        };
    }]);