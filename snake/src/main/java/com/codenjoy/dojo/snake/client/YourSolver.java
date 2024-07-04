package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {
    private Board board;

    @Override
    public String get(Board board) {
        return doSolve(board).toString();
    }

    Direction doSolve(Board board) {
        this.board = board;

        List<Point> boardSnake = board.getSnake();
        List<Point> walls = board.getWalls();
        boardSnake.addAll(walls);
        boardSnake.add(board.getStones().get(0));
        LeeApp leeApp = new LeeApp();


        List<PointM> obstacles1 = custPointList(boardSnake); // obstacles

        PointM src = custPoint(board.getHead());             // head
        PointM dst = custPoint(board.getApples().get(0));   // apple
        // optimal
//        int maxSnake = 38;
//        int redirectStone = 29;

        int maxSnake = 42;
        int redirectStone = 30;


        int countWaysHaveToBeApplied = 1;
        PointM nextStep = leeApp.doSolve(src, dst, obstacles1);      // next point if trace exist
        System.out.println(nextStep + "NEXT ");
        int count = 0;

        if (null != nextStep) count = leeApp.countCorners(nextStep, obstacles1);
        if ((null != nextStep) && (board.getSnake().size() < maxSnake) && (count > countWaysHaveToBeApplied)) {
            return nextPointDirection(nextStep);     // to apple
        }
        PointM stone = custPoint(board.getStones().get(0));
        obstacles1.remove(stone);
        if ((board.getSnake().size() > redirectStone)) {        // to stone
            nextStep = leeApp.doSolve(src, stone, obstacles1);
            if (null != nextStep) {
                count = leeApp.countCorners(nextStep, obstacles1);
                if (count > countWaysHaveToBeApplied) return nextPointDirection(nextStep);
            }
        }
        return nextPointDirection(nextStepCheck(leeApp, obstacles1, countWaysHaveToBeApplied));
    }

    public PointM nextStepCheck(LeeApp leeApp, List<PointM> obstacles1, int count) {
        List<PointM> nextStep = neighboursEmpty(obstacles1);
        if (nextStep.isEmpty()) return hitYourself();
        return chooseWay(leeApp, nextStep, obstacles1, count);
    }

    public PointM chooseWay(LeeApp leeApp, List<PointM> way, List<PointM> obstacles, int count) {
        for (PointM point : way) {
            if (leeApp.countCorners(point, obstacles) >= way.size() - (count-1)) return point;
        }
        return chooseWay(leeApp, way, obstacles, ++count);
    }


    public static void main(String[] args) {
        String url = "http://138.197.189.109/codenjoy-contest/board/player/1acm7l421mtjpidda6pb?code=7922101859992520145";

        WebSocketRunner.runClient(
                url,
                new YourSolver(),
                new Board());

    }

    public PointM custPoint(Point p) {
        return new PointM(p.getX(), p.getY());
    }

    public List<PointM> custPointList(List<Point> list) {
        return list.stream().map(this::custPoint).collect(Collectors.toList());
    }

    public Direction nextPointDirection(PointM nextStep) {
        if (board.getHead().getX() > nextStep.getX()) return Direction.LEFT;
        if (board.getHead().getX() < nextStep.getX()) return Direction.RIGHT;
        if (board.getHead().getY() < nextStep.getY()) return Direction.UP;
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


    private List<PointM> neighboursEmpty(List<PointM> obstacles) {  // change to list
        System.out.println("neighboursEmpty check");
        PointM stone = custPoint(board.getStones().get(0));
        obstacles.remove(stone);
        List<PointM> exit = deltas().get()
                .map(d -> custPoint(board.getHead()).move(d.getX(), d.getY()))
                .collect(Collectors.toList());
        return exit.stream().filter(p -> !obstacles.contains(p)).collect(Collectors.toList());
    }


    public PointM hitYourself() {
        System.out.println("hit yourself");
        System.out.println(board.getSnake().get(1));
        return custPoint(board.getSnake().get(1));
    }


}
