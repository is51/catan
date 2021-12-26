System.register([], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var User;
    return {
        setters:[],
        execute: function() {
            User = (function () {
                function User(params) {
                    this.id = params.id;
                    this.guest = params.guest;
                    this.username = params.username;
                    this.firstName = params.firstName;
                    this.lastName = params.lastName;
                }
                User.prototype.update = function (params) {
                    //TODO: revise this method
                    this.guest = params.guest;
                    this.username = params.username;
                    this.firstName = params.firstName;
                    this.lastName = params.lastName;
                };
                User.prototype.getDisplayedName = function () {
                    return this.username;
                };
                return User;
            }());
            exports_1("User", User);
        }
    }
});
