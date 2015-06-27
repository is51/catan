'use strict';

angular.module('catan')
    .provider('Remote', function () {

        var Remote = {};

        var http;

        return {
            setRequest: function (group, name, params) {
                Remote[group] = Remote[group] || {};
                Remote[group][name] = function(data) {
                    params.data = data;
                    return http(params);
                };
            },

            $get: ['$http', function($http) {
                http = $http;
                return Remote;
            }]
        };
    });