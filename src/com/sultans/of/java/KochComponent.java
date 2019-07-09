package com.sultans.of.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A JComponent that is used for drawing the Koch snowflake at a specific iteration.
 * <p>
 * This component supports setting the iteration later after initialisation and is draggable on the screen.
 *
 * @author Brett Rijnders
 * @see java.awt.Component
 */
public class KochComponent extends JComponent implements MouseListener, MouseMotionListener, ActionListener {

    private Point fStartPoint;
    private Point fEndPoint;
    private int fIter;

    private int mouseX, mouseY;
    private static List<ColoredLine> fLines = new ArrayList<>();
    private boolean fHasPrevIter = false;

    //  Used for translating points
    private double fTransX = 0;
    private double fTransY = 0;

    //  Action listener interface implemented somewhere else. In this case it's used for updating the slider indicating
    //  it's current iteration.
    private ActionListener fKochListener;

    /**
     * Create a Koch snowflake based on start point and end point of a line.
     * The coordinates of the two other lines will be calculated and added.
     *
     * @param startPoint  Starting point of base (bottom) line of triangle.
     * @param endPoint    End point of base (bottom) line of triangle.
     * @param initialIter Initial iteration at which Koch snowflake should be created.
     */
    KochComponent(Point startPoint, Point endPoint, int initialIter) {
        fStartPoint = startPoint;
        fEndPoint = endPoint;
        fIter = initialIter;
        createKochSnowflake(initialIter);

        addMouseMotionListener(this);
        addMouseListener(this);

    }

    /**
     * Create snowflake of previous iteration, if any.
     * The previous snowflake is shown in a different color.
     *
     * @param iter Iteration for which Koch snowflake needs to be created.
     */
    private void createPrevKochSnowflake(int iter) {
        if (hasPrevIter() && iter > 0) {
            createKochSnowflake(iter - 1, Color.CYAN);
        }
    }

    /**
     * Create Koch snowflake at a specific iteration.
     *
     * @param iter Iteration for which Koch snowflake needs to be created.
     */
    private void createKochSnowflake(int iter) {
        createKochSnowflake(iter, Color.BLUE);
    }

    private void createKochSnowflake(int initialIter, Color aLineColor) {
        createKochLines(initialIter, aLineColor, fStartPoint, fEndPoint);
        createKochLines(initialIter, aLineColor, new Point(fEndPoint), new Point((fEndPoint.x - fStartPoint.x) / 2 + fStartPoint.x,
                (int) (fEndPoint.y - (fEndPoint.x - fStartPoint.x) / 2 * Math.tan(60.0 / 180.0 * Math.PI))));
        createKochLines(initialIter, aLineColor, new Point((fEndPoint.x - fStartPoint.x) / 2 + fStartPoint.x,
                (int) (fEndPoint.y - (fEndPoint.x - fStartPoint.x) / 2 * Math.tan(60.0 / 180.0 * Math.PI))), fStartPoint);
    }

    @Override
    /*
      Override of paint component that will draw the koch lines and take into account translations.
      The translations are based on how much the mouse has moved.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(fTransX, fTransY);

        for (ColoredLine line : fLines) {
            g2d.setColor(line.getColor());
            g2d.draw(line.getLine());
        }
    }

    public void createKochLines(int iter, Color aLineColor, Point pStart, Point pEnd) {
        if (iter == 0) {

            Line2D.Double line = new Line2D.Double(pStart.x, pStart.y, pEnd.x, pEnd.y);
            ColoredLine coloredLine = new ColoredLine(line, aLineColor);
            fLines.add(coloredLine);
        } else {
            Point p1 = pStart;   //  First point
            Point p5 = pEnd; //  Last point

            Point p2 = new Point((int) (p1.x + (p5.x - p1.x) / 3.0), (int) (p1.y + (p5.y - p1.y) / 3.0));
            Point p4 = new Point((int) (p1.x + (2 * (p5.x - p1.x) / 3.0)), (int) (p1.y + (2 * ((p5.y - p1.y) / 3.0))));

            //  Find point for the tip of triangle
            //  Rotate the line between p2 and p4 to create the line of the left side of the triangle p2 and p3.
            //  This is a special triangle, a so called equilateral triangle in which all sides and angles are equal.
            double theta = Math.PI / 3; // 60 degrees
            Point p3r = rotatePoint(p2, p4, theta);

            createKochLines(iter - 1, aLineColor, p1, p2);
            createKochLines(iter - 1, aLineColor, p2, p3r);
            createKochLines(iter - 1, aLineColor, p3r, p4);
            createKochLines(iter - 1, aLineColor, p4, p5);
        }
    }

    /**
     * Rotate a vector, consisting of point p1 (starting point) and p2 (end point), around point p1
     *
     * More info: https://matthew-brett.github.io/teaching/rotation_2d.html
     * and https://academo.org/demos/rotation-about-point/
     *
     * @param p1            Starting point around which rotation is applied.
     * @param p2            End point.
     * @param rotationAngle Angle in radians, rotating counter-clock-wise is positive
     *
     * @return Coordinates of rotated point.
     */
    private Point rotatePoint(Point p1, Point p2, double rotationAngle) {
        //  First translate point to origin, hence (p2-p1). Then rotate around origin.
        //  Finally, apply translation back to original point
        int newX = (int) (p1.x + (p2.x - p1.x) * Math.cos(rotationAngle) - (p2.y - p1.y) * Math.sin(rotationAngle));
        int newY = (int) (p1.y + (p2.y - p1.y) * Math.cos(rotationAngle) + (p2.x - p1.x) * Math.sin(rotationAngle));

        return new Point(newX, newY);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void mouseDraggedRecalculate(MouseEvent e) {
        Rectangle visibleRect = getVisibleRect();

        double panFactor = -0.02;

        Point newStartPoint
                = new Point((int) (fStartPoint.x + panFactor * (mouseX - e.getX())), ((int) (fStartPoint.y + panFactor * (mouseY - e.getY()))));
        Point newEndPoint = new Point((int) (fEndPoint.x + panFactor * (mouseX - e.getX())), (int) (fEndPoint.y + panFactor * (mouseY - e.getY())));

        if (fEndPoint.x + panFactor * (mouseX - e.getX()) - visibleRect.getWidth() > 0 ||
                fStartPoint.x + panFactor * (mouseX - e.getX()) < 0) {
            newStartPoint = new Point((int) (fStartPoint.x), (int) (fStartPoint.y + panFactor * (mouseY - e.getY())));
            newEndPoint = new Point((int) (fEndPoint.x), (int) (fEndPoint.y + panFactor * (mouseY - e.getY())));
        }

        if (fStartPoint.y + panFactor * (mouseY - e.getY()) - visibleRect.getHeight() > 0 ||
                fStartPoint.y - (fEndPoint.x - fStartPoint.x) / 2 * Math.tan(60.0 / 180.0 * Math.PI) + panFactor * (mouseY - e.getY()) < 0) {
            newStartPoint = new Point((fStartPoint.x), (fStartPoint.y));
            newEndPoint = new Point((fEndPoint.x), (fEndPoint.y));
        }

        fStartPoint.setLocation(newStartPoint);
        fEndPoint.setLocation(newEndPoint);

        //  TODO: Performance improvement suggestion:
        //  Instead of recalculating, just take a snapshot of current lines collection and translate the lines
        fLines.clear();
        createKochSnowflake(fIter);
        createPrevKochSnowflake(fIter);
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Rectangle visibleRect = getVisibleRect();

        double panFactor = -0.02;

        fTransX += panFactor * (mouseX - e.getX());
        fTransY += panFactor * (mouseY - e.getY());

        //  Prevent shape to translate outside view
        Rectangle kochBoundingBox = getBoundingBox();
        if (kochBoundingBox.getMaxX() + fTransX - visibleRect.getWidth() > 0 ||
                kochBoundingBox.getMinX() + fTransX < 0) {
            fTransX -= panFactor * (mouseX - e.getX());
        }

        if (kochBoundingBox.getMaxY() + fTransY - visibleRect.getHeight() > 0 ||
                kochBoundingBox.getMinY() + fTransY < 0) {
            fTransY -= panFactor * (mouseY - e.getY());
        }

        repaint();
    }

    private Rectangle getBoundingBox() {
        IntSummaryStatistics xStats = fLines.stream().parallel()
                .flatMapToInt(coloredLine -> IntStream.of((int) coloredLine.getLine().x1, (int) coloredLine.getLine().x2)).summaryStatistics();

        IntSummaryStatistics yStats = fLines.stream().parallel()
                .flatMapToInt(coloredLine -> IntStream.of((int) coloredLine.getLine().y1, (int) coloredLine.getLine().y2)).summaryStatistics();

        return new Rectangle(xStats.getMin(), yStats.getMin(), xStats.getMax() - xStats.getMin(), yStats.getMax() - yStats.getMin());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public void setIteration(int newIteration) {
        fIter = newIteration;
        fireChange("changeIter");
    }

    private void fireChange(String command) {
        if (fKochListener != null) {
            fKochListener.actionPerformed(new ActionEvent(KochComponent.this, ActionEvent.ACTION_PERFORMED, command));
        }
    }

    public int getIteration() {
        return fIter;
    }

    public void recreate() {
        clear();
        createKochSnowflake(fIter);
        createPrevKochSnowflake(fIter);
        repaint();
    }

    public void clear() {
        fLines.clear();
    }

    public void showPrevIter(boolean showPrevIter) {
        fHasPrevIter = showPrevIter;
    }

    public boolean hasPrevIter() {
        return fHasPrevIter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (getIteration() > 5) {
            setIteration(0);
        } else {
            int nextIteration = getIteration() + 1;
            setIteration(nextIteration);
        }

        recreate();
    }

    public void setActionListener(ActionListener k) {
        fKochListener = k;
    }

    /**
     *
     */
    private class ColoredLine {
        private final Line2D.Double fLine;
        private final Color fColor;

        public ColoredLine(Line2D.Double aLine, Color aColor) {
            fLine = aLine;
            fColor = aColor;
        }

        public Line2D.Double getLine() {
            return fLine;
        }

        public Color getColor() {
            return fColor;
        }
    }
}
