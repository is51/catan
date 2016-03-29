import { GameMap } from 'app/shared/domain/game-map/game-map';
import { Hex } from 'app/shared/domain/game-map/hex';
import { Node } from 'app/shared/domain/game-map/node';
import { Hexes, HexesIds } from 'app/shared/domain/game-map/hexes';
import { Nodes, NodesIds } from 'app/shared/domain/game-map/nodes';

export class Edge {
    id: number;
    orientation: EdgeOrientation;

    hexesIds: HexesIds;
    hexes: Hexes;
    nodesIds: NodesIds;
    nodes: Nodes;

    building: EdgeBuilding;

    private _onUpdate: Function;

    constructor(params) {
        this.id = params.edgeId;
        this.orientation = EdgeOrientation[params.orientation];

        this.hexesIds = <HexesIds>params.hexesIds;
        this.nodesIds = <NodesIds>params.nodesIds;
        this.hexes = <Hexes>{};
        this.nodes = <Nodes>{};

        if (params.building) {
            this.building = <EdgeBuilding>{
                built: EdgeBuildingType[params.building.built],
                ownerPlayerId: params.building.ownerGameUserId
            };
        }
    }

    linkRelatedEntities(map: GameMap) {
        this._linkNodes(map.nodes);
        this._linkHexes(map.hexes);
    }

    private _linkNodes(nodes: Node[]) {
        for (let i in this.nodesIds) {
            let newKey = i.slice(0, -2);
            this.nodes[newKey] = nodes.filter(node => node.id === this.nodesIds[i])[0];
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
            (this.building && params.building && this.building.built !== EdgeBuildingType[params.building.built]);

        if (buildingChanged) {
            this.building = this.building || <EdgeBuilding>{};
            this.building.built = EdgeBuildingType[params.building.built];
            this.building.ownerPlayerId = params.building.ownerGameUserId;

            this.triggerUpdate();
        }
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

interface EdgeBuilding {
    built: EdgeBuildingType;
    ownerPlayerId: number;
}

export enum EdgeOrientation {
    BOTTOM_RIGHT,
    BOTTOM_LEFT,
    VERTICAL
}

enum EdgeBuildingType {
    ROAD
}