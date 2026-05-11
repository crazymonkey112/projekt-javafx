import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*; 
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane; 
import javafx.scene.layout.VBox; 
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private Pane workspace;

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // Stworzenie menubara i dodanie do niego opcji
        MenuBar menuBar = new MenuBar();
        
        Menu menuPlik = new Menu("Plik");
        MenuItem itemZapisz = new MenuItem("Zapisz");
        MenuItem itemWczytaj = new MenuItem("Wczytaj");
        menuPlik.getItems().addAll(itemZapisz, itemWczytaj);

        Menu menuFigury = new Menu("Figury");
        MenuItem itemOkrag = new MenuItem("Okrąg");
        MenuItem itemProstokat = new MenuItem("Prostokąt");
        MenuItem itemWielokat = new MenuItem("Wielokąt");
        menuFigury.getItems().addAll(itemOkrag, itemProstokat, itemWielokat);

        Menu menuPomoc = new Menu("Pomoc");
        MenuItem itemInstrukcja = new MenuItem("Instrukcja użytkownika");
        menuPomoc.getItems().add(itemInstrukcja);

        menuBar.getMenus().addAll(menuPlik, menuFigury, menuPomoc);

        // Panel górny z przyciskiem info
        HBox topPanel = new HBox(10);
        topPanel.setAlignment(Pos.CENTER_LEFT);
        topPanel.setPadding(new Insets(10));
        topPanel.setStyle("-fx-background-color: #F0F8FF;");

        Button infoButton = new Button("Info");
        infoButton.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        infoButton.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;");

        infoButton.setOnAction(event -> showInfoDialog());

        topPanel.getChildren().addAll(infoButton);

        // Grupujemy menuBar i topPanel w jeden pionowy układ (VBox), aby obydwa elementy zmieściły się na górze okna
        VBox topContainer = new VBox(menuBar, topPanel);

        // Obszar roboczy
        workspace = new Pane();
        workspace.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-width: 2px;");
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.widthProperty().bind(workspace.widthProperty());
        clip.heightProperty().bind(workspace.heightProperty());
        workspace.setClip(clip);

        EditorController controller = new EditorController(workspace);

        // Podpinamy akcje pod menu
        itemOkrag.setOnAction(e -> controller.setTool(EditorController.Tool.CIRCLE));
        itemProstokat.setOnAction(e -> controller.setTool(EditorController.Tool.RECTANGLE));
        itemWielokat.setOnAction(e -> controller.setTool(EditorController.Tool.POLYGON));

        // Składanie głównego okna
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topContainer);  
        borderPane.setCenter(workspace); 
        BorderPane.setMargin(workspace, new Insets(10));
        borderPane.setStyle("-fx-background-color: #F0F8FF;");

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setTitle("Geometrical Figure Editor");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    // Wyświetlanie okienka dialogowego po naciśnięciu guzika Info
    private void showInfoDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacje o programie");
        alert.setHeaderText("Geometrical Figure Editor");
        alert.setContentText("Przeznaczenie: Rysowanie i edycja figur geometrycznych.\nAutor: Artiom Borokhov");
        alert.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}