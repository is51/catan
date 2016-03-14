import { Injectable } from 'angular2/core';

import { Hex } from 'app/shared/domain/game-map/hex';
import { Node, NodeOrientation } from 'app/shared/domain/game-map/node';
import { Edge } from 'app/shared/domain/game-map/edge';
import { Point } from 'app/shared/domain/game-map/point';

@Injectable()
export class DrawMapHelper {
    getFirstHexOfNode(node: Node) {
        for (let k in node.hexes) {
            if (node.hexes[k]) {
                return node.hexes[k];
            }
        }
        return null;
    }

    getFirstHexOfEdge(edge: Edge) {
        for (let k in edge.hexes) {
            if (edge.hexes[k]) {
                return edge.hexes[k];
            }
        }
        return null;
    }

    getObjectPosition(where: any, what: any) {
        for (let k in where) {
            if (where[k] === what) {
                return k;
            }
        }
    }

    getHexCoords(hex: Hex, hexWidth: number, hexHeight: number) {
        return <Point>{
            x: hex.x * hexWidth + hex.y * hexWidth / 2,
            y: hex.y * hexHeight
        }
    }

    getNodeCoords(node: Node, hexWidth: number, hexHeight: number) {
        let hex = this.getFirstHexOfNode(node);
        let position = this.getObjectPosition(hex.nodes, node);
        let hexCoords = this.getHexCoords(hex, hexWidth, hexHeight);

        let x = hexCoords.x;
        if (position === "topLeft" || position === "bottomLeft") { x -= Math.round(hexWidth/2); }
        if (position === "topRight" || position === "bottomRight") { x += Math.round(hexWidth/2); }

        var y = hexCoords.y;
        if (position === "topRight" || position === "top" || position === "topLeft") { y -= Math.round(hexHeight/2); }
        if (position === "bottomRight" || position === "bottom" || position === "bottomLeft") { y += Math.round(hexHeight/2); }

        return <Point>{x: x, y: y}
    }

    getEdgeCoords(edge: Edge, hexWidth: number, hexHeight: number) {
        let hex = this.getFirstHexOfEdge(edge);
        let position = this.getObjectPosition(hex.edges, edge);
        let hexCoords = this.getHexCoords(hex, hexWidth, hexHeight);

        let x = hexCoords.x;
        if (position === "left") { x -= Math.round(hexWidth/2); }
        if (position === "topLeft" || position === "bottomLeft") { x -= Math.round(hexWidth/4); }
        if (position === "topRight" || position === "bottomRight") { x += Math.round(hexWidth/4); }
        if (position === "right") { x += Math.round(hexWidth/2); }

        let y = hexCoords.y;
        if (position === "topLeft" || position === "topRight") { y -= Math.round(hexHeight/2); }
        if (position === "bottomLeft" || position === "bottomRight") { y += Math.round(hexHeight/2); }

        return <Point>{x: x, y: y}
    }

    getPortOffset(node: Node) {
        let x, y;

        if (node.orientation === NodeOrientation.SINGLE_BOTTOM) {
            y = (node.hexes.bottom) ? -1 : 1;

            if (node.hexes.topLeft && !node.hexes.topRight) {
                x = 1;
            } else if (!node.hexes.topLeft && node.hexes.topRight) {
                x = -1;
            } else {
                x = 0;
            }
        }

        if (node.orientation === NodeOrientation.SINGLE_TOP) {
            y = (node.hexes.top) ? 1 : -1;

            if (node.hexes.bottomLeft && !node.hexes.bottomRight) {
                x = 1;
            } else if (!node.hexes.bottomLeft && node.hexes.bottomRight) {
                x = -1;
            } else {
                x = 0;
            }
        }

        return <Point>{x: x, y: y};
    }
}