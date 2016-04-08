import { Injectable } from 'angular2/core';
import { Http } from 'angular2/http';
import { Observable } from 'rxjs/Rx';

//TODO: defs (filers, gradients) should be loaded separately and only once - on first loading of template

const TEMPLATES_PATH = '/new/resources/templates/'; // Depends on #rootpath
const TEMPLATES_SUFFIX = '.template.xml';
const TEMPLATES = [
    'hex-bg',

    'hex-bg-edge-top-right',
    'hex-bg-edge-top-left',
    'hex-bg-edge-bottom-right',
    'hex-bg-edge-bottom-left',
    'hex-bg-edge-left',
    'hex-bg-edge-right',

    'hex-bg-node-bottom',
    'hex-bg-node-bottom-right',
    'hex-bg-node-bottom-left',
    'hex-bg-node-top',
    'hex-bg-node-top-right',
    'hex-bg-node-top-left',

    'hex-bg-node-right-top',
    'hex-bg-node-right-bottom',
    'hex-bg-node-left-top',
    'hex-bg-node-left-bottom',

    'hex-dice',
    'hex-brick',
    'hex-wood',
    'hex-sheep',
    'hex-wheat',
    'hex-stone',
    'hex-empty',

    'icon-brick',
    'icon-wood',
    'icon-sheep',
    'icon-wheat',
    'icon-stone',
    'icon-any',

    'robber',

    'blank-node',
    'settlement',
    'city',

    'port',
    'port-horizontal',
    'port-vertical',

    'road-vertical',
    'road-horizontal',
    'blank-edge-vertical',
    'blank-edge-horizontal',

    'clouds',
    'map-bottom'
];

@Injectable()
export class TemplatesService {
    private _templates: Map<string, string> = new Map<string, string>();

    constructor(private _http: Http) { }

    load() {
        return new Promise((resolve, reject) => {
            let requests = TEMPLATES.map(templateName =>
                this._http.get(TEMPLATES_PATH + templateName + TEMPLATES_SUFFIX)
            );

            Observable.forkJoin(requests).subscribe(
                    responses => {
                        responses.forEach((response, i) => {
                            this._templates.set(TEMPLATES[i], response.text());
                        });
                        resolve();
                    },
                    () => reject()
            );
        });
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