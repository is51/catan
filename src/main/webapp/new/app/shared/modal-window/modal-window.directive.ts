import { Directive, OnInit, OnDestroy } from 'angular2/core';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

@Directive({
    selector: 'ct-modal-window',
    inputs: ['modalWindowId'],
    host: {
        '[hidden]': 'isHidden()'
    },
})

export class ModalWindowDirective implements OnInit, OnDestroy {
    public modalWindowId: string;
    
    constructor(private _modalWindowService: ModalWindowService) { }

    ngOnInit() {
        this._modalWindowService.register(this.modalWindowId);
    }

    ngOnDestroy() {
        this._modalWindowService.unregister(this.modalWindowId);
    }

    isHidden() {
        return this._modalWindowService.isHidden(this.modalWindowId);
    }
}