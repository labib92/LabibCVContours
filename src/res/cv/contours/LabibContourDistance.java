package res.cv.contours;

public class LabibContourDistance {
    private int contourId;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private double distance;
    private double maxRadius;


    public LabibContourDistance(int contourId, int x1, int y1, int x2, int y2, double distance, double maxRadius){
        this.contourId = contourId;
        this.x1 = x1;
        this.y1 =y1;
        this.x2 = x2;
        this.y2 = y2;
        this.distance = distance;
        this.maxRadius = maxRadius;
    }

    public int getContourId() {
        return contourId;
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

    public double getMaxRadius() {return maxRadius;}

    @Override
    public String toString(){
        return "[Contour№"+contourId+": max distance point("+x1+","+y1+") --- point("+x2+","+y2+") = "+distance+" pixels," +
                " maximum Radius = "+maxRadius+"]";
    }
}
