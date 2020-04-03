import { Hex } from 'app/shared/domain/game-map/hex';

export interface HexesIds {
    topId: number,
    topLeftId: number,
    topRightId: number,
    rightId: number,
    bottomRightId: number,
    bottomLeftId: number,
    bottomId: number,
    leftId: number
}

export interface Hexes {
    top: Hex,
    topLeft: Hex,
    topRight: Hex,
    right: Hex,
    bottomRight: Hex,
    bottomLeft: Hex,
    bottom: Hex,
    left: Hex
}