package res.cv.contours;

public class LabibMin {
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private double distance;

    public LabibMin(int x1, int y1, int x2, int y2, double distance){
        this.x1 = x1;
        this.y1 =y1;
        this.x2 = x2;
        this.y2 = y2;
        this.distance = distance;
    }


    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString(){
        return "Min distance  center point("+x1+","+y1+") --- point("+x2+","+y2+") = "+distance+" pixels";
    }
}
