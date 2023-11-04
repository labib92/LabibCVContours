package res.cv.contours;

import processing.core.PApplet;
import processing.core.PImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LabibDrawExternalContours extends PApplet {
    BufferedImage image = ImageIO.read(new File("C:\\Users\\Labib\\Desktop\\13 FigureNew\\FigureEdgesErison.png"));
    PImage resultImage;
    int width = image.getWidth();
    int height = image.getHeight();

    int[] imageArray;
    List<LabibContourCenter> contourCenterArrayList;
    List<ContourLabib.Contour> contours;
    List<ContourLabib.Contour> externalContours;
    List<LabibContourDistance> contourDistancesArrayList;
    List<LabibContourMinDistanceFromCenter> contourDistanceFromCentersArrayList;

    List<Point> radiusPoint1ArrayList;
    List<Point> radiusPoint2ArrayList;
    int WIDTH = width;
    int W_GRID;
    float NOISE = 0.5f;
    double imageVolume;
    double impurityConcentration;


    public LabibDrawExternalContours() throws IOException {}

    public static void main(String[] args) {
        PApplet.main("res.cv.contours.LabibDrawExternalContours", args);
    }

    @Override
    public void setup(){
        try {
            imageArray = imageTo1DArray(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        resultImage = loadImage("C:\\Users\\Labib\\Desktop\\13 FigureNew\\figures.png");
        textureMode(NORMAL);

        W_GRID = width/ WIDTH;
        contours = new ContourLabib().findContours(imageArray, WIDTH, height);
        externalContours = new ArrayList<ContourLabib.Contour>();


        //Get External Contours
        for (ContourLabib.Contour contour : contours) {
            if (contour.parent == 0 & contour.points.size() > 95) {
                externalContours.add(contour);
            }
        }

        //Get All Contours
        //externalContours.addAll(contours);

        //Labib Method
        /*maxDistancePointInContours(externalContours);
        contourCenter(contourDistancesArrayList);
        contourRadius(externalContours, contourCenterArrayList);
        printAllContours(externalContours);*/

        //Labib - Khlopin Method
        /*maxDistancePointInContoursOptimized(externalContours);
        contourCenter(contourDistancesArrayList);
        contourRadiusOptimized(externalContours , contourCenterArrayList);
        printAllContours(externalContours);*/

        //Labib - Khlopin Method 2
        maxDistancePointInContoursOptimized2(externalContours);
        contourCenter(contourDistancesArrayList);
        contourRadiusOptimized(externalContours, contourCenterArrayList);
        printAllContours(externalContours);

        //Labib - Khlopin Method 3
        /*maxDistancePointInContoursOptimized3(externalContours);
        contourCenter(contourDistancesArrayList);
        contourRadiusOptimized(externalContours, contourCenterArrayList);
        printAllContours(externalContours);*/

    }

    @Override
    public void settings(){
        size(width, height);
    }

    @Override
    public void draw(){
        image(resultImage,0,0, width, height);
        int counter = 1;

        // draw the contours!
        beginShape();
        for(int i = 0; i < externalContours.size(); i++){
            noFill();
            strokeWeight(4);
            stroke(255,0,0);
            //find ALL contours
            beginShape();
            for (int j = 0; j < externalContours.get(i).points.size() - 1; j++){
                int x = externalContours.get(i).points.get(j).x;
                int y = externalContours.get(i).points.get(j).y;
                int xNext = externalContours.get(i).points.get((j+1)%contours.get(i).points.size()).x;
                int yNext = externalContours.get(i).points.get((j+1)%contours.get(i).points.size()).y;

                vertex(x * W_GRID + W_GRID * (1-NOISE)/2 + W_GRID* NOISE * noise((float) i, (float) j, (float) (frameCount * 0.1)),
                        y * W_GRID + W_GRID * (1-NOISE)/2 + W_GRID * NOISE* noise((float) i, (float) (j+1), (float) (frameCount * 0.1)));

            }
            endShape(CLOSE);

            //Maximum contour diameter
            noFill();
            strokeWeight(5);
            stroke(255,128,0);
            beginShape();
            vertex(contourDistancesArrayList.get(i).getX1(), contourDistancesArrayList.get(i).getY1());
            vertex(contourDistancesArrayList.get(i).getX2(), contourDistancesArrayList.get(i).getY2());
            endShape(CLOSE);

            //Contour Radius
            noFill();
            strokeWeight(5);
            stroke(255,128,255);
            beginShape();
            vertex(contourDistanceFromCentersArrayList.get(i).getX1(), contourDistanceFromCentersArrayList.get(i).getY1());
            vertex(contourDistanceFromCentersArrayList.get(i).getX2(), contourDistanceFromCentersArrayList.get(i).getY2());
            endShape(CLOSE);
       /*     noFill();
            strokeWeight(5);
            stroke(181,230,29);
            beginShape();
            vertex(contourCenterArrayList.get(i).getGx(), contourCenterArrayList.get(i).getGy());
            vertex(radiusPoint1ArrayList.get(i).x, radiusPoint1ArrayList.get(i).y);
            endShape(CLOSE);

            noFill();
            strokeWeight(5);
            stroke(255,0,128);
            beginShape();
            vertex(contourCenterArrayList.get(i).getGx(), contourCenterArrayList.get(i).getGy());
            vertex(radiusPoint2ArrayList.get(i).x, radiusPoint2ArrayList.get(i).y);
            endShape(CLOSE);*/

            //display PARENT ID > SELF ID for ALL contourst
            fill(0,0,0);
            strokeWeight(100);
            textSize(25);
            text(externalContours.get(i).parent+">"+externalContours.get(i).id,externalContours.get(i).points.get(0).x*W_GRID+W_GRID/2,externalContours.get(i).points.get(0).y*W_GRID+W_GRID/2);
            //text(counter,externalContours.get(i).points.get(0).x*W_GRID+W_GRID/2,externalContours.get(i).points.get(0).y*W_GRID+W_GRID/2);
            counter++;
            //Display Center
            fill(0,0,0);
            strokeWeight(5);
          /*  text("("+contourCenterArrayList.get(i).getGx()+","+contourCenterArrayList.get(i).getGy()+")",
                    contourCenterArrayList.get(i).getGx(),contourCenterArrayList.get(i).getGy());*/

        }

       /* strokeWeight(100);
        textSize(100);
        fill(255,255,0);
        text("Image Size: "+width+"x"+height,0,130);
        text("Total pixels: "+ width*height,0,290);
        text("All object detected = "+ externalContours.size(),0,470);
        text("Impurity Concentration = "+impurityConcentration+" % VOL",0,600);*/

        save("C:\\Users\\Labib\\Desktop\\c1external.png");
    }

    public int[] imageTo1DArray(BufferedImage image) throws IOException {
        int[][] grayscaleImage = ImageUtils.GSArray(image);
        int[] array = Stream.of(grayscaleImage).flatMapToInt(IntStream::of).toArray();
        for(int i =0; i < array.length; i++){
            if(array[i] != 0){
                array[i] = 1;
            }
        }
        return array;
    }

    static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow((x2 - x1),2) + Math.pow((y2 - y1),2));
    }

    public void contourCenter(List<LabibContourDistance> contourDistances){
        contourCenterArrayList = new ArrayList<LabibContourCenter>();
        int gx;
        int gy;
        for(int i = 0; i < contourDistances.size(); i++){
            int x1 = contourDistances.get(i).getX1();
            int y1 = contourDistances.get(i).getY1();
            int x2 = contourDistances.get(i).getX2();
            int y2 = contourDistances.get(i).getY2();
            gx = (x1 + x2)/2;
            gy = (y1 + y2)/2;
            contourCenterArrayList.add(new LabibContourCenter(contourDistances.get(i).getContourId(), gx, gy));
        }
        System.out.println(contourCenterArrayList);
    }

    public void maxDistancePointInContours(List<ContourLabib.Contour> contours){
        double maxDistance;
        int xFirstMax;
        int yFirstMax;
        int xMax;
        int yMax;
        int counterSteps = 0;
        int contourSize =0;
        contourDistancesArrayList = new ArrayList<LabibContourDistance>();
        List<LabibMax> contoursDistanceFirstArrayList = new ArrayList<LabibMax>();
        List<Point> contourArrayList;
        TreeSet<LabibMin> maxDistanceTreeSet;
        for(int i =0; i < contours.size() ; i++) {
            contourArrayList = new ArrayList<Point>();
            maxDistanceTreeSet = new TreeSet<LabibMin>(new Comparator<LabibMin>() {
                @Override
                public int compare(LabibMin o1, LabibMin o2) {
                    if (o1.getDistance() > o2.getDistance()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            for (int j = 0; j < contours.get(i).points.size(); j++) {
                contourArrayList.add(new Point(contours.get(i).points.get(j).x, contours.get(i).points.get(j).y));

            }

            for (int j = 0; j < contourArrayList.size() - 1; j++) {
                int xFirst = contourArrayList.get(j).x;
                int yFirst = contourArrayList.get(j).y;
                for (int k = j + 1; k < contourArrayList.size(); k++) {
                    int x = contours.get(i).points.get(k).x;
                    int y = contours.get(i).points.get(k).y;
                    maxDistance = distance(xFirst, yFirst, x, y);
                    xFirstMax = xFirst;
                    yFirstMax = yFirst;
                    xMax = x;
                    yMax = y;
                    maxDistanceTreeSet.add(new LabibMin(xFirstMax, yFirstMax, xMax, yMax, maxDistance));
                    counterSteps++;
                }
            }

            contourDistancesArrayList.add(new LabibContourDistance(contours.get(i).id,
                    maxDistanceTreeSet.last().getX1(), maxDistanceTreeSet.last().getY1(),
                    maxDistanceTreeSet.last().getX2(), maxDistanceTreeSet.last().getY2(),
                    maxDistanceTreeSet.last().getDistance(), maxDistanceTreeSet.last().getDistance() / 2));
            maxDistanceTreeSet.clear();
            contourArrayList.clear();
            contourSize = (int) (contourSize + Math.pow(contours.get(i).points.size(), 2));
        }
        System.out.println("Contour size Optimal method to enumerate all points:" + contourSize);
        System.out.println("Number of Max Distance Point In Contours Iterations = "+counterSteps+" Steps");
    }

    public void maxDistancePointInContoursOptimized(List<ContourLabib.Contour> contours){
        double maxDistance;
        int xFirstMax;
        int yFirstMax;
        int xMax;
        int yMax;
        int xMaxLeft;
        int yMaxLeft;
        int xMaxRight;
        int yMaxRight;

        int xMaxPlus1;
        int yMaxPlus1;
        int xMaxLeftPlus1;
        int yMaxLeftPlus1;
        int xMaxRightPlus1;
        int yMaxRightPlus1;

        int xMaxMinus1;
        int yMaxMinus1;
        int xMaxLeftMinus1;
        int yMaxLeftMinus1;
        int xMaxRightMinus1;
        int yMaxRightMinus1;

        int xFirstMaxPlus1;
        int yFirstMaxPlus1;
        int xFirstMaxMinus1;
        int yFirstMaxMinus1;

        double distanceLeft;
        double distanceRight;

        int counterSteps = 0;

        TreeSet<LabibMax> maxDistanceTreeSet;
        contourDistancesArrayList = new ArrayList<LabibContourDistance>();
        for(int i =0; i < contours.size() ; i++){
            maxDistanceTreeSet = new TreeSet<LabibMax>(new Comparator<LabibMax>() {
                @Override
                public int compare(LabibMax o1, LabibMax o2) {
                    if(o1.getDistance() > o2.getDistance()){
                        return 1;
                    }else {
                        return -1;
                    }
                }
            });

            for(int j = 0; j < contours.get(i).points.size()/2; j++){
                xFirstMax = contours.get(i).points.get(j).x;
                yFirstMax = contours.get(i).points.get(j).y;

                xMax = contours.get(i).points.get(j + (contours.get(i).points.size()/2)).x;
                yMax = contours.get(i).points.get(j + (contours.get(i).points.size()/2)).y;

                xMaxLeft = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) - (contours.get(i).points.size()/4)))%contours.get(i).points.size()).x;
                yMaxLeft = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) - (contours.get(i).points.size()/4)))%contours.get(i).points.size()).y;
                xMaxLeftPlus1 = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) - (contours.get(i).points.size()/4) + 1 ))%contours.get(i).points.size()).x;
                yMaxLeftPlus1 = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) - (contours.get(i).points.size()/4) + 1))%contours.get(i).points.size()).y;
                xMaxLeftMinus1 = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) - (contours.get(i).points.size()/4) - 1))%contours.get(i).points.size()).x;
                yMaxLeftMinus1 = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) - (contours.get(i).points.size()/4) - 1))%contours.get(i).points.size()).y;


                if(j < (contours.get(i).points.size()/2) - 1){
                    xFirstMaxPlus1 = contours.get(i).points.get(j+1).x;
                    yFirstMaxPlus1 = contours.get(i).points.get(j+1).y;
                }else {
                    xFirstMaxPlus1 = contours.get(i).points.get((contours.get(i).points.size() / 2) + 1).x;
                    yFirstMaxPlus1 = contours.get(i).points.get((contours.get(i).points.size()/2) + 1).y;
                }

                if(j > 1){
                    xFirstMaxMinus1 = contours.get(i).points.get(j-1).x;
                    yFirstMaxMinus1 = contours.get(i).points.get(j-1).y;
                }else {
                    xFirstMaxMinus1 = contours.get(i).points.get(contours.get(i).points.size() - 1).x;
                    yFirstMaxMinus1 = contours.get(i).points.get(contours.get(i).points.size() - 1).y;
                }

                if(j < (contours.get(i).points.size()/2) - 1){
                    xMaxPlus1  = contours.get(i).points.get((j + (contours.get(i).points.size()/2)) + 1).x;
                    yMaxPlus1  = contours.get(i).points.get((j + (contours.get(i).points.size()/2)) + 1).y;
                    xMaxMinus1 = contours.get(i).points.get((j + (contours.get(i).points.size()/2)) - 1).x;
                    yMaxMinus1 = contours.get(i).points.get((j + (contours.get(i).points.size()/2)) - 1 ).y;
                }else {
                    xMaxPlus1  = contours.get(i).points.get(1).x;;
                    yMaxPlus1  = contours.get(i).points.get(1).y;
                    xMaxMinus1 = contours.get(i).points.get(contours.get(i).points.size() - 2).x;
                    yMaxMinus1 = contours.get(i).points.get(contours.get(i).points.size() - 2).y;
                }

                xMaxRight = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) + (contours.get(i).points.size()/4)))%contours.get(i).points.size()).x;
                yMaxRight = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) + (contours.get(i).points.size()/4)))%contours.get(i).points.size()).y;
                xMaxRightPlus1 = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) + (contours.get(i).points.size()/4) + 1 ))%contours.get(i).points.size()).x;
                yMaxRightPlus1 = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) + (contours.get(i).points.size()/4) + 1))%contours.get(i).points.size()).y;
                xMaxRightMinus1 = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) + (contours.get(i).points.size()/4) - 1))%contours.get(i).points.size()).x;
                yMaxRightMinus1 = contours.get(i).points
                        .get((j + ((contours.get(i).points.size()/2) + (contours.get(i).points.size()/4) - 1))%contours.get(i).points.size()).y;

                //Calculate Distance
                maxDistance = distance(xFirstMax, yFirstMax, xMax , yMax);
                distanceLeft = distance(xFirstMax , yFirstMax , xMaxLeft , yMaxLeft);
                distanceRight = distance(xFirstMax, yFirstMax, xMaxRight, yMaxRight);

                //1. Check right and left point and choose the maximum
                if(distanceLeft > maxDistance && distanceLeft > distanceRight) {
                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxLeft, yMaxLeft, distanceLeft));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxLeftPlus1, yMaxLeftPlus1,
                            distance(xFirstMax, yFirstMax, xMaxLeftPlus1, yMaxLeftPlus1)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxLeftMinus1, yMaxLeftMinus1,
                            distance(xFirstMax, yFirstMax, xMaxLeftMinus1, yMaxLeftMinus1)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMaxPlus1, yFirstMaxPlus1, xMaxLeft, yMaxLeft,
                            distance(xFirstMaxPlus1, yFirstMaxPlus1, xMaxLeft, yMaxLeft)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMaxMinus1, yFirstMaxMinus1, xMaxLeft, yMaxLeft,
                            distance(xFirstMaxMinus1, yFirstMaxMinus1, xMaxLeft, yMaxLeft)));

                }else if (distanceRight > maxDistance && distanceRight > distanceLeft){
                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxRight, yMaxRight, distanceRight));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxRightPlus1, yMaxRightPlus1,
                            distance(xFirstMax, yFirstMax, xMaxRightPlus1, yMaxRightPlus1)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxRightMinus1, yMaxRightMinus1,
                            distance(xFirstMax, yFirstMax, xMaxRightMinus1, yMaxRightMinus1)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMaxPlus1, yFirstMaxPlus1, xMaxRight, yMaxRight,
                            distance(xFirstMaxPlus1, yFirstMaxPlus1, xMaxRight, yMaxRight)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMaxMinus1, yFirstMaxMinus1, xMaxRight, yMaxRight,
                            distance(xFirstMaxMinus1, yFirstMaxMinus1, xMaxRight, yMaxRight)));
                }else {
                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMax, yMax, maxDistance));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxPlus1, yMaxPlus1,
                            distance(xFirstMax, yFirstMax, xMaxPlus1, yMaxPlus1)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxMinus1, yMaxMinus1,
                            distance(xFirstMax, yFirstMax, xMaxMinus1, yMaxMinus1)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMaxPlus1, yFirstMaxPlus1, xMax, yMax,
                            distance(xFirstMaxPlus1, yFirstMaxPlus1, xMax, yMax)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMaxMinus1, yFirstMaxMinus1, xMax, yMax,
                            distance(xFirstMaxMinus1, yFirstMaxMinus1, xMax, yMax)));
                }
                counterSteps++;
            }

            contourDistancesArrayList.add(new LabibContourDistance(contours.get(i).id,
                    maxDistanceTreeSet.last().getX1(), maxDistanceTreeSet.last().getY1(),
                    maxDistanceTreeSet.last().getX2(), maxDistanceTreeSet.last().getY2(),
                    maxDistanceTreeSet.last().getDistance(), maxDistanceTreeSet.last().getDistance()/2));

            maxDistanceTreeSet.clear();
        }
        System.out.println("Number of Max Distance Point In Contours Optimized Iterations = "+counterSteps + " Steps");
        System.out.println(contourDistancesArrayList);
    }

    ///////////////////////
    //Function one contour
    ///////////////////////
    private static int maxDistanceNeighbour(List<ContourLabib.Contour> contours, int i  ,double maxDistance, int firstIndex , int secondIndex){
        boolean goLeft = false, goRight = false;
        int startIndex = 0;

        for(int j =0; j < contours.get(i).points.size(); j++) {
            if (secondIndex < contours.get(i).points.size() && firstIndex < contours.get(i).points.size() && secondIndex > 0) {
                double maxDistanceRight = distance(contours.get(i).points.get(firstIndex).x, contours.get(i).points.get(firstIndex).y, contours.get(i).points.get((secondIndex + 1) % contours.get(i).points.size()).x, contours.get(i).points.get((secondIndex + 1) % contours.get(i).points.size()).y);
                double maxDistanceLeft = distance(contours.get(i).points.get(firstIndex).x, contours.get(i).points.get(firstIndex).y, contours.get(i).points.get((secondIndex - 1) % contours.get(i).points.size()).x, contours.get(i).points.get((secondIndex - 1) % contours.get(i).points.size()).y);
                if (maxDistance < maxDistanceRight) {
                    goLeft = false;
                    goRight = true;
                    secondIndex = (secondIndex + 1)%contours.get(i).points.size();  //mod!!!
                    maxDistance = maxDistanceRight;

                }
                if (maxDistance < maxDistanceLeft) {
                    goLeft = true;
                    goRight = false;
                    secondIndex = (secondIndex - 1)%contours.get(i).points.size();  //mod!!!
                    maxDistance = maxDistanceLeft;
                }

                while (goLeft || goRight) {
                    if (goLeft) {
                        if (secondIndex - 1 > 0) {
                            maxDistanceLeft = distance(contours.get(i).points.get(firstIndex).x, contours.get(i).points.get(firstIndex).y, contours.get(i).points.get((secondIndex - 1) % contours.get(i).points.size()).x, contours.get(i).points.get((secondIndex - 1) % contours.get(i).points.size()).y);
                        }

                        if (maxDistance <= maxDistanceLeft) {
                            goLeft = true;
                            secondIndex = (secondIndex - 1);   //mod!!!
                            if(secondIndex < 0){
                                secondIndex = contours.get(i).points.size() -1;
                            }
                            maxDistance = maxDistanceLeft;
                        } else {
                            goLeft = false;
                        }
                    }
                    if (goRight) {

                        maxDistanceRight = distance(contours.get(i).points.get(firstIndex).x, contours.get(i).points.get(firstIndex).y, contours.get(i).points.get((secondIndex + 1)%contours.get(i).points.size()).x, contours.get(i).points.get((secondIndex + 1)%contours.get(i).points.size()).y);

                        if (maxDistance <= maxDistanceRight) {
                            goRight = true;
                            secondIndex = (secondIndex + 1)%contours.get(i).points.size(); //mod!!!
                            maxDistance = maxDistanceRight;
                        } else {
                            goRight = false;
                        }
                    }
                }
            }
        }

        return  secondIndex;
    }

    private static int maxDistanceNeighbour2(List<ContourLabib.Contour> contours ,double maxDistance, int firstIndex , int secondIndex){
        boolean goLeft = false, goRight = false;

        for(int i =0; i < contours.size() ; i++){
            for(int j =0; j < contours.get(i).points.size(); j++) {
                double maxDistanceRight = distance(contours.get(i).points.get(firstIndex % contours.get(i).points.size()).x, contours.get(i).points.get(firstIndex % contours.get(i).points.size()).y, contours.get(i).points.get((secondIndex + 1) % contours.get(i).points.size()).x, contours.get(i).points.get((secondIndex + 1) % contours.get(i).points.size()).y);
                double maxDistanceLeft;
                if(secondIndex < 1){
                    maxDistanceLeft = distance(contours.get(i).points.get(firstIndex % contours.get(i).points.size()).x, contours.get(i).points.get(firstIndex % contours.get(i).points.size()).y, contours.get(i).points.get(contours.get(i).points.size() - 1).x, contours.get(i).points.get(contours.get(i).points.size() - 1).y);
                }else{
                    maxDistanceLeft = distance(contours.get(i).points.get(firstIndex % contours.get(i).points.size()).x, contours.get(i).points.get(firstIndex % contours.get(i).points.size()).y, contours.get(i).points.get((secondIndex - 1) % contours.get(i).points.size()).x, contours.get(i).points.get((secondIndex - 1) % contours.get(i).points.size()).y);
                }

                if (maxDistance < maxDistanceRight) {
                    goLeft = false;
                    goRight = true;
                    secondIndex = (secondIndex + 1)%contours.get(i).points.size();  //mod!!!
                    maxDistance = maxDistanceRight;

                }
                if (maxDistance < maxDistanceLeft) {
                    goLeft = true;
                    goRight = false;
                    secondIndex = (secondIndex - 1);  //mod!!!
                    if(secondIndex < 0){
                        secondIndex = contours.get(i).points.size() - 1;
                    }
                    maxDistance = maxDistanceLeft;
                }

                while (goLeft || goRight) {
                    if (goLeft) {
                        if (secondIndex - 1 > 0) {
                            maxDistanceLeft = distance(contours.get(i).points.get(firstIndex%contours.get(i).points.size()).x, contours.get(i).points.get(firstIndex%contours.get(i).points.size()).y, contours.get(i).points.get((secondIndex - 1) % contours.get(i).points.size()).x, contours.get(i).points.get((secondIndex - 1) % contours.get(i).points.size()).y);
                        }

                        if (maxDistance <= maxDistanceLeft) {
                            goLeft = true;
                            secondIndex = (secondIndex - 1);   //mod!!!
                            if(secondIndex < 0){
                                secondIndex = contours.get(i).points.size() - 1;
                            }
                            maxDistance = maxDistanceLeft;
                        } else {
                            goLeft = false;
                        }
                    }
                    if (goRight) {

                        maxDistanceRight = distance(contours.get(i).points.get(firstIndex%contours.get(i).points.size()).x, contours.get(i).points.get(firstIndex%contours.get(i).points.size()).y, contours.get(i).points.get((secondIndex + 1)%contours.get(i).points.size()).x, contours.get(i).points.get((secondIndex + 1)%contours.get(i).points.size()).y);

                        if (maxDistance <= maxDistanceRight) {
                            goRight = true;
                            secondIndex = (secondIndex + 1)%contours.get(i).points.size(); //mod!!!
                            maxDistance = maxDistanceRight;
                        } else {
                            goRight = false;
                        }
                    }
                }
            }

        }
        return  secondIndex;
    }
    public void maxDistancePointInContoursOptimized2(List<ContourLabib.Contour> contours){
        double maxDistance;
        int xFirstMax;
        int yFirstMax;
        int xMax;
        int yMax;
        int xMaxLeft;
        int yMaxLeft;

        double distanceLeft;

        int counterSteps = 0;
        int contourSize = 0;

        TreeSet<LabibMax> maxDistanceTreeSet;
        contourDistancesArrayList = new ArrayList<LabibContourDistance>();
        for(int i =0; i < contours.size() ; i++){
            maxDistanceTreeSet = new TreeSet<LabibMax>(new Comparator<LabibMax>() {
                @Override
                public int compare(LabibMax o1, LabibMax o2) {
                    if(o1.getDistance() > o2.getDistance()){
                        return 1;
                    }else {
                        return -1;
                    }
                }
            });

            for(int j = 0; j < contours.get(i).points.size(); j++){
                if( j < contours.get(i).points.size()/2){
                    xFirstMax = contours.get(i).points.get(j).x;
                    yFirstMax = contours.get(i).points.get(j).y;

                    xMax = contours.get(i).points.get(j + (contours.get(i).points.size()/2)).x;
                    yMax = contours.get(i).points.get(j + (contours.get(i).points.size()/2)).y;

                    //Calculate Distance
                    maxDistance = distance(xFirstMax, yFirstMax, xMax , yMax);

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMax, yMax, maxDistance));

                    counterSteps++;
                }

                xFirstMax = contours.get(i).points.get(j).x;
                yFirstMax = contours.get(i).points.get(j).y;

                xMaxLeft = contours.get(i).points.get((j + (contours.get(i).points.size()/4))%contours.get(i).points.size()).x;
                yMaxLeft = contours.get(i).points.get((j + (contours.get(i).points.size()/4))%contours.get(i).points.size()).y;

                //Calculate Distance
                distanceLeft = distance(xFirstMax , yFirstMax , xMaxLeft , yMaxLeft);

                maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxLeft, yMaxLeft, distanceLeft));

                counterSteps++;
            }

            //Проверка соседних пексилей
            /*int x1MaxOptimized = maxDistanceTreeSet.last().getX1();
            int y1MaxOptimized = maxDistanceTreeSet.last().getY1();
            int x2MaxOptimized = maxDistanceTreeSet.last().getX2();
            int y2MaxOptimized = maxDistanceTreeSet.last().getY2();
            maxDistance = distance(x1MaxOptimized ,y1MaxOptimized , x2MaxOptimized , y2MaxOptimized);

            int firstIndex = findIndexPoint(contours, i , x1MaxOptimized , y1MaxOptimized);
            int secondIndex = findIndexPoint(contours, i , x2MaxOptimized , y2MaxOptimized);

            //first neighbour iteration
            int secondIndexMax = maxDistanceNeighbour(contours, i , maxDistance, firstIndex, secondIndex);
                double distance1 = distance(x1MaxOptimized ,y1MaxOptimized ,contours.get(i).points.get(secondIndexMax).x,
                        contours.get(i).points.get(secondIndexMax).y);
                maxDistanceTreeSet.add(new LabibMax(x1MaxOptimized ,y1MaxOptimized ,contours.get(i).points.get(secondIndexMax).x,
                        contours.get(i).points.get(secondIndexMax).y , distance1));


            //second neighbour iteration

            int firstIndexMax = maxDistanceNeighbour(contours, i,  maxDistance, secondIndex, firstIndex);
                double distance2 = distance(contours.get(i).points.get(firstIndexMax).x,
                        contours.get(i).points.get(firstIndexMax).y, x2MaxOptimized , y2MaxOptimized);
                maxDistanceTreeSet.add(new LabibMax(contours.get(i).points.get(firstIndexMax).x,
                        contours.get(i).points.get(firstIndexMax).y, x2MaxOptimized , y2MaxOptimized, distance2));

            int x1MaxOptimized2 = maxDistanceTreeSet.last().getX1();
            int y1MaxOptimized2 = maxDistanceTreeSet.last().getY1();
            int x2MaxOptimized2 = maxDistanceTreeSet.last().getX2();
            int y2MaxOptimized2 = maxDistanceTreeSet.last().getY2();
            int firstIndex2 = findIndexPoint(contours, i , x1MaxOptimized2 , y1MaxOptimized2);
            int secondIndex2 = findIndexPoint(contours, i , x2MaxOptimized2 , y2MaxOptimized2);

            //third neighbour iteration
            int secondIndexMax2 = maxDistanceNeighbour(contours, i, maxDistance, firstIndex2, secondIndex2);
            double distance3 = distance(x1MaxOptimized2 ,y1MaxOptimized2 ,contours.get(i).points.get(secondIndexMax2).x,
                        contours.get(i).points.get(secondIndexMax2).y);
            maxDistanceTreeSet.add(new LabibMax(x1MaxOptimized2 ,y1MaxOptimized2 ,contours.get(i).points.get(secondIndexMax2).x,
                        contours.get(i).points.get(secondIndexMax2).y , distance3));

            //fourth neighbour iteration
           int firstIndexMax2 = maxDistanceNeighbour(contours, i, maxDistance, secondIndex2, firstIndex2);
           double distance4 = distance(contours.get(i).points.get(firstIndexMax2).x,
                        contours.get(i).points.get(firstIndexMax2).y, x2MaxOptimized2 , y2MaxOptimized2);
           maxDistanceTreeSet.add(new LabibMax(contours.get(i).points.get(firstIndexMax2).x,
                        contours.get(i).points.get(firstIndexMax2).y, x2MaxOptimized , y2MaxOptimized, distance4));*/


            int  firstIndexMax, firstIndex , secondIndexMax ,secondIndex, counterZ=0;
            do {
                //Alternative method
                counterZ++;
                System.out.println("counterz = "+counterZ);
                int x1MaxOptimized = maxDistanceTreeSet.last().getX1();
                int y1MaxOptimized = maxDistanceTreeSet.last().getY1();
                int x2MaxOptimized = maxDistanceTreeSet.last().getX2();
                int y2MaxOptimized = maxDistanceTreeSet.last().getY2();
                maxDistance = distance(x1MaxOptimized, y1MaxOptimized, x2MaxOptimized, y2MaxOptimized);

                firstIndex = findIndexPoint(contours, i, x1MaxOptimized, y1MaxOptimized);
                secondIndex = findIndexPoint(contours, i, x2MaxOptimized, y2MaxOptimized);

                //first neighbour iteration
                secondIndexMax = maxDistanceNeighbour2(contours, maxDistance, firstIndex, secondIndex);
                double distance1 = distance(x1MaxOptimized, y1MaxOptimized, contours.get(i).points.get(secondIndexMax % contours.get(i).points.size()).x,
                        contours.get(i).points.get(secondIndexMax % contours.get(i).points.size()).y);
                maxDistanceTreeSet.add(new LabibMax(x1MaxOptimized, y1MaxOptimized, contours.get(i).points.get(secondIndexMax % contours.get(i).points.size()).x,
                        contours.get(i).points.get(secondIndexMax % contours.get(i).points.size()).y, distance1));


                //second neighbour iteration
                firstIndexMax = maxDistanceNeighbour2(contours, maxDistance, secondIndex, firstIndex);
                double distance2 = distance(contours.get(i).points.get(firstIndexMax % contours.get(i).points.size()).x,
                        contours.get(i).points.get(firstIndexMax % contours.get(i).points.size()).y, x2MaxOptimized, y2MaxOptimized);

                maxDistanceTreeSet.add(new LabibMax(contours.get(i).points.get(firstIndexMax % contours.get(i).points.size()).x,
                        contours.get(i).points.get(firstIndexMax % contours.get(i).points.size()).y, x2MaxOptimized, y2MaxOptimized, distance2));

            } while (firstIndexMax !=firstIndex && secondIndexMax !=secondIndex && counterZ < 15);

            contourDistancesArrayList.add(new LabibContourDistance(contours.get(i).id,
                    maxDistanceTreeSet.last().getX1(), maxDistanceTreeSet.last().getY1(),
                    maxDistanceTreeSet.last().getX2(), maxDistanceTreeSet.last().getY2(),
                    maxDistanceTreeSet.last().getDistance(), maxDistanceTreeSet.last().getDistance()/2));

            maxDistanceTreeSet.clear();
            contourSize = contourSize + contours.get(i).points.size() * 3/2;

        }
        System.out.println(contourDistancesArrayList);
        System.out.println("Contour size Optimal method №2 = "+ contourSize);
        System.out.println("Number of Max Distance Point In Contours Optimized Iterations = "+counterSteps + " Steps");
        System.out.println(contourDistancesArrayList);
    }

    public void maxDistancePointInContoursOptimized3(List<ContourLabib.Contour> contours){
        double maxDistance;
        int xFirstMax;
        int yFirstMax;
        int xMax;
        int yMax;
        int xMaxLeft;
        int yMaxLeft;
        int xMaxRight;
        int yMaxRight;

        int xMaxPlus1;
        int yMaxPlus1;
        int xMaxLeftPlus1;
        int yMaxLeftPlus1;
        int xMaxRightPlus1;
        int yMaxRightPlus1;

        int xMaxMinus1;
        int yMaxMinus1;
        int xMaxLeftMinus1;
        int yMaxLeftMinus1;
        int xMaxRightMinus1;
        int yMaxRightMinus1;

        double distanceLeft;
        double distanceRight;

        int counterSteps = 0;

        TreeSet<LabibMax> maxDistanceTreeSet;
        contourDistancesArrayList = new ArrayList<LabibContourDistance>();
        for(int i =0; i < contours.size() ; i++){
            maxDistanceTreeSet = new TreeSet<LabibMax>(new Comparator<LabibMax>() {
                @Override
                public int compare(LabibMax o1, LabibMax o2) {
                    if(o1.getDistance() > o2.getDistance()){
                        return 1;
                    }else {
                        return -1;
                    }
                }
            });

            for(int j = 0; j < contours.get(i).points.size(); j++){
                xFirstMax = contours.get(i).points.get(j).x;
                yFirstMax = contours.get(i).points.get(j).y;

                xMax = contours.get(i).points.get((j + (contours.get(i).points.size()/2))%contours.get(i).points.size()).x;
                yMax = contours.get(i).points.get((j + (contours.get(i).points.size()/2))%contours.get(i).points.size()).y;

                xMaxLeft = contours.get(i).points.get((int) ((j + (0.3125 * contours.get(i).points.size()/4))%contours.get(i).points.size())).x;
                yMaxLeft = contours.get(i).points.get((int) ((j + (0.3125 * contours.get(i).points.size()/4))%contours.get(i).points.size())).y;

                xMaxRight = contours.get(i).points.get((int) ((j + ((0.684932 * contours.get(i).points.size())/4))%contours.get(i).points.size())).x;
                yMaxRight = contours.get(i).points.get((int) ((j + ((0.684932 * contours.get(i).points.size())/4))%contours.get(i).points.size())).y;

                //Calculate Distance
                maxDistance = distance(xFirstMax, yFirstMax, xMax , yMax);
                distanceLeft = distance(xFirstMax , yFirstMax , xMaxLeft , yMaxLeft);
                distanceRight = distance(xFirstMax, yFirstMax, xMaxRight, yMaxRight);


                //Initialize neighbour pixels
                xMaxPlus1  = contours.get(i).points.get(((j + (contours.get(i).points.size()/2)) + 1)%contours.get(i).points.size()).x;
                yMaxPlus1  = contours.get(i).points.get(((j + (contours.get(i).points.size()/2)) + 1)%contours.get(i).points.size()).y;
                xMaxMinus1 = contours.get(i).points.get(((j + (contours.get(i).points.size()/2)) - 1)%contours.get(i).points.size()).x;
                yMaxMinus1 = contours.get(i).points.get(((j + (contours.get(i).points.size()/2)) - 1)%contours.get(i).points.size()).y;

                xMaxLeftPlus1 = contours.get(i).points.get(((j + (contours.get(i).points.size()/4)) + 1)%contours.get(i).points.size()).x;
                yMaxLeftPlus1 = contours.get(i).points.get(((j + (contours.get(i).points.size()/4)) + 1)%contours.get(i).points.size()).y;
                xMaxLeftMinus1 = contours.get(i).points.get(((j + (contours.get(i).points.size()/4)) - 1)%contours.get(i).points.size()).x;
                yMaxLeftMinus1 = contours.get(i).points.get(((j + (contours.get(i).points.size()/4)) - 1)%contours.get(i).points.size()).y;

                xMaxRightPlus1 = contours.get(i)
                        .points.get(((j + ((3 * contours.get(i).points.size())/4)) + 1)%contours.get(i).points.size()).x;
                yMaxRightPlus1 = contours.get(i)
                        .points.get(((j + ((3 * contours.get(i).points.size())/4)) + 1)%contours.get(i).points.size()).y;
                xMaxRightMinus1 = contours.get(i)
                        .points.get(((j + ((3 * contours.get(i).points.size())/4)) - 1) %contours.get(i).points.size()).x;
                yMaxRightMinus1 = contours.get(i)
                        .points.get(((j + ((3 * contours.get(i).points.size())/4)) - 1)%contours.get(i).points.size()).y;

                //Choose the maximum distance
                if(distanceLeft > maxDistance && distanceLeft > distanceRight) {
                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxLeft, yMaxLeft, distanceLeft));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxLeftPlus1, yMaxLeftPlus1,
                            distance(xFirstMax, yFirstMax, xMaxLeftPlus1, yMaxLeftPlus1)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxLeftMinus1, yMaxLeftMinus1,
                            distance(xFirstMax, yFirstMax, xMaxLeftMinus1, yMaxLeftMinus1)));

                }else if (distanceRight > maxDistance && distanceRight > distanceLeft){
                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxRight, yMaxRight, distanceRight));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxRightPlus1, yMaxRightPlus1,
                            distance(xFirstMax, yFirstMax, xMaxRightPlus1, yMaxRightPlus1)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxRightMinus1, yMaxRightMinus1,
                            distance(xFirstMax, yFirstMax, xMaxRightMinus1, yMaxRightMinus1)));

                }else {
                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMax, yMax, maxDistance));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxPlus1, yMaxPlus1,
                            distance(xFirstMax, yFirstMax, xMaxPlus1, yMaxPlus1)));

                    maxDistanceTreeSet.add(new LabibMax(xFirstMax, yFirstMax, xMaxMinus1, yMaxMinus1,
                            distance(xFirstMax, yFirstMax, xMaxMinus1, yMaxMinus1)));

                }
                counterSteps++;
            }

            contourDistancesArrayList.add(new LabibContourDistance(contours.get(i).id,
                    maxDistanceTreeSet.last().getX1(), maxDistanceTreeSet.last().getY1(),
                    maxDistanceTreeSet.last().getX2(), maxDistanceTreeSet.last().getY2(),
                    maxDistanceTreeSet.last().getDistance(), maxDistanceTreeSet.last().getDistance()/2));

            maxDistanceTreeSet.clear();
        }
        System.out.println("Number of Max Distance Point In Contours Optimized Iterations = "+counterSteps + " Steps");
        System.out.println(contourDistancesArrayList);
    }


    public void contourRadius(List<ContourLabib.Contour> contours, List<LabibContourCenter> contourCenter){
        double minDistance;
        int gxFirstMin;
        int gyFirstMin;
        int xMin;
        int yMin;
        int counterSteps = 0;

        contourDistanceFromCentersArrayList = new ArrayList<LabibContourMinDistanceFromCenter>();
        ArrayList<LabibContourMinDistanceFromCenter> minDistanceArrayList;
        TreeSet<LabibMin> minDistanceTreeSet;

        for(int i = 0; i < contours.size(); i++){
            minDistanceTreeSet = new TreeSet<LabibMin>(new Comparator<LabibMin>() {
                @Override
                public int compare(LabibMin o1, LabibMin o2) {
                    if(o1.getDistance() > o2.getDistance()){
                        return 1;
                    }else {
                        return -1;
                    }
                } });
            int gxFirst = contourCenter.get(i).getGx();
            int gyFirst = contourCenter.get(i).getGy();
            for(int j = 0; j < contourCenter.size(); j++){
                for (int k = j + 1; k < contours.get(j).points.size(); k++){
                    int x = contours.get(j).points.get(k).x;
                    int y = contours.get(j).points.get(k).y;
                    minDistance = distance(gxFirst, gyFirst, x, y);
                    gxFirstMin = gxFirst;
                    gyFirstMin = gyFirst;
                    xMin = x;
                    yMin = y;
                    minDistanceTreeSet.add(new LabibMin(gxFirstMin ,gyFirstMin , xMin , yMin, minDistance));
                    counterSteps++;
                }
            }
            contourDistanceFromCentersArrayList.add(new LabibContourMinDistanceFromCenter(contours.get(i).id,
                    minDistanceTreeSet.first().getX1(), minDistanceTreeSet.first().getY1(),
                    minDistanceTreeSet.first().getX2(), minDistanceTreeSet.first().getY2(),
                    minDistanceTreeSet.first().getDistance()));
            minDistanceTreeSet.clear();
        }
        System.out.println(contourDistanceFromCentersArrayList);
        System.out.println("Number of Contour Radius Iterations = "+counterSteps+" Steps");
    }

    public void contourRadiusOptimized(List<ContourLabib.Contour> contours, List<LabibContourCenter> contourCenter){
        double distanceRadius1;
        double distanceRadius1Plus1;
        double distanceRadius1Minus1;

        double distanceRadius2;
        double distanceRadius2Plus1;
        double distanceRadius2Minus1;

        int xRadius1;
        int yRadius1;
        int xRadius1Plus1;
        int yRadius1Plus1;
        int xRadius1Minus1;
        int yRadius1Minus1;

        int xRadius2;
        int yRadius2;
        int xRadius2Plus1;
        int yRadius2Plus1;
        int xRadius2Minus1;
        int yRadius2Minus1;

        int jMax;
        int iMax;
        int z1;
        int z2;
        int counterSteps = 0;
        contourDistanceFromCentersArrayList = new ArrayList<LabibContourMinDistanceFromCenter>();
        radiusPoint1ArrayList = new ArrayList<Point>();
        radiusPoint2ArrayList = new ArrayList<Point>();

        for(int i = 0; i < contours.size(); i++){

            int gxFirst = contourCenter.get(i).getGx();
            int gyFirst = contourCenter.get(i).getGy();
            int x1 = contourDistancesArrayList.get(i).getX1();
            int y1 = contourDistancesArrayList.get(i).getY1();
            int x2 = contourDistancesArrayList.get(i).getX2();
            int y2 = contourDistancesArrayList.get(i).getY2();

            iMax = findIndexPoint(contours, i, x1, y1);
            jMax = findIndexPoint(contours, i, x2, y2);

            //z = (j+i)/2
            z1 = Math.abs(jMax + iMax)/2;
            xRadius1 = contours.get(i).points.get(z1).x;
            yRadius1 = contours.get(i).points.get(z1).y;
            /*xRadius1Plus1 = contours.get(i).points.get(z1 + 1).x;
            yRadius1Plus1 = contours.get(i).points.get(z1 + 1).y;
            xRadius1Minus1 = contours.get(i).points.get(z1 - 1).x;
            yRadius1Minus1 = contours.get(i).points.get(z1 - 1).y;*/

            radiusPoint1ArrayList.add(new Point(xRadius1, yRadius1));

            //z = (N + (j+i))/2
            z2 = Math.abs((contours.get(i).points.size()) + (jMax + iMax))/2;
            if(z2 < contours.get(i).points.size() - 1){
                xRadius2 = contours.get(i).points.get(z2).x;
                yRadius2 = contours.get(i).points.get(z2).y;
                /*xRadius2Plus1 = contours.get(i).points.get(z2 + 1).x;
                yRadius2Plus1 = contours.get(i).points.get(z2 + 1).y;
                xRadius2Minus1 = contours.get(i).points.get(z2 - 1).x;
                yRadius2Minus1 = contours.get(i).points.get(z2 - 1).y;*/
                radiusPoint2ArrayList.add(new Point(xRadius2,yRadius2));
            }else {
                int newIndex =  Math.abs(z2 - contours.get(i).points.size());
                xRadius2 = contours.get(i).points.get(newIndex).x;
                yRadius2 = contours.get(i).points.get(newIndex).y;
               /* xRadius2Plus1 = contours.get(i).points.get(newIndex + 1).x;
                yRadius2Plus1 = contours.get(i).points.get(newIndex + 1).y;
                if(newIndex > 1){
                    xRadius2Minus1 = contours.get(i).points.get(newIndex - 1).x;
                    yRadius2Minus1 = contours.get(i).points.get(newIndex - 1).y;
                }else {
                    xRadius2Minus1 = contours.get(i).points.get(contours.get(i).points.size() - 2).x;
                    yRadius2Minus1 = contours.get(i).points.get(contours.get(i).points.size() - 2).y;
                }*/
                radiusPoint2ArrayList.add(new Point(xRadius2,yRadius2));
            }

            // find max radius
            distanceRadius1 = distance(gxFirst , gyFirst , xRadius1, yRadius1);
            distanceRadius2 = distance(gxFirst , gyFirst , xRadius2, yRadius2);

            if(distanceRadius1 > distanceRadius2){
               /* distanceRadius1Plus1 = distance(gxFirst, gyFirst , xRadius1Plus1, yRadius1Plus1);
                distanceRadius1Minus1 = distance(gxFirst, gyFirst , xRadius1Minus1, yRadius1Minus1);*/

               /* if(distanceRadius1Plus1 >  distanceRadius1 && distanceRadius1Plus1 > distanceRadius1Minus1){
                    contourDistanceFromCentersArrayList.add(new LabibContourMinDistanceFromCenter(contours.get(i).id,
                            gxFirst,gyFirst , xRadius1Plus1, yRadius1Plus1 , distanceRadius1Plus1));
                }else if(distanceRadius1Minus1 > distanceRadius1 && distanceRadius1Minus1 > distanceRadius1Plus1){
                    contourDistanceFromCentersArrayList.add(new LabibContourMinDistanceFromCenter(contours.get(i).id,
                            gxFirst,gyFirst , xRadius1Minus1, yRadius1Minus1 , distanceRadius1Minus1));
                }else {
                    contourDistanceFromCentersArrayList.add(new LabibContourMinDistanceFromCenter(contours.get(i).id,
                            gxFirst,gyFirst , xRadius1, yRadius1 , distanceRadius1));
                }*/

                contourDistanceFromCentersArrayList.add(new LabibContourMinDistanceFromCenter(contours.get(i).id,
                        gxFirst,gyFirst , xRadius1, yRadius1 , distanceRadius1));
            }else {
               /* distanceRadius2Plus1 = distance(gxFirst, gyFirst , xRadius2Plus1, yRadius2Plus1);
                distanceRadius2Minus1 = distance(gxFirst, gyFirst , xRadius2Minus1, yRadius2Minus1);

                if(distanceRadius2Plus1 >  distanceRadius2 && distanceRadius2Plus1 > distanceRadius2Minus1){
                    contourDistanceFromCentersArrayList.add(new LabibContourMinDistanceFromCenter(contours.get(i).id,
                            gxFirst,gyFirst , xRadius2Plus1, yRadius2Plus1 , distanceRadius2Plus1));
                }else if(distanceRadius2Minus1 > distanceRadius2 && distanceRadius2Minus1 > distanceRadius2Plus1){
                    contourDistanceFromCentersArrayList.add(new LabibContourMinDistanceFromCenter(contours.get(i).id,
                            gxFirst,gyFirst , xRadius2Minus1, yRadius2Minus1 , distanceRadius2Minus1));
                }else {
                    contourDistanceFromCentersArrayList.add(new LabibContourMinDistanceFromCenter(contours.get(i).id,
                            gxFirst,gyFirst , xRadius2, yRadius2 , distanceRadius2));
                }*/

                contourDistanceFromCentersArrayList.add(new LabibContourMinDistanceFromCenter(contours.get(i).id,
                        gxFirst,gyFirst , xRadius2, yRadius2 , distanceRadius2));
            }
            counterSteps++;
        }
        System.out.println("Number of Contour Radius  Iteration = "+counterSteps+" Steps");
    }

    private static int findIndexPoint(List<ContourLabib.Contour> contours, int i, int x2, int y2) {
        int jMax = 0;
        for(int j = 0; j < contours.get(i).points.size(); j++){
            if(x2 == contours.get(i).points.get(j).x && y2 == contours.get(i).points.get(j).y){
                jMax = j;
                break;
            }
        }
        return jMax;
    }

    public  void printAllContours(List<ContourLabib.Contour> contours){
        StringBuilder stringBuilder = new StringBuilder();
        int counter = 1;
        double w;
        double l;
        double h;
        double area = 0;
        double sumContourArea = 0;
        double ellipseVolume;
        double radius;
        double physicalSizeInInches  = 0.15748; //Inch
        double cmCoefficient = 2.54;
        double DPI;

        if(width < height){
            System.out.println("Image.width() = "+width);
            DPI = width/physicalSizeInInches; // px/inch
        }else {
            System.out.println("Image.height() = "+height);
            DPI = height/physicalSizeInInches; //px/inches
        }
        System.out.println("DPI = "+ DPI+ " PX/INCH");
        double onePixel = cmCoefficient/DPI; // cm
        System.out.println("1 px = "+onePixel+" cm");
        double squaredOnePixel = onePixel * onePixel;
        System.out.println("cm^2 = "+squaredOnePixel);

        for(int i =0; i < contours.size() ; i++){

            stringBuilder.append("contour №").append(contours.get(i).id).append(" has ").append(contours.get(i).points.size()).append(" points");
            stringBuilder.append("\n");
            stringBuilder.append("contour №").append(contours.get(i).id).append(": {");


            for(int j =0; j < contours.get(i).points.size(); j++){
                int x = externalContours.get(i).points.get(j).x;
                int y = externalContours.get(i).points.get(j).y;
                int xNext = externalContours.get(i).points.get((j+1)%contours.get(i).points.size()).x;
                int yNext = externalContours.get(i).points.get((j+1)%contours.get(i).points.size()).y;
                //calculate area
                area = area + (x * yNext) - (xNext * y);
                stringBuilder.append("point№[").append(counter).append("](").append(contours.get(i).points.get(j).x ).append(",").append(contours.get(i).points.get(j).y ).append(") , ");
                counter++;

            }
            area = Math.abs(area/2) * squaredOnePixel; //cm^2
            // area = area/2;
            radius = contourDistanceFromCentersArrayList.get(i).getMinRadius() * onePixel; //cm
            ellipseVolume = (4.0/3.0) * Math.PI * radius * area; // cm^3

            //sum volume of ellipse
            sumContourArea = sumContourArea + ellipseVolume; //cm^3
            stringBuilder.deleteCharAt(stringBuilder.length() - 2);
            stringBuilder.append("}");
            stringBuilder.append("\n");
            // stringBuilder.append("Contour Volume = ").append(ellipseVolume).append(" cm^3").append("\n");
            stringBuilder.append("Max Distance ("+contourDistancesArrayList.get(i).getX1()+","
                    +contourDistancesArrayList.get(i).getY1()+") --> ("+contourDistancesArrayList.get(i).getX2()
                    +","+contourDistancesArrayList.get(i).getY2()+") = "+contourDistancesArrayList.get(i).getMaxRadius()+" Pixels");
            stringBuilder.append("\n");
            stringBuilder.append("\n");
            counter = 1;
        }
        w = width * onePixel; //cm
        l = height * onePixel; //cm
        h = Math.min(width, height) * onePixel;//cm
        imageVolume = w * l * h; //cm^3
        impurityConcentration = (sumContourArea/ imageVolume) * 100;

        System.out.println(stringBuilder);

        System.out.println("Sum all Contours Volume = "+sumContourArea+" cm^3");
        System.out.println("Image Volume = "+imageVolume+" cm^3");
        System.out.println("Image Size: "+width+"x"+height+" pixels");
        System.out.println("Total pixels: "+ width*height+" pixels");
        System.out.println("Impurity Concentration = "+impurityConcentration+" % VOL");
        System.out.println("All object detected = "+externalContours.size());
    }
}
