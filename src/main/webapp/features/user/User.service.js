'use strict';

angular.module('catan')
    .factory('User', ['$q', 'AuthToken', 'Remote', function ($q, AuthToken, Remote) {

        var STATUS_PENDING = 0,
            STATUS_NOT_AUTHORIZED = 1,
            STATUS_AUTHORIZED = 2;

        var status = STATUS_PENDING,
            details = {};

        return {
            isAuthorized: function() {
                return status === STATUS_AUTHORIZED;
            },

            isGuest: function() {
                return status === STATUS_NOT_AUTHORIZED;
            },

            isPending: function() {
                return status === STATUS_PENDING;
            },

            get: function() {
                return details;
            },

            load: function () {
                var deferred = $q.defer();

                //status = STATUS_PENDING;

                if (AuthToken.get()) {
                    Remote.auth.details().then(function(response) {
                        details = response.data;
                        status = STATUS_AUTHORIZED;
                        deferred.resolve();
                    }, function() {
                        status = STATUS_NOT_AUTHORIZED;
                        deferred.reject();
                    });
                } else {
                    status = STATUS_NOT_AUTHORIZED;
                    deferred.reject();
                }

                return deferred.promise;
            },

            setToGuest: function() {
                status = STATUS_NOT_AUTHORIZED;
                details = {};
            }
        };
    }]);