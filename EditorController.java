import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.MouseButton;

public class EditorController {

    private Pane workspace;
    public enum Tool { CIRCLE, RECTANGLE, POLYGON }
    private Tool currentTool = Tool.RECTANGLE;
    private double startX, startY;
    private Shape currentShape;
    private Shape activeShape = null;
    private double dragDeltaX, dragDeltaY;
    private ContextMenu contextMenu;
    private ColorPicker colorPicker;

    public List<ShapeData> getShapesAsData() {
        List<ShapeData> list = new ArrayList<>();
        
        for (javafx.scene.Node node : workspace.getChildren()) {
            if (node instanceof Shape) {
                Shape shape = (Shape) node;
                ShapeData data = new ShapeData();
                
                data.translateX = shape.getTranslateX();
                data.translateY = shape.getTranslateY();
                data.scaleX = shape.getScaleX();
                data.scaleY = shape.getScaleY();
                data.rotate = shape.getRotate();
                
                data.hexColor = ((Color) shape.getFill()).toString();

                if (shape instanceof Rectangle) {
                    data.type = ShapeData.ShapeType.RECTANGLE;
                    Rectangle r = (Rectangle) shape;
                    data.x = r.getX();
                    data.y = r.getY();
                    data.width = r.getWidth();
                    data.height = r.getHeight();
                } else if (shape instanceof Circle) {
                    data.type = ShapeData.ShapeType.CIRCLE;
                    Circle c = (Circle) shape;
                    data.centerX = c.getCenterX();
                    data.centerY = c.getCenterY();
                    data.radius = c.getRadius();
                } else if (shape instanceof Polygon) {
                    data.type = ShapeData.ShapeType.POLYGON;
                    Polygon p = (Polygon) shape;
                    data.points = new ArrayList<>(p.getPoints());
                }
                list.add(data);
            }
        }
        return list;
    }

    public void loadShapesFromData(List<ShapeData> dataList) {
        workspace.getChildren().clear(); 
        setActiveShape(null);
        
        for (ShapeData data : dataList) {
            Shape shape = null;
            
            if (data.type == ShapeData.ShapeType.RECTANGLE) {
                shape = new Rectangle(data.x, data.y, data.width, data.height);
            } else if (data.type == ShapeData.ShapeType.CIRCLE) {
                shape = new Circle(data.centerX, data.centerY, data.radius);
            } else if (data.type == ShapeData.ShapeType.POLYGON) {
                Polygon p = new Polygon();
                p.getPoints().addAll(data.points);
                shape = p;
            }
            
            if (shape != null) {
                shape.setTranslateX(data.translateX);
                shape.setTranslateY(data.translateY);
                shape.setScaleX(data.scaleX);
                shape.setScaleY(data.scaleY);
                shape.setRotate(data.rotate);
                shape.setFill(Color.valueOf(data.hexColor));

                makeShapeInteractive(shape); 
                workspace.getChildren().add(shape);
            }
        }
    }

    private void setupContextMenu() {
        contextMenu = new ContextMenu();
        colorPicker = new ColorPicker();

        colorPicker.setOnAction(event -> {
            if (activeShape != null) {
                activeShape.setFill(colorPicker.getValue());
            }
        });

        CustomMenuItem colorItem = new CustomMenuItem(colorPicker);
        colorItem.setHideOnClick(false); 
        
        contextMenu.getItems().add(colorItem);
    }

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
            event.consume();

            if (event.getButton() == MouseButton.SECONDARY) {
                
                colorPicker.setValue((Color) shape.getFill());
                contextMenu.show(shape, event.getScreenX(), event.getScreenY());
                
            } else if (event.getButton() == MouseButton.PRIMARY) {
                contextMenu.hide();
                dragDeltaX = shape.getTranslateX() - event.getSceneX();
                dragDeltaY = shape.getTranslateY() - event.getSceneY();
            }
        });

        shape.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown()) return;

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
        setupContextMenu();
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
    }

    private void setupMouseEvents() {
        
        workspace.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY) return;

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
            if (!event.isPrimaryButtonDown()) return;

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
