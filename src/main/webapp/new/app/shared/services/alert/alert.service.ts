import { Injectable } from 'angular2/core';

@Injectable()
export class AlertService {
    message(text: string) {
        alert(text);
    }
}