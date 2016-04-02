import { Injectable } from 'angular2/core';
import { ApplicationActiveService } from 'app/shared/services/application-active/application-active.service';

declare var Notification: any;

const ICONS_PATH = 'resources/notification/';
const ICONS = {
    DEFAULT: ICONS_PATH + 'default.png',
    THROW_DICE: ICONS_PATH + 'throw-dice.png',
    TRADE_REPLY: ICONS_PATH + 'trade-reply.png',
    KICK_OFF_RESOURCES: ICONS_PATH + 'kick-off-resources.png',
    GAME_IS_STARTED: ICONS_PATH + 'game-is-started.png'
};

@Injectable()
export class NotificationService {
    constructor (private _appActive: ApplicationActiveService) { }

    notify(message: string, iconCode: string = 'DEFAULT', tag?: string, showOnlyIfAppIsNotFocused: boolean = true) {

        if (showOnlyIfAppIsNotFocused && this._appActive.isActive()) {
            return Promise.reject('APP_IS_FOCUSED');
        }

        let icon = ICONS[iconCode];

        return new Promise((resolve, reject) => {
            Notification.requestPermission(permission => {
                if (permission === "granted") {
                    let notification = new Notification(message, {
                        tag,
                        icon
                    });
                    notification.onclick = () => notification.close(); //TODO: make close when user click inside app window or window.focus
                    resolve(notification);
                } else {
                    reject(permission);
                }
            });
        });
    }

    notifyGlobal(message: string, iconCode?: string) {
        this.notify(message, iconCode, 'GLOBAL');
    }

    requestPermission() {
        return Notification.requestPermission();
    }
}