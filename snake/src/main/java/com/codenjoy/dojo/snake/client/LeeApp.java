package com.codenjoy.dojo.snake.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
        return  lee.count(src, obstacles);


    }

}
