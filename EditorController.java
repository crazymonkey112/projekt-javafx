import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class EditorController {

    private Pane workspace;
    public enum Tool { CIRCLE, RECTANGLE, POLYGON }
    private Tool currentTool = Tool.RECTANGLE;

    public EditorController(Pane workspace) {
        this.workspace = workspace;
        setupMouseEvents();
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
        System.out.println("Zmieniono narzędzie na: " + tool);
    }

    // Konfiguracja zdarzeń myszy
    private void setupMouseEvents() {
        workspace.setOnMousePressed(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            if (currentTool == Tool.RECTANGLE) {
                Rectangle rect = new Rectangle(mouseX, mouseY, 100, 60);
                rect.setFill(Color.CORNFLOWERBLUE);
                workspace.getChildren().add(rect);
                
            } else if (currentTool == Tool.CIRCLE) {
                Circle circle = new Circle(mouseX, mouseY, 40);
                circle.setFill(Color.TOMATO); 
                workspace.getChildren().add(circle);
                
            } else if (currentTool == Tool.POLYGON) {
                Polygon polygon = new Polygon();
                polygon.getPoints().addAll(
                    mouseX, mouseY - 40,      
                    mouseX - 40, mouseY + 30,
                    mouseX + 40, mouseY + 30
                );
                polygon.setFill(Color.MEDIUMSEAGREEN);
                workspace.getChildren().add(polygon);
            }
        });
    }
}