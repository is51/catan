'use strict';

angular.module('catan')
        .factory('ModalWindowService', [function () {

            var modals = {};

            var ModalWindowService = {};

            ModalWindowService.register = function(modalWindowId) {
                modals[modalWindowId] = false; // default value
            };

            ModalWindowService.show = function(modalWindowId) {
                modals[modalWindowId] = true;
            };

            ModalWindowService.hide = function(modalWindowId) {
                modals[modalWindowId] = false;
            };

            ModalWindowService.toggle = function(modalWindowId) {
                if (this.isVisible(modalWindowId)) {
                    this.hide(modalWindowId);
                } else {
                    this.show(modalWindowId);
                }
            };

            ModalWindowService.isVisible = function(modalWindowId) {
                return modals[modalWindowId];
            };

            ModalWindowService.isHidden = function(modalWindowId) {
                return !modals[modalWindowId];
            };

            return ModalWindowService;
        }]);