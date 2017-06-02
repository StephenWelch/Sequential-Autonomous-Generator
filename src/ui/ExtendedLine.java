package ui;

import javafx.scene.shape.Line;

/**
 * Created by Stephen Welch on 5/29/2017.
 */
public class ExtendedLine extends Line {

    private ExtendedLine prevLine;
    private double angleToPrev, length;

    public ExtendedLine(ExtendedLine prevLine, double startX, double startY, double endX, double endY) {
        super();
        this.prevLine = prevLine;
        this.setStartX(startX);
        this.setStartY(startY);
        this.setEndX(endX);
        this.setEndY(endY);
        this.length = getDistance(getStartX(), getStartY(), endX, endY);

        if (prevLine != null) {
            //Distance from the previous line's start to this one's end
            double prevOriginToCurrEnd = getDistance(prevLine.getStartX(), prevLine.getStartY(), getEndX(), getEndY());
            double pointSide = (prevLine.getEndX() - prevLine.getStartX()) * (getEndY() - prevLine.getStartY()) - (getEndX() - prevLine.getStartX()) * (prevLine.getEndY() - prevLine.getStartY());
            angleToPrev = Math.acos((Math.pow(getLength(), 2) + Math.pow(prevLine.getLength(), 2) - Math.pow(prevOriginToCurrEnd, 2)) / (2 * getLength() * prevLine.getLength())); //Law of cosines
            angleToPrev = (pointSide >= 0) ? angleToPrev : -angleToPrev;
        }
        if (prevLine == null) {
            double baseX = endX;
            double baseY = getStartY();
            double length = baseX - getStartX();
            double height = getEndY() - baseY;
            angleToPrev = Math.atan(height / length);
        }
    }

    public ExtendedLine(double startX, double startY) {
        this(null, startX, startY, 0, 0);
    }

    public ExtendedLine(ExtendedLine prevLine, double endX, double endY) {
        this(prevLine, prevLine.getEndX(), prevLine.getEndY(), endX, endY);
    }

    public ExtendedLine getPrevLine() {
        return prevLine;
    }

    public double getLength() {
        return length;
    }

    public double getLengthInches(double PPI) {
        return length / PPI;
    }

    public double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((Math.pow(x2 - x1, 2) + (Math.pow(y2 - y1, 2))));
    }

    public double getSlope() {
        return (getEndY() - getStartY()) / (getEndX() - getStartY());
    }

    public double getMidpointX() {
        return (getStartX() + getEndX()) / 2;
    }

    public double getMidpointY() {
        return (getStartY() + getEndY()) / 2;
    }

    public double getAngleToPrev() {
        return (angleToPrev * 180) / Math.PI;
    }

}
