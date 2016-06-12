import { Injectable } from 'angular2/core';

interface ModalWindow {
    isVisible: boolean;
    onShow: Function;
    onHide: Function;
}

@Injectable()
export class ModalWindowService {
    private _modalWindows: Map<string, ModalWindow> = new Map<string, ModalWindow>();

    private _getOrCreate(id: string) {
        if (!this._modalWindows.has(id)) {
            this._modalWindows.set(id, <ModalWindow>{
                isVisible: false,
                onShow: null,
                onHide: null
            });
        }
        return this._modalWindows.get(id);
    }

    show(id: string) {
        let modalWindow = this._getOrCreate(id);
        modalWindow.isVisible = true;
        if (modalWindow.onShow) {
            modalWindow.onShow();
        }
    }

    hide(id: string) {
        let modalWindow = this._getOrCreate(id);
        modalWindow.isVisible = false;
        if (modalWindow.onHide) {
            modalWindow.onHide();
        }
    }

    toggle(id: string) {
        if (this.isVisible(id)) {
            this.hide(id);
        } else {
            this.show(id);
        }
    }

    isVisible(id: string) {
        return this._modalWindows.has(id) && this._modalWindows.get(id).isVisible;
    }

    isHidden(id: string) {
        return !this.isVisible(id);
    }

    onShow(id: string, onShow: Function) {
        this._getOrCreate(id).onShow = onShow;
    }

    onHide(id: string, onHide: Function) {
        this._getOrCreate(id).onHide = onHide;
    }

    /*removeOnShow(id: string) {
        this._modalWindows.get(id).onShow = null;
    }

    removeOnHide(id: string) {
        this._modalWindows.get(id).onHide = null;
    }*/
}