import { Component } from 'angular2/core';

const MAX_LINE_LENGTH = 23;

@Component({
    selector: 'ct-alert',
    templateUrl: 'app/shared/alert/alert.component.html',
    styleUrls: ['app/shared/alert/alert.component.css']
})

export class AlertComponent {
    lines: string[] = <string[]>[];
    close: Function = () => {};

    setText(text: string) {
        this.lines = this._split(text);
    }

    private _split(text: string) {
        let lines = <string[]>[];
        let words = text.split(' ');

        let currentLine = '';
        while (words.length) {
            let word = words.shift();

            if (currentLine === '') {
                currentLine = word;
            } else {
                let currentLineNewValue = currentLine + ' ' + word;
                if (currentLineNewValue.length <= MAX_LINE_LENGTH) {
                    currentLine = currentLineNewValue;
                } else {
                    lines.push(currentLine);
                    currentLine = word;
                }
            }
        }
        lines.push(currentLine);

        return lines;
    }

    moreThan2Lines() {
        return this.lines.length > 2;
    }
}