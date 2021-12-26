System.register([], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var Dice;
    return {
        setters:[],
        execute: function() {
            Dice = (function () {
                function Dice(params) {
                    if (!params) {
                        return;
                    }
                    this.thrown = params.thrown;
                    this.first = params.first;
                    this.second = params.second;
                    this.value = params.value;
                }
                Dice.prototype.update = function (params) {
                    if (!params) {
                        return;
                    }
                    // TODO: add dice.id (number) to indicate if new dice thrown
                    if (this.thrown !== params.thrown ||
                        this.first !== params.first ||
                        this.second !== params.second) {
                        this.thrown = params.thrown;
                        this.first = params.first;
                        this.second = params.second;
                        this.value = params.value;
                        if (this.thrown) {
                            this.triggerThrow(this.value);
                        }
                    }
                };
                //TODO: try to replace with Subscribable
                Dice.prototype.onThrow = function (_onThrow) {
                    this._onThrow = _onThrow;
                };
                Dice.prototype.cancelOnThrow = function () {
                    this._onThrow = undefined;
                };
                Dice.prototype.triggerThrow = function (value) {
                    if (this._onThrow) {
                        this._onThrow(value);
                    }
                };
                return Dice;
            }());
            exports_1("Dice", Dice);
        }
    }
});
