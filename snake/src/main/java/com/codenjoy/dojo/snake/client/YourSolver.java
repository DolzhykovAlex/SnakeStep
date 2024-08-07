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
        LeeApp leeApp = new LeeApp();
        return leeApp.nextPoint(board);
    }



    public static void main(String[] args) {
        String url = "http://138.197.189.109/codenjoy-contest/board/player/1acm7l421mtjpidda6pb?code=7922101859992520145";

        WebSocketRunner.runClient(
                url,
                new YourSolver(),
                new Board());

    }




}
