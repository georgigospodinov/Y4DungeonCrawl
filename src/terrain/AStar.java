package terrain;

import terrain.grid.Cell;
import terrain.grid.CellManager;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;

public class AStar {
    private CellManager cellManager;
    private Cell start, end;
    private LinkedHashSet<Cell> visited = new LinkedHashSet<>();

    private class Node {
        Node parent;
        Cell cell;
        int cost, estimate;

        Node(Node parent, Cell cell) {
            this.parent = parent;
            this.cell = cell;
            this.estimate = cell.distance(end);

            if (parent == null)
                this.cost = 1;
            else this.cost = parent.cost + 1;
        }

        int getTotal() {
            return cost + estimate;
        }

        boolean isSolution() {
            return this.cell.equals(end);
        }

        int length() {
            int l = 0;

            for (Node n = parent; n != null; n = n.parent)
                l++;

            return l;
        }
    }

    public void setStartAndEnd(Cell start, Cell end) {
        this.start = start;
        this.end = end;
        visited.clear();
    }

    public AStar(Dungeon d) {
        this.cellManager = d.cellManager;
    }

    public int shortestPath() {
        PriorityQueue<Node> nodes = new PriorityQueue<>(Comparator.comparingInt(Node::getTotal));
        Node init = new Node(null, start);

        for (Node current = init; current != null; current = nodes.poll()) {
            if (visited.contains(current.cell)) continue;

            if (current.isSolution()) return current.length();

            Cell c = current.cell;
            visited.add(c);

            Cell north = new Cell(c.x, c.y - 1);
            if (cellManager.isFree(north))
                nodes.add(new Node(current, north));

            Cell south = new Cell(c.x, c.y + 1);
            if (cellManager.isFree(south))
                nodes.add(new Node(current, south));

            Cell east = new Cell(c.x + 1, c.y);
            if (cellManager.isFree(east))
                nodes.add(new Node(current, east));

            Cell west = new Cell(c.x - 1, c.y);
            if (cellManager.isFree(west))
                nodes.add(new Node(current, west));
        }

        return -1;
    }

    public Cell firstStep() {
        PriorityQueue<Node> nodes = new PriorityQueue<>(Comparator.comparingInt(Node::getTotal));
        Node init = new Node(null, start);

        Node current;
        for (current = init; current != null; current = nodes.poll()) {
            if (visited.contains(current.cell)) continue;

            if (current.isSolution()) break;

            Cell c = current.cell;
            visited.add(c);

            Cell north = new Cell(c.x, c.y - 1);
            if (cellManager.isFree(north))
                nodes.add(new Node(current, north));

            Cell south = new Cell(c.x, c.y + 1);
            if (cellManager.isFree(south))
                nodes.add(new Node(current, south));

            Cell east = new Cell(c.x + 1, c.y);
            if (cellManager.isFree(east))
                nodes.add(new Node(current, east));

            Cell west = new Cell(c.x - 1, c.y);
            if (cellManager.isFree(west))
                nodes.add(new Node(current, west));
        }

        if (current == null) return null;

        Cell prev = current.cell;
        while (!current.cell.equals(start)) {
            prev = current.cell;
            current = current.parent;
        }

        return prev;
    }
}
