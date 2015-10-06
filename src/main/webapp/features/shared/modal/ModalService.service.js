'use strict';

angular.module('catan')
        .factory('ModalService', [function () {

            var modals = {};

            var ModalService = {};

            ModalService.register = function(modalId) {
                modals[modalId] = false; // default value
            };

            ModalService.show = function(modalId) {
                modals[modalId] = true;
            };

            ModalService.hide = function(modalId) {
                modals[modalId] = false;
            };

            ModalService.toggle = function(modalId) {
                if (this.isVisible(modalId)) {
                    this.hide(modalId);
                } else {
                    this.show(modalId);
                }
            };

            ModalService.isVisible = function(modalId) {
                return modals[modalId];
            };

            ModalService.isHidden = function(modalId) {
                return !modals[modalId];
            };

            return ModalService;
        }]);