import { Directive } from 'angular2/core';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

@Directive({
    selector: '[ct-modal-window-close]',
    inputs: ['modalWindowId'],
    host: {
        '(click)': 'onClick($event)',
    }
})

export class ModalWindowCloseDirective {
    public modalWindowId: string;
    
    constructor(private _modalWindowService: ModalWindowService) { }

    onClick() {
        this._modalWindowService.hide(this.modalWindowId);
    }
}

//TODO: try to find modalWindowId automatically