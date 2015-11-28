'use strict';

angular.module('catan')
        .factory('ChooseResourcesWindowService', ['ModalWindowService', function (ModalWindowService) {

            var type = null;

            var ChooseResourcesWindowService = {};

            ChooseResourcesWindowService.show = function(shownType) {
                type = shownType;
                ModalWindowService.show("CHOOSE_RESOURCES");
            };

            ChooseResourcesWindowService.hide = function() {
                ModalWindowService.hide("CHOOSE_RESOURCES");
                type = null;
            };

            ChooseResourcesWindowService.getType = function() {
                return type;
            };

            return ChooseResourcesWindowService;
        }]);