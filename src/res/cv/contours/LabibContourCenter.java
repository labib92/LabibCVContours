package res.cv.contours;

public class LabibContourCenter {
    private int contourId;

    private int gx;
    private int gy;


    public LabibContourCenter(int contourId, int gx, int gy){
        this.contourId = contourId;
        this.gx = gx;
        this.gy = gy;
    }

    public int getContourId() {
        return contourId;
    }

    public int getGx() {return gx;}

    public int getGy() {return gy;}

    @Override
    public String toString(){
        return "[Contour№"+contourId+": center point("+gx+", "+gy+")]";
    }
}
