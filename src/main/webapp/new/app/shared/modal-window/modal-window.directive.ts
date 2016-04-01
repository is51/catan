import { Directive, OnInit } from 'angular2/core';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

@Directive({
    selector: 'ct-modal-window',
    inputs: [
        'modalWindowId',
        'onShow',
        'onHide'
    ],
    host: {
        '[hidden]': 'isHidden()'
    },
})

export class ModalWindowDirective implements OnInit {
    modalWindowId: string;
    onShow: Function;
    onHide: Function;
    
    constructor(private _modalWindowService: ModalWindowService) { }

    ngOnInit() {
        this._modalWindowService.onShow(this.modalWindowId, this.onShow);
        this._modalWindowService.onHide(this.modalWindowId, this.onHide);

        if (this._modalWindowService.isVisible(this.modalWindowId) && this.onShow) {
            this.onShow();
        }
    }

    isHidden() {
        return this._modalWindowService.isHidden(this.modalWindowId);
    }
}