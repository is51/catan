import { GameMap } from 'app/shared/domain/game-map/game-map';
import { Hex } from 'app/shared/domain/game-map/hex';
import { Edge } from 'app/shared/domain/game-map/edge';
import { Edges, EdgesIds } from 'app/shared/domain/game-map/edges';
import { Hexes, HexesIds } from 'app/shared/domain/game-map/hexes';

export class Node {
    id: number;
    orientation: NodeOrientation;
    port: NodePort;

    hexesIds: HexesIds;
    hexes: Hexes;
    edgesIds: EdgesIds;
    edges: Edges;

    building: NodeBuilding;

    private _onUpdate: Function;

    constructor(params) {
        this.id = params.nodeId;
        this.orientation = NodeOrientation[params.orientation];
        this.port = NodePort[params.port];

        this.hexesIds = <HexesIds>params.hexesIds;
        this.edgesIds = <EdgesIds>params.edgesIds;
        this.hexes = <Hexes>{};
        this.edges = <Edges>{};

        if (params.building) {
            this.building = <NodeBuilding>{
                built: NodeBuildingType[params.building.built],
                ownerPlayerId: <number>params.building.ownerGameUserId
            };
        }
    }

    linkRelatedEntities(map: GameMap) {
        this._linkEdges(map.edges);
        this._linkHexes(map.hexes);
    }

    private _linkEdges(edges: Edge[]) {
        for (let i in this.edgesIds) {
            let newKey = i.slice(0, -2);
            this.edges[newKey] = edges.filter(edge => edge.id === this.edgesIds[i])[0];
        }
    }

    private _linkHexes(hexes: Hex[]) {
        for (let i in this.hexesIds) {
            let newKey = i.slice(0, -2);
            this.hexes[newKey] = hexes.filter(hex => hex.id === this.hexesIds[i])[0];
        }
    }

    update(params) {
        let buildingChanged =
            (!this.building && params.building) ||
            (this.building && params.building && this.building.built !== NodeBuildingType[params.building.built]);

        if (buildingChanged) {
            this.building = this.building || <NodeBuilding>{};
            this.building.built = NodeBuildingType[params.building.built];
            this.building.ownerPlayerId = params.building.ownerGameUserId;

            this.triggerUpdate();
        }
    }

    getPortToString() {
        return NodePort[this.port];
    }

    hasPort() {
        return this.port !== NodePort.NONE;
    }

    hasSettlement() {
        return this.building.built === NodeBuildingType.SETTLEMENT;
    }

    hasCity() {
        return this.building.built === NodeBuildingType.CITY;
    }

    /*isNeighborOf(node: Node) {
        return !!this.getJointEdge(node);
    }*/

    getJointEdge(node: Node) {
        for (let i in node.edges) {
            for (let j in this.edges) {
                if (node.edges[i] === this.edges[j]) {
                    return node.edges[i];
                }
            }
        }
        return false;
    }

    //TODO: try to replace with Subscribable
    onUpdate(onUpdate: Function) {
        this._onUpdate = onUpdate;
    }
    cancelOnUpdate() {
        this._onUpdate = undefined;
    }
    triggerUpdate() {
        if (this._onUpdate) {
            this._onUpdate();
        }
    }
}

interface NodeBuilding {
    built: NodeBuildingType;
    ownerPlayerId: number;
}

export enum NodeOrientation {
    SINGLE_TOP,
    SINGLE_BOTTOM
}

export enum NodePort {
    BRICK,
    WOOD,
    SHEEP,
    WHEAT,
    STONE,
    ANY,
    NONE
}

enum NodeBuildingType {
    SETTLEMENT,
    CITY
}