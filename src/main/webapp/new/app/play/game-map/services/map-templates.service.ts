import { Injectable } from 'angular2/core';

const COLOR_BRICK = '#43a8d9';
const COLOR_WOOD = '#d5c867';
const COLOR_SHEEP = '#48da83';
const COLOR_WHEAT = '#a187aa';
const COLOR_STONE = '#f69f7e';
const COLOR_EMPTY = '#cccccc';

@Injectable()
export class MapTemplatesService {

    private _templates: Map<string, string> = new Map<string, string>();

    constructor() {

        this._templates.set('hex-bg', `
            <g transform="scale(0.5)">
                <polygon fill="#FFFFFF" points="-75,14.43375 25,-43.30125 75,-14.43375 -25,43.30125" />
            </g>
        `);

        this._templates.set('hex-resource-brick', `
            <g transform="scale(0.45)">
                <polygon fill="${COLOR_BRICK}" stroke-width="0" points="-75,14.43375 25,-43.30125 75,-14.43375 -25,43.30125" />
            </g>
        `);

        this._templates.set('hex-resource-wood', `
            <g transform="scale(0.45)">
                <polygon fill="${COLOR_WOOD}" stroke-width="0" points="-75,14.43375 25,-43.30125 75,-14.43375 -25,43.30125" />
            </g>
        `);

        this._templates.set('hex-resource-sheep', `
            <g transform="scale(0.45)">
                <polygon fill="${COLOR_SHEEP}" stroke-width="0" points="-75,14.43375 25,-43.30125 75,-14.43375 -25,43.30125" />
            </g>
        `);

        this._templates.set('hex-resource-wheat', `
            <g transform="scale(0.45)">
                <polygon fill="${COLOR_WHEAT}" stroke-width="0" points="-75,14.43375 25,-43.30125 75,-14.43375 -25,43.30125" />
            </g>
        `);

        this._templates.set('hex-resource-stone', `
            <g transform="scale(0.45)">
                <polygon fill="${COLOR_STONE}" stroke-width="0" points="-75,14.43375 25,-43.30125 75,-14.43375 -25,43.30125" />
            </g>
        `);

        this._templates.set('hex-resource-empty', `
            <g transform="scale(0.45)">
                <polygon fill="${COLOR_EMPTY}" stroke-width="0" points="-75,14.43375 25,-43.30125 75,-14.43375 -25,43.30125" />
            </g>
        `);

        this._templates.set('hex-dice', `
            <g transform="translate({x}, {y})" class="dice">
                <g transform="scale(1, 0.57735)">
                    <text x="0" y="3" text-anchor="middle" font-size="9">{number}</text>
                </g>
            </g>
        `);

        this._templates.set('blank-node', `
            <g transform="scale(1, 0.57735)">
                <circle cx="0" cy="0" r="7"></circle>
            </g>
        `);

        this._templates.set('settlement', `
            <g transform="scale(1, 0.57735)">
                <circle cx="0" cy="0" r="8" player-color="{colorId}"></circle>
                <circle cx="0" cy="-3" r="8" player-color="{colorId}"></circle>
                <text x="0" y="1" text-anchor="middle">s</text>
            </g>
        `);

        this._templates.set('city', `
            <g transform="scale(1, 0.57735)">
                <circle cx="0" cy="0" r="10" player-color="{colorId}"></circle>
                <circle cx="0" cy="-3" r="10" player-color="{colorId}"></circle>
                <circle cx="0" cy="-6" r="10" player-color="{colorId}"></circle>
                <text x="0" y="-1" text-anchor="middle">C</text>
            </g>
        `);

        this._templates.set('road-vertical', `
            <g>
                <polygon player-color="{colorId}" points="-15,-5.7735 -10,-8.66025 15,5.7735 10,8.66025" />
            </g>
        `);

        this._templates.set('road-horizontal', `
            <g>
                <polygon player-color="{colorId}" points="-15,5.7735 10,-8.66025 15,-5.7735 -10,8.66025" />
            </g>
        `);

        this._templates.set('blank-edge-vertical', `
            <g>
                <polygon points="-15,-5.7735 -10,-8.66025 15,5.7735 10,8.66025" />
            </g>
        `);

        this._templates.set('blank-edge-horizontal', `
            <g>
                <polygon points="-15,5.7735 10,-8.66025 15,-5.7735 -10,8.66025" />
            </g>
        `);

        this._templates.set('port', `
            <g transform="scale(0.35)">
                <g transform="scale(1, 0.57735)">
                    <g transform="translate(1, 6)">
                        <path fill="#999" fill-opacity="0.15" d="M8.527-12.958c0,0,2.118-1.573,4.102-0.143c1.711,1.232-0.244,4.047-0.244,4.047L4.548-0.978l3.626,17.884
                            l-1.671,1.726L-1.018,5.15l-5.07,4.543l0.182,5.203l-1.113,1.54l-1.624-5.905l-4.053,0.933l1.378-3.509l-6.499-1.669l2.146-1.201
                            l5.262,0.102l4.752-4.68l-14.561-8.624l1.44-1.72l18.505,4.566L8.527-12.958z"/>
                    </g>
                    <path resource-type="{type}" d="M8.527-12.958c0,0,2.118-1.573,4.102-0.143c1.711,1.232-0.244,4.047-0.244,4.047L4.548-0.978l3.626,17.884
                        l-1.671,1.726L-1.018,5.15l-5.07,4.543l0.182,5.203l-1.113,1.54l-1.624-5.905l-4.053,0.933l1.378-3.509l-6.499-1.669l2.146-1.201
                        l5.262,0.102l4.752-4.68l-14.561-8.624l1.44-1.72l18.505,4.566L8.527-12.958z"/>
                </g>
            </g>
        `);

        this._templates.set('robber', `
            <g transform="scale(0.15)">
                <g transform="scale(1, 0.57735)">
                    <path fill="#444444" d="M1.5,3.347c0,0,7.75,9.25,8.5,15.5c0.864,7.203-8.5,16-8.5,16s8.5,1.25,21.5-8
                        c3.84-2.732,6.155-12.864,7-17.5c0.882-4.835,0.288-12.75-2.5-19.5c-4.141-10.024-17-16.5-17-16.5s-3.619,6.767-2.65,11.225
                        c1.25,5.75,7.9,10.775,7.9,10.775S4.75-4.75,0-11c-2.077-2.732-4.02-12.26-3.5-15.653C-3.136-29.03,1-36.903,1-36.903
                        s-13.464,7.164-16.5,10.75c-2.488,2.938-3.874,11.167-3.5,15c0.288,2.957,3.676,8.146,4.5,11c0.66,2.285,3,6.5-0.5,9.5
                        c-1.445,1.239-5.772-1.544-7-3c-2.072-2.457-3-12.5-3-12.5s-6.997,6.959-8,10c-1.423,4.314,0.524,13.909,2.5,18
                        c1.631,3.377,8.266,8.663,12,9c4.732,0.427,14.52-4.447,17-8.5C1.01,18.245,1.5,3.347,1.5,3.347z"/>
                </g>
            </g>
        `);

    }

    get(id: string, replace?: any) {
        let template = this._templates.get(id);

        if (!template) {
            return null;
        }

        if (replace) {
            for (let i in replace) {
                template = template.replace(new RegExp('{'+i+'}','g'), replace[i]);
            }
        }

        return template;
    }

}