import { Component } from 'angular2/core';

@Component({
    selector: 'ct-alert',
    templateUrl: 'app/shared/alert/alert.component.html',
    styleUrls: ['app/shared/alert/alert.component.css']
})

export class AlertComponent {
    text: string = '';
    close: Function = () => {};
}