import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape; 

public class EditorController {

    private Pane workspace;
    public enum Tool { CIRCLE, RECTANGLE, POLYGON }
    private Tool currentTool = Tool.RECTANGLE;
    private double startX, startY;
    private Shape currentShape;
    private Shape activeShape = null;
    private double dragDeltaX, dragDeltaY;

    private void setActiveShape(Shape shape) {
        if (activeShape != null) {
            activeShape.setStroke(null);
        }

        activeShape = shape;

        if (activeShape != null) {
            activeShape.setStroke(Color.RED);
            activeShape.setStrokeWidth(2);
            activeShape.toFront();
        }
    }

    private void makeShapeInteractive(Shape shape) {
        shape.setOnMousePressed(event -> {
            setActiveShape(shape);
            dragDeltaX = shape.getTranslateX() - event.getSceneX();
            dragDeltaY = shape.getTranslateY() - event.getSceneY();
            event.consume();
        });

        shape.setOnMouseDragged(event -> {
            shape.setTranslateX(event.getSceneX() + dragDeltaX);
            shape.setTranslateY(event.getSceneY() + dragDeltaY);
            event.consume();
        });
        shape.setOnScroll(event -> {
            if (activeShape != shape) return;

            double delta = event.getDeltaY();

            if (delta == 0.0) return;

            if (event.isControlDown()) {
                double currentAngle = shape.getRotate();
                double angleDelta = (delta > 0) ? 10 : -10;
                shape.setRotate(currentAngle + angleDelta);
                
            } else {
                double currentScaleX = shape.getScaleX();
                double currentScaleY = shape.getScaleY();
                double scaleFactor = (delta > 0) ? 1.1 : 0.9;
                
                shape.setScaleX(currentScaleX * scaleFactor);
                shape.setScaleY(currentScaleY * scaleFactor);
            }

            event.consume();
        });
    }

    public EditorController(Pane workspace) {
        this.workspace = workspace;
        setupMouseEvents();
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
    }

    private void setupMouseEvents() {
        
        workspace.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();

            if (currentTool == Tool.RECTANGLE) {
                Rectangle rect = new Rectangle(startX, startY, 0, 0);
                rect.setFill(Color.CORNFLOWERBLUE);
                currentShape = rect;
                makeShapeInteractive(rect);
                workspace.getChildren().add(rect);
                
            } else if (currentTool == Tool.CIRCLE) {
                Circle circle = new Circle(startX, startY, 0);
                circle.setFill(Color.TOMATO); 
                currentShape = circle;
                makeShapeInteractive(circle);
                workspace.getChildren().add(circle);
                
            } else if (currentTool == Tool.POLYGON) {
                Polygon polygon = new Polygon();
                polygon.setFill(Color.MEDIUMSEAGREEN);
                currentShape = polygon;
                makeShapeInteractive(polygon);
                workspace.getChildren().add(polygon);
            }
        });

        workspace.setOnMouseDragged(event -> {
            double currentX = event.getX();
            double currentY = event.getY();

            if (currentTool == Tool.RECTANGLE && currentShape instanceof Rectangle) {
                Rectangle rect = (Rectangle) currentShape;
                rect.setX(Math.min(startX, currentX));
                rect.setY(Math.min(startY, currentY));
                rect.setWidth(Math.abs(currentX - startX));
                rect.setHeight(Math.abs(currentY - startY));
                
            } else if (currentTool == Tool.CIRCLE && currentShape instanceof Circle) {
                Circle circle = (Circle) currentShape;
                
                double radius = Math.sqrt(Math.pow(currentX - startX, 2) + Math.pow(currentY - startY, 2));
                circle.setRadius(radius);
                
            } else if (currentTool == Tool.POLYGON && currentShape instanceof Polygon) {
                Polygon polygon = (Polygon) currentShape;
                
                double minX = Math.min(startX, currentX);
                double maxX = Math.max(startX, currentX);
                double minY = Math.min(startY, currentY);
                double maxY = Math.max(startY, currentY);

                polygon.getPoints().clear();

                polygon.getPoints().addAll(
                    (minX + maxX) / 2, minY, 
                    minX, maxY,           
                    maxX, maxY               
                );
            }
        });
    }
}
