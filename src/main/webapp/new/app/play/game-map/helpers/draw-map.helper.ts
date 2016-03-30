import { Injectable } from 'angular2/core';

import { Hex } from 'app/shared/domain/game-map/hex';
import { Node, NodeOrientation } from 'app/shared/domain/game-map/node';
import { Edge } from 'app/shared/domain/game-map/edge';
import { Point } from 'app/shared/domain/game-map/point';
import { NodesPair } from 'app/shared/domain/game-map/nodes-pair';

const ISOMETRIC_RATIO =  Math.tan(Math.PI / 6);

@Injectable()
export class DrawMapHelper {

    getFirstHexOfNode(node: Node) {
        return node.getFirstHex();
    }

    getFirstHexOfEdge(edge: Edge) {
        return edge.getFirstHex();
    }

    getObjectPosition(where: any, what: any) {
        for (let k in where) {
            if (where[k] === what) {
                return k;
            }
        }
    }

    getHexCoords(hex: Hex, hexWidth: number, hexHeight: number) {
        let x = 0;
        let y = 0;

        // x affection
        x = x + (hex.x + hex.y/2) * hexWidth;
        y = y - (hex.x + hex.y/2) * hexWidth * ISOMETRIC_RATIO;

        // y affection
        y = y + hex.y * hexHeight * ISOMETRIC_RATIO;
        x = x + hex.y * hexHeight;

        return new Point(x, y);
    }

    getNodeCoords(node: Node, hexWidth: number, hexHeight: number) {
        let hex = this.getFirstHexOfNode(node);
        let position = this.getObjectPosition(hex.nodes, node);
        let hexCoords = this.getHexCoords(hex, hexWidth, hexHeight);

        let x = hexCoords.x;
        if (position === "topLeft") { x -= hexWidth*3/4; }
        if (position === "top" || position === "bottomLeft") { x -= hexWidth/4; }
        if (position === "topRight" || position === "bottom") { x += hexWidth/4; }
        if (position === "bottomRight") { x += hexWidth*3/4; }

        let y = hexCoords.y;
        if (position === "topRight") { y -= hexHeight*3/2 * ISOMETRIC_RATIO; }
        if (position === "top" || position === "bottomRight") { y -= hexHeight/2 * ISOMETRIC_RATIO; }
        if (position === "topLeft" || position === "bottom") { y += hexHeight/2 * ISOMETRIC_RATIO; }
        if (position === "bottomLeft") { y += hexHeight*3/2 * ISOMETRIC_RATIO; }

        return new Point(x, y);
    }

    getEdgeCoords(edge: Edge, hexWidth: number, hexHeight: number) {
        let hex = this.getFirstHexOfEdge(edge);
        let position = this.getObjectPosition(hex.edges, edge);
        let hexCoords = this.getHexCoords(hex, hexWidth, hexHeight);

        let x = hexCoords.x;
        if (position === "topLeft" || position === "left") { x -= hexWidth/2; }
        if (position === "bottomRight" || position === "right") { x += hexWidth/2; }

        let y = hexCoords.y;
        if (position === "topRight" || position === "right") { y -= hexHeight * ISOMETRIC_RATIO; }
        if (position === "left" || position === "bottomLeft") { y += hexHeight * ISOMETRIC_RATIO; }

        return new Point(x, y);
    }

    getPortOffset(node: Node, portDistance: number) {
        let xDec, yDec;

        if (node.orientation === NodeOrientation.SINGLE_BOTTOM) {
            yDec = (node.hexes.bottom) ? -1 : 1;

            if (node.hexes.topLeft && !node.hexes.topRight) {
                xDec = 1;
            } else if (!node.hexes.topLeft && node.hexes.topRight) {
                xDec = -1;
            } else {
                xDec = 0;
            }
        }

        if (node.orientation === NodeOrientation.SINGLE_TOP) {
            yDec = (node.hexes.top) ? 1 : -1;

            if (node.hexes.bottomLeft && !node.hexes.bottomRight) {
                xDec = 1;
            } else if (!node.hexes.bottomLeft && node.hexes.bottomRight) {
                xDec = -1;
            } else {
                xDec = 0;
            }
        }

        let x = 0;
        let y = 0;

        // x affection
        x = x + xDec * portDistance;
        y = y - xDec * portDistance * ISOMETRIC_RATIO;

        // y affection
        y = y + yDec * portDistance * ISOMETRIC_RATIO;
        x = x + yDec * portDistance;

        return new Point(x, y);
    }

    getPortPairs(nodes: Node[]) {
        let portNodes = nodes
            .filter(node => node.hasPort())
            .sort((a, b) => (a.gridY === b.gridY) ? b.gridX - a.gridX : b.gridY - a.gridY);
        let pairs: NodesPair[] = <NodesPair[]>[];

        while (portNodes.length) {
            let pair: NodesPair = <NodesPair>{};
            pair.firstNode = portNodes.shift();

            portNodes.some((node, i) => {
                let jointEdge = node.getJointEdge(pair.firstNode);
                if (jointEdge) {
                    pair.edge = jointEdge;
                    pair.secondNode = node;
                    portNodes.splice(i, 1);
                    return true;
                }
            });

            pairs.push(pair);
        }

        return pairs;
    }
}