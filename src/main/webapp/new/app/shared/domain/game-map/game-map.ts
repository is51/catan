import { Hex } from 'app/shared/domain/game-map/hex';
import { Node } from 'app/shared/domain/game-map/node';
import { Edge } from 'app/shared/domain/game-map/edge';

export class GameMap {
    hexes: Hex[];
    nodes: Node[];
    edges: Edge[];

    constructor(params) {
        this.hexes = params.hexes.map(hexParams => new Hex(hexParams));
        this.nodes = params.nodes.map(nodeParams => new Node(nodeParams));
        this.edges = params.edges.map(edgeParams => new Edge(edgeParams));

        this.hexes.forEach(hex => hex.linkRelatedEntities(this));
        this.nodes.forEach(node => node.linkRelatedEntities(this));
        this.edges.forEach(edge => edge.linkRelatedEntities(this));
    }

    update(params) {
        this.hexes.forEach((hex, key) => hex.update(params.hexes[key]));
        this.nodes.forEach((node, key) => node.update(params.nodes[key]));
        this.edges.forEach((edge, key) => edge.update(params.edges[key]));
    }
}