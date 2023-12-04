package h4wb;
//-------------------------------------------------
// Reference: https://stackoverflow.com/a/41353991
//-------------------------------------------------
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class Arrow extends Group {

    private final Line line;
    private final Polygon arrow;
    private static final double initArrowPositio = 30, initArrowLength = 20, initArrowWidth = 10;
    private double arrowPosition = initArrowPositio, arrowLength = initArrowLength, arrowWidth = initArrowWidth, lineLength = 0;
    private static int nextID = 0;
    
    public Arrow() {
        this(new Line(), new Polygon(-initArrowLength/2, -initArrowWidth/2, -initArrowLength/2, initArrowWidth/2, initArrowLength/2, 0));
    }

    private void updateArrow() {
    	double ex = getEndX();
        double ey = getEndY();
        double sx = getStartX();
        double sy = getStartY();
        lineLength = Math.hypot(sx-ex, sy-ey);
        double factor = (arrowLength*0.3 + arrowPosition)/lineLength;
        arrow.setLayoutX(ex + (sx-ex)*factor);
        arrow.setLayoutY(ey + (sy-ey)*factor);
        arrow.setScaleX(arrowLength/initArrowLength);
        arrow.setScaleY(arrowWidth/initArrowWidth);

        if (ex == sx && ey == sy) {
            // arrow parts of length 0
        	arrow.setVisible(false);
        } else {
        	arrow.setVisible(true);
            arrow.setRotate(Math.toDegrees(Math.atan2(ey-sy, ex-sx)));
        }
    }
    
    private Arrow(Line line, Polygon arrow) {
        super(line, arrow);
        this.line = line;
        this.line.setStrokeWidth(2);
        this.arrow = arrow;
		this.setOnMouseMoved(e->{
			this.setArrowPosition(Math.hypot(e.getX()-this.getEndX(), e.getY()-this.getEndY()) - arrowLength/2);
		});
		this.setOnMouseExited(e->{
			this.resetArrowPosition();
		});
		this.setId("arrow"+String.valueOf(nextID++));
    }

    // start/end properties

    public final void setStartX(double value) {
        line.setStartX(value);
        updateArrow();
    }

    public final double getStartX() {
        return line.getStartX();
    }

    public final DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public final void setStartY(double value) {
        line.setStartY(value);
        updateArrow();
    }

    public final double getStartY() {
        return line.getStartY();
    }

    public final DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    public final void setEndX(double value) {
        line.setEndX(value);
        updateArrow();
    }

    public final double getEndX() {
        return line.getEndX();
    }

    public final DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    public final void setEndY(double value) {
        line.setEndY(value);
        updateArrow();
    }

    public final double getEndY() {
        return line.getEndY();
    }

    public final DoubleProperty endYProperty() {
        return line.endYProperty();
    }
    
    public final void setArrowPosition(double value) {
    	arrowPosition = Math.min(Math.max(value, 0), lineLength-arrowLength*0.7);
    	updateArrow();
    }
    
    public final void setArrowLength(double value) {
    	arrowLength = value;
    	updateArrow();
    }
    
    public final void setArrowWidth(double value) {
    	arrowWidth = value;
    	updateArrow();
    }
    
    public final void resetArrowPosition() {
    	setArrowPosition(initArrowPositio);
    }
    
    public final void resetArrowLength() {
    	setArrowLength(initArrowLength);
    }
    
    public final void resetArrowWidth() {
    	setArrowWidth(initArrowWidth);
    }

}