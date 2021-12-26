import { Component, OnInit, OnDestroy } from 'angular2/core';
import { Game } from 'app/shared/domain/game';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

const DISPLAYING_MESSAGES_INTERVAL = 2000;

@Component({
    selector: 'ct-top-message',
    templateUrl: 'app/play/top-message/top-message.component.html',
    styleUrls: ['app/play/top-message/top-message.component.css'],
    inputs: ['game']
})

export class TopMessageComponent implements OnInit, OnDestroy {
    game: Game;
    text: string = null;
    messageBoxWidth: number = 0;
    viewBoxWidth: number = 700;

    private _displayedMessage = null;

    private _messagesQueue: string[] = <string[]>[];
    private _isDisplayingCycleExecuting = false;

    constructor(private _authUser: AuthUserService) { }

    ngOnInit() {
        let currentPlayer = this.game.getCurrentPlayer(this._authUser.get());

        let lastLog = (currentPlayer.log.length) ? currentPlayer.log[0] : null;
        if (lastLog && lastLog.displayedOnTop) {
            this._addMessageToQueue(lastLog.message);
            if (currentPlayer.displayedMessage) {
                this._displayedMessage = currentPlayer.displayedMessage;
            }
        } else if (currentPlayer.displayedMessage) {
            this._show(currentPlayer.displayedMessage);
        }

        currentPlayer.onDisplayedMessageUpdate(
                text => {
                    this._displayedMessage = text;
                    if (!this._isDisplayingCycleExecuting) {
                        this._show(this._displayedMessage);
                    }
                },
                () => {
                    this._displayedMessage = null;
                    if (!this._isDisplayingCycleExecuting) {
                        this._hide();
                    }
                }
            );

        currentPlayer.onDisplayedLogUpdate(
            newLogItems => {
                newLogItems.forEach(item => this._addMessageToQueue(item.message));
            }
        );
    }

    private _addMessageToQueue(text: string) {
        this._messagesQueue.push(text);
        if (!this._isDisplayingCycleExecuting) {
            this._executeDisplayingCycle();
        }
    }

    private _executeDisplayingCycle() {
        if (this._messagesQueue.length) {
            this._isDisplayingCycleExecuting = true;
            let message = this._messagesQueue.shift();
            this._show(message);
            setTimeout(() => this._executeDisplayingCycle(), DISPLAYING_MESSAGES_INTERVAL);
        } else {
            this._isDisplayingCycleExecuting = false;
            if (this._displayedMessage) {
                this._show(this._displayedMessage);
            }
        }
    }

    private _show(text: string) {
        this.text = text;
        this.messageBoxWidth = text.length * 14 + 10;
    }

    private _hide() {
        this.text = null;
    }

    ngOnDestroy() {
        let currentPlayer = this.game.getCurrentPlayer(this._authUser.get());
        currentPlayer.cancelOnDisplayedMessageUpdate();
        currentPlayer.cancelOnDisplayedLogUpdate();
    }
}