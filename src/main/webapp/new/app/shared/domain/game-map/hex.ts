import { GameMap } from 'app/shared/domain/game-map/game-map';
import { Node } from 'app/shared/domain/game-map/node';
import { Edge } from 'app/shared/domain/game-map/edge';
import { Edges, EdgesIds } from 'app/shared/domain/game-map/edges';
import { Nodes, NodesIds } from 'app/shared/domain/game-map/nodes';

export class Hex {
    id: number;
    x: number;
    y: number;
    type: HexType;
    dice: number;
    robbed: boolean;

    edgesIds: EdgesIds;
    edges: Edges;
    nodesIds: NodesIds;
    nodes: Nodes;

    constructor(params) {
        this.id = params.hexId;
        this.x = params.x;
        this.y = params.y;
        this.type = HexType[params.type];
        this.dice = params.dice;
        this.robbed = params.robbed;

        this.edgesIds = <EdgesIds>params.edgesIds;
        this.nodesIds = <NodesIds>params.nodesIds;
        this.edges = <Edges>{};
        this.nodes = <Nodes>{};
    }

    linkRelatedEntities(map: GameMap) {
        this._linkEdges(map.edges);
        this._linkNodes(map.nodes);
    }

    private _linkEdges(edges: Edge[]) {
        for (let i in this.edgesIds) {
            let newKey = i.slice(0, -2);
            this.edges[newKey] = edges.filter(edge => edge.id === this.edgesIds[i])[0];
        }
    }

    private _linkNodes(nodes: Node[]) {
        for (let i in this.nodesIds) {
            let newKey = i.slice(0, -2);
            this.nodes[newKey] = nodes.filter(node => node.id === this.nodesIds[i])[0];
        }
    }

    update(params) {
        this.robbed = params.robbed;
    }

    getTypeToString() {
        return HexType[this.type];
    }
}

enum HexType {
    BRICK,
    WOOD,
    SHEEP,
    WHEAT,
    STONE,
    EMPTY
}

