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
                ownerPlayerId: params.building.ownerGameUserId
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
        if (params.building) {
            this.building = this.building || <NodeBuilding>{};
            this.building.built = NodeBuildingType[params.building.built];
            this.building.ownerPlayerId = params.building.ownerGameUserId;
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