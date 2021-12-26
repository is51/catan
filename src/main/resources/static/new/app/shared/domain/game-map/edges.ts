import { Edge } from 'app/shared/domain/game-map/edge';

export interface EdgesIds {
    topLeftId: number,
    topRightId: number,
    rightId: number,
    bottomRightId: number,
    bottomLeftId: number,
    leftId: number
}

export interface Edges {
    topLeft: Edge,
    topRight: Edge,
    right: Edge,
    bottomRight: Edge,
    bottomLeft: Edge,
    left: Edge
}