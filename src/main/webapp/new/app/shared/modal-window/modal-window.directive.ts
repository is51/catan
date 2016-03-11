import { Directive, OnInit, OnDestroy, ElementRef } from 'angular2/core';
import { ModalWindowService } from 'app/shared/modal-window/modal-window.service';

@Directive({
    selector: 'ct-modal-window',
    inputs: ['modalWindowId']
})

export class ModalWindowDirective implements OnInit, OnDestroy {
    public modalWindowId: string;
    
    constructor(
        public element: ElementRef,
        private _modalWindowService: ModalWindowService) { }

    ngOnInit() {
        this._modalWindowService.register(
            this.modalWindowId,
            () => this.element.nativeElement.style.display = 'block',
            () => this.element.nativeElement.style.display = 'hide'
        );
    }

    ngOnDestroy() {
        this._modalWindowService.unregister(this.modalWindowId);
    }
}