package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LeeApp {


    public PointM doSolve(PointM src, PointM dst, List<PointM> obstacles) {
        Lee lee = new Lee(15, 15);

        Optional<LinkedList<PointM>> trace = lee.trace(src, dst, obstacles);
//        System.out.println(trace);
//        trace.ifPresent(path -> System.out.println(lee.boardFormatted(path)));
        return trace.map(pointMS -> pointMS.get(1)).orElse(null);
    }

    public int countCorners(PointM src, List<PointM> obstacles) {
        Lee lee = new Lee(15, 15);
        return lee.count(src, obstacles);


    }

    public Direction nextPoint(Board board) {

        List<Point> boardSnake = board.getSnake();
        List<Point> walls = board.getWalls();
        boardSnake.addAll(walls);
        boardSnake.add(board.getStones().get(0));
        List<PointM> obstacles1 = custPointList(boardSnake); // obstacles

        PointM src = castPoint(board.getHead());             // head
        PointM dst = castPoint(board.getApples().get(0));   // apple



        int maxSnake = 38;
        int redirectStone = 28;
        int count = 0;
        int countWaysHaveToBeApplied = 1;
        PointM head = castPoint(board.getHead());

        PointM nextStep = doSolve(src, dst, obstacles1);      // next point if trace exist


        if (null != nextStep) count = countCorners(nextStep, obstacles1);
        if ((null != nextStep) && (board.getSnake().size() < maxSnake) && (count > countWaysHaveToBeApplied)) {
            return nextPointDirection(nextStep, head);     // to apple
        }

        PointM stone = castPoint(board.getStones().get(0));
        obstacles1.remove(stone);

        if ((board.getSnake().size() > redirectStone)) {        // to stone
            nextStep = doSolve(src, stone, obstacles1);
            if (null != nextStep) {
                count = countCorners(nextStep, obstacles1);
                if (count > countWaysHaveToBeApplied) return nextPointDirection(nextStep, head);
            }
        }
        PointM snake = castPoint(board.getSnake().get(1));
        return nextPointDirection(nextStepCheck(obstacles1, countWaysHaveToBeApplied,head,snake), head);

    }

    public PointM nextStepCheck(List<PointM> obstacles1, int count, PointM head,PointM snake) {
        List<PointM> nextStep = neighboursEmpty(obstacles1,head);
        if (nextStep.isEmpty()) return snake;
        return chooseWay(nextStep, obstacles1, count);
    }

    public PointM chooseWay(List<PointM> way, List<PointM> obstacles, int count) {
        for (PointM point : way) {
            if (countCorners(point, obstacles) >= way.size() - (count - 1)) return point;
        }
        return chooseWay(way, obstacles, ++count);


    }

    public PointM castPoint(Point p) {
        return new PointM(p.getX(), p.getY());
    }

    public List<PointM> custPointList(List<Point> list) {
        return list.stream().map(this::castPoint).collect(Collectors.toList());
    }

    public Direction nextPointDirection(PointM nextStep, PointM head) {
        if (head.getX() > nextStep.getX()) return Direction.LEFT;
        if (head.getX() < nextStep.getX()) return Direction.RIGHT;
        if (head.getY() < nextStep.getY()) return Direction.UP;
        else return Direction.DOWN;

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


    private List<PointM> neighboursEmpty(List<PointM> obstacles, PointM head) {

        List<PointM> exit = deltas().get()
                .map(d -> head.move(d.getX(), d.getY()))
                .collect(Collectors.toList());
        return exit.stream().filter(p -> !obstacles.contains(p)).collect(Collectors.toList());
    }


}
