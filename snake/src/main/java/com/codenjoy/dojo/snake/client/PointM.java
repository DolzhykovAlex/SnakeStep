package com.codenjoy.dojo.snake.client;


import java.util.Objects;

public class PointM {
    private final int x;
    private final int y;

    public PointM(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }



    public static PointM of(int x, int y) {
        return new PointM(x, y);
    }

    public PointM move(int dx, int dy) {
        return new PointM(x + dx, y + dy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointM pointM = (PointM) o;
        return x == pointM.x && y == pointM.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("[%2d, %2d]", x, y);
        //("[%2d,%2d]".formatted(x,y));

    }
}
