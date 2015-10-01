'use strict';

angular.module('catan')
    .provider('Remote', function () {

        var Remote = {},
            http,
            defaultParams = {},
            groupParams = {};

        return {
            setDefault: function(params) {
                defaultParams = params;
                return this;
            },

            setGroup: function(group, params) {
                groupParams[group] = params;
                return this;
            },

            setRequest: function (group, name, params) {

                var finalParams = angular.extend(
                    {},
                    defaultParams,
                    (groupParams[group]) ? groupParams[group] : {},
                    params
                );

                Remote[group] = Remote[group] || {};
                Remote[group][name] = function(data) {
                    finalParams.data = data;
                    return http(finalParams);
                };

                return this;
            },

            $get: ['$http', function($http) {
                http = $http;
                return Remote;
            }]
        };
    });