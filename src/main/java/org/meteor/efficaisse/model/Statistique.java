package org.meteor.efficaisse.model;

public class Statistique {

    public Statistique() {
        super();
    }

    String x;
    float y;

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Statistique{" +
                "x='" + x + '\'' +
                ", y=" + y +
                '}';
    }
}
