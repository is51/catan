import { Injectable } from 'angular2/core';
import { ApplicationActiveService } from 'app/shared/services/application-active/application-active.service';

const ICONS_PATH = 'app/shared/services/notification/images/';
const ICONS = {
    DEFAULT: ICONS_PATH + 'default.png',
    THROW_DICE: ICONS_PATH + 'throw-dice.png',
    TRADE_REPLY: ICONS_PATH + 'trade-reply.png',
    KICK_OFF_RESOURCES: ICONS_PATH + 'kick-off-resources.png'
};

@Injectable()
export class NotificationService {
    private _nativeService: any = null;

    constructor (private _appActive: ApplicationActiveService) {
        this._nativeService = window.Notification;
    }

    notify(message: string, iconCode: string = 'DEFAULT', tag?: string, showOnlyIfAppIsNotFocused: boolean = true) {
        if (!this._nativeService) {
            return;
        }

        if (showOnlyIfAppIsNotFocused && this._appActive.isActive()) {
            return;
        }

        let icon = ICONS[iconCode];

        this._nativeService.requestPermission(permission => {
            if (permission === "granted") {
                let notification = new this._nativeService(message, {
                    tag,
                    icon
                });
                notification.onclick = () => notification.close(); //TODO: make close when user click inside app window or window.focus
            }
        });
    }

    notifyGlobal(message: string, iconCode?: string) {
        this.notify(message, iconCode, 'GLOBAL');
    }

    requestPermission() {
        if (this._nativeService) {
            this._nativeService.requestPermission();
        }
    }
}