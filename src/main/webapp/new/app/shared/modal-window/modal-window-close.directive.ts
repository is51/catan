import { Directive, ElementRef } from 'angular2/core';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

@Directive({
    selector: '[ct-modal-window-close]',
    inputs: ['modalWindowId']
})

export class ModalWindowCloseDirective {
    public modalWindowId: string;
    
    constructor(
        public element: ElementRef,
        private _modalWindowService: ModalWindowService) { }

    onClick() {
        alert('asdasd');
        this._modalWindowService.hide(this.modalWindowId);
    }
}