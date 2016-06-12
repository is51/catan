import { Node } from 'app/shared/domain/game-map/node';
import { Edge } from 'app/shared/domain/game-map/edge';

export interface NodesPair {
    firstNode: Node;
    secondNode: Node;
    edge: Edge;
}