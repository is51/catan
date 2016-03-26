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

/*

HOW TO CALCULATE COORDS OF HEX DEPEND ON PROPORTION
[ http://sandbox.onlinephpfunctions.com/code/ee6f3fdfb7a67e0f278ecdb0c8023b815b939680 ]

$N = 0.95; // N=1 - normal hex, N<1 - thinner
$W = 100;  // rhombus width
$K = tan(pi()/6);

$a = (2+$N)/4*$W;
$b = (3-2*$N)/4*$W;

$x = array(-$a, $b, $a, -$b);
$y = array($b*$K, -$a*$K, -$b*$K, $a*$K);
$p = array(
    $x[0].','.$y[0],
    $x[1].','.$y[1],
    $x[2].','.$y[2],
    $x[3].','.$y[3]
);

echo implode(' ', $p);

*/