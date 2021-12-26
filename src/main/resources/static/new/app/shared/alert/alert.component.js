System.register(['angular2/core'], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1;
    var MAX_LINE_LENGTH, AlertComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            MAX_LINE_LENGTH = 23;
            AlertComponent = (function () {
                function AlertComponent() {
                    this.lines = [];
                    this.close = function () { };
                }
                AlertComponent.prototype.setText = function (text) {
                    this.lines = this._split(text);
                };
                AlertComponent.prototype._split = function (text) {
                    var lines = [];
                    var words = text.split(' ');
                    var currentLine = '';
                    while (words.length) {
                        var word = words.shift();
                        if (currentLine === '') {
                            currentLine = word;
                        }
                        else {
                            var currentLineNewValue = currentLine + ' ' + word;
                            if (currentLineNewValue.length <= MAX_LINE_LENGTH) {
                                currentLine = currentLineNewValue;
                            }
                            else {
                                lines.push(currentLine);
                                currentLine = word;
                            }
                        }
                    }
                    lines.push(currentLine);
                    return lines;
                };
                AlertComponent.prototype.moreThan2Lines = function () {
                    return this.lines.length > 2;
                };
                AlertComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-alert',
                        templateUrl: 'app/shared/alert/alert.component.html',
                        styleUrls: ['app/shared/alert/alert.component.css']
                    }), 
                    __metadata('design:paramtypes', [])
                ], AlertComponent);
                return AlertComponent;
            }());
            exports_1("AlertComponent", AlertComponent);
        }
    }
});
