import { Directive } from 'angular2/core';
import { SelectService } from 'app/play/shared/services/select.service';

@Directive({
    selector: '[ct-choose-resources-cancel]',
    inputs: ['type'],
    host: {
        '(click)': 'onClick($event)',
    }
})

export class ChooseResourcesCancelDirective {
    public type: string;
    
    constructor(private _select: SelectService) { }

    onClick() {
        this._select.cancelRequestSelection(this.type);
    }
}