import { Injectable } from 'angular2/core';
import { Http } from 'angular2/http';
import { Observable } from 'rxjs/Rx';

const TEMPLATES_PATH = '/new/resources/map/'; // Depends on #rootpath
const TEMPLATES_SUFFIX = '.template.xml';
const TEMPLATES = [
    'hex-bg',
    'hex-dice',
    'hex-brick',
    'hex-wood',
    'hex-sheep',
    'hex-wheat',
    'hex-stone',
    'hex-empty',

    'robber',

    'blank-node',
    'settlement',
    'city',

    'port',

    'road-vertical',
    'road-horizontal',
    'blank-edge-vertical',
    'blank-edge-horizontal'
];

@Injectable()
export class MapTemplatesService {
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