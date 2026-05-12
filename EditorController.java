import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

//  Kontroler odpowiadający za logikę rysowania, modyfikacji i zarządzania figurami na obszarze roboczym.
public class EditorController {

    // Komponenty interfejsu
    private Pane workspace;
    private ContextMenu contextMenu;
    private ColorPicker colorPicker;

    // Narzędzia i stan edytora
    public enum Tool { CIRCLE, RECTANGLE, POLYGON }
    private Tool currentTool = Tool.RECTANGLE;
    
    // Zmienne pomocnicze do rysowania i transformacji
    private double startX, startY;
    private double dragDeltaX, dragDeltaY;
    private Shape currentShape;
    private Shape activeShape = null;

    public EditorController(Pane workspace) {
        this.workspace = workspace;
        setupMouseEvents();
        setupContextMenu();
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
    }

    // Zapis danych - Konwertuje obiekty JavaFX na płótnie do formatu DTO (Data Transfer Object) w celu umożliwienia ich serializacji.
    public List<ShapeData> getShapesAsData() {
        List<ShapeData> list = new ArrayList<>();
        
        for (Node node : workspace.getChildren()) {
            if (node instanceof Shape) {
                Shape shape = (Shape) node;
                ShapeData data = new ShapeData();
                
                // Zapis transformacji i właściwości bazowych
                data.translateX = shape.getTranslateX();
                data.translateY = shape.getTranslateY();
                data.scaleX = shape.getScaleX();
                data.scaleY = shape.getScaleY();
                data.rotate = shape.getRotate();
                data.hexColor = ((Color) shape.getFill()).toString();

                // Zapis właściwości specyficznych dla danego typu figury
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

    // Odczyt danych - Czyści obecne płótno i odtwarza figury na podstawie wczytanych danych DTO.
    public void loadShapesFromData(List<ShapeData> dataList) {
        workspace.getChildren().clear(); 
        setActiveShape(null);
        
        for (ShapeData data : dataList) {
            Shape shape = null;
            
            // Rekonstrukcja geometrii
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
                // Aplikacja zapisanych transformacji i kolorów
                shape.setTranslateX(data.translateX);
                shape.setTranslateY(data.translateY);
                shape.setScaleX(data.scaleX);
                shape.setScaleY(data.scaleY);
                shape.setRotate(data.rotate);
                shape.setFill(Color.valueOf(data.hexColor));
                shape.setStroke(Color.BLACK); 

                makeShapeInteractive(shape); 
                workspace.getChildren().add(shape);
            }
        }
    }

    // Konfiguruje menu kontekstowe oraz narzędzie ColorPicker dla aktywnych figur.
    private void setupContextMenu() {
        contextMenu = new ContextMenu();
        colorPicker = new ColorPicker();

        // Akcja zmiany koloru dla aktywnej figury
        colorPicker.setOnAction(event -> {
            if (activeShape != null) {
                activeShape.setFill(colorPicker.getValue());
            }
        });

        CustomMenuItem colorItem = new CustomMenuItem(colorPicker);
        colorItem.setHideOnClick(false); 
        contextMenu.getItems().add(colorItem);
    }

    // Rejestruje wskazaną figurę jako aktywną i nakłada na nią wizualne obramowanie.
    private void setActiveShape(Shape shape) {
        if (activeShape != null) {
            activeShape.setStroke(Color.BLACK); 
            activeShape.setStrokeWidth(1);
        }

        activeShape = shape;

        if (activeShape != null) {
            activeShape.setStroke(Color.RED);
            activeShape.setStrokeWidth(2);
            activeShape.toFront();
        }
    }

    // Dodaje event listnery umożliwiające aktywowanie, przesuwanie, skalowanie, obracanie figury oraz pozwala na wywołanie menu kontekstowego
    private void makeShapeInteractive(Shape shape) {
        shape.setOnMousePressed(event -> {
            setActiveShape(shape);
            event.consume();

            if (event.getButton() == MouseButton.SECONDARY) {
                // Wywołanie menu kontekstowego
                colorPicker.setValue((Color) shape.getFill());
                contextMenu.show(shape, event.getScreenX(), event.getScreenY());
            } else if (event.getButton() == MouseButton.PRIMARY) {
                // Rozpoczęcie przesuwania figury
                contextMenu.hide();
                dragDeltaX = shape.getTranslateX() - event.getSceneX();
                dragDeltaY = shape.getTranslateY() - event.getSceneY();
            }
        });

        // Obsługa przesuwania figury
        shape.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown()) return;

            shape.setTranslateX(event.getSceneX() + dragDeltaX);
            shape.setTranslateY(event.getSceneY() + dragDeltaY);
            event.consume();
        });

        // Obsługa skalowania (Scroll) i obracania (Ctrl + Scroll)
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

    // Konfiguruje główne listenery obszaru roboczego, odpowiadające za inicjowanie i rysowanie figur
    private void setupMouseEvents() {
        // Zdarzenie kliknięcia - punkt początkowy rysowania
        workspace.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY) return;

            startX = event.getX();
            startY = event.getY();

            if (currentTool == Tool.RECTANGLE) {
                Rectangle rect = new Rectangle(startX, startY, 0, 0);
                rect.setFill(Color.WHITE);
                rect.setStroke(Color.BLACK);        
                currentShape = rect;
                makeShapeInteractive(rect);
                workspace.getChildren().add(rect);
            } else if (currentTool == Tool.CIRCLE) {
                Circle circle = new Circle(startX, startY, 0);
                circle.setFill(Color.WHITE); 
                circle.setStroke(Color.BLACK);
                currentShape = circle;
                makeShapeInteractive(circle);
                workspace.getChildren().add(circle);
            } else if (currentTool == Tool.POLYGON) {
                Polygon polygon = new Polygon();
                polygon.setFill(Color.WHITE);
                polygon.setStroke(Color.BLACK);
                currentShape = polygon;
                makeShapeInteractive(polygon);
                workspace.getChildren().add(polygon);
            }
        });

        // Zdarzenie przeciągania - dynamiczne dopasowywanie wymiarów figury
        workspace.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown()) return;

            double currentX = event.getX();
            double currentY = event.getY();

            if (currentTool == Tool.RECTANGLE && currentShape instanceof Rectangle) {
                Rectangle rect = (Rectangle) currentShape;
                // Matematyczne zabezpieczenie przed ujemnymi wymiarami
                rect.setX(Math.min(startX, currentX));
                rect.setY(Math.min(startY, currentY));
                rect.setWidth(Math.abs(currentX - startX));
                rect.setHeight(Math.abs(currentY - startY));
            } else if (currentTool == Tool.CIRCLE && currentShape instanceof Circle) {
                Circle circle = (Circle) currentShape;
                // Obliczanie promienia na podstawie twierdzenia Pitagorasa
                double radius = Math.sqrt(Math.pow(currentX - startX, 2) + Math.pow(currentY - startY, 2));
                circle.setRadius(radius);
            } else if (currentTool == Tool.POLYGON && currentShape instanceof Polygon) {
                Polygon polygon = (Polygon) currentShape;
                // Generowanie trójkąta na podstawie wirtualnej obwiedni (bounding box)
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