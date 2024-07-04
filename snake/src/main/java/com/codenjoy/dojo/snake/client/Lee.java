package com.codenjoy.dojo.snake.client;


import com.codenjoy.dojo.snake.client.colored.Ansi;
import com.codenjoy.dojo.snake.client.colored.Attribute;
import com.codenjoy.dojo.snake.client.colored.Colored;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Lee {
    private static final int EMPTY = 0;
    private static final int START = 1;
    private static final int OBSTACLE = -10;
    private final int width;
    private final int height;
    private final int[][] board;

    public Lee(int width, int height) {
        this.width = width;
        this.height = height;
        this.board = new int[height][width];
    }

    private int get(int x, int y) {
        return board[y][x];
    }

    private void set(int x, int y, int value) {
        board[y][x] = value;
    }

    private int get(PointM p) {
        return get(p.getX(), p.getY());
    }

    private void set(PointM p, int value) {
        set(p.getX(), p.getY(), value);
    }

    private boolean isOnBoard(PointM p) {
        return p.getX() > 0 && p.getX() < (width) && p.getY() > 0 && p.getY() < (height);
    }

    private boolean isUnvisited(PointM p) {
        return get(p) == EMPTY;
    }

    private Supplier<Stream<PointM>> deltas() {
        return () -> Stream.of(
                // actually just dx, dy
                PointM.of(-1, 0),
                PointM.of(0, -1),
                PointM.of(1, 0),
                PointM.of(0, 1)
        );
    }

    private Supplier<Stream<PointM>> cornersBoard() {
        return () -> Stream.of(
                PointM.of(1, 1),
                PointM.of(1, height - 2),
                PointM.of(width - 2, 1),
                PointM.of(width - 2, height - 2)
        );
    }


    private Stream<PointM> neighbours(PointM p) {
        return deltas().get()
                .map(d -> p.move(d.getX(), d.getY()))
                .filter(this::isOnBoard);
    }

    private Stream<PointM> neighboursUnvisited(PointM p) {
        return neighbours(p)
                .filter(this::isUnvisited);
    }

    private Stream<PointM> neighboursByValue(PointM pt, int value) {
        return neighbours(pt)
                .filter(p -> get(p) == value);
    }

    private void initializeBoard(List<PointM> obstacles) {
        IntStream.range(0, height).forEach(y ->
                IntStream.range(0, width).forEach(x ->
                        set(x, y, EMPTY)
                )
        );
        obstacles.forEach(p -> set(p, OBSTACLE));
    }


    String cellFormatted(PointM p, Set<PointM> path) {
        int value = get(p);
        String valueF = String.format("%3d", value);

        if (value == OBSTACLE) {
            Attribute a = new Attribute(Ansi.ColorFont.BLUE);
            return Colored.build(" XX", a);
        }

        if (path.isEmpty()) return valueF;

        if (path.contains(p)) {
            Attribute a = new Attribute(Ansi.ColorFont.RED);
            return Colored.build(valueF, a);
        }

        return valueF;
    }

    public String boardFormatted(Iterable<PointM> path0) {
        Set<PointM> path = StreamSupport
                .stream(path0.spliterator(), false)
                .collect(Collectors.toSet());
        return IntStream.range(0, height)
                .mapToObj(y ->
                        IntStream.range(0, width).mapToObj(x -> PointM.of(x, (height - y - 1)))
                                .map(p -> cellFormatted(p, path))
                                .collect(Collectors.joining())
                )
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return boardFormatted(Set.of());
    }

    public Optional<LinkedList<PointM>> trace(PointM src, PointM dst, List<PointM> obstacles) {
        initializeBoard(obstacles);
        // 1. fill the board
        int[] counter = {START};
        set(src, counter[0]);
        counter[0]++;

        boolean found = false;

        for (Set<PointM> curr = Set.of(src); !(found || curr.isEmpty()); counter[0]++) {
            Set<PointM> next = curr.stream()
                    .flatMap(this::neighboursUnvisited)
                    .collect(Collectors.toSet());
            next.forEach(p -> set(p, counter[0]));
            found = next.contains(dst);
            curr = next;

        }

        // 2. backtrack (reconstruct path)
        if (!found) return Optional.empty();
        LinkedList<PointM> path = new LinkedList<>();
        path.add(dst);
        counter[0]--;
        PointM curr = dst;
        while (counter[0] > START) {
            counter[0]--;
            PointM prev = neighboursByValue(curr, counter[0])
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("impossible!"));
            path.addFirst(prev);
            curr = prev;
        }
        return Optional.of(path);
    }

    public boolean traceCorner(PointM src, PointM dst, List<PointM> obstacles) {
        initializeBoard(obstacles);
        // 1. fill the board
        int[] counter = {START};
        set(src, counter[0]);
        counter[0]++;
        System.out.println("COUNTER " + counter[0]);


        boolean found = false;
        for (Set<PointM> curr = Set.of(src); !(found || curr.isEmpty()); counter[0]++) {
            Set<PointM> next = curr.stream()
                    .flatMap(this::neighboursUnvisited)
                    .collect(Collectors.toSet());
            next.forEach(p -> set(p, counter[0]));
            found = next.contains(dst);
            curr = next;
        }
        System.out.println("found: " + found);
        return found;
    }


    public int count(PointM p, List<PointM> obstacles) {

      int  count = (int) cornersBoard().get()
                .filter(d -> trace(p, d, obstacles).isPresent())
                .count();
        System.out.println("!!!!!count: traces " + count);
        return count;
    }
}