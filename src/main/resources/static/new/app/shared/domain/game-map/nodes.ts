import { Node } from 'app/shared/domain/game-map/node';

export interface NodesIds {
    topLeftId: number,
    topId: number,
    topRightId: number,
    bottomRightId: number,
    bottomId: number;
    bottomLeftId: number,
}

export interface Nodes {
    topLeft: Node,
    top: Node,
    topRight: Node,
    bottomRight: Node,
    bottom: Node,
    bottomLeft: Node
}