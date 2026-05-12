import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*; 
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane; 
import javafx.stage.Stage;

/**
 * Główna klasa aplikacji (punkt wejścia) oparta na architekturze JavaFX.
 * Odpowiada za inicjalizację interfejsu graficznego (GUI), paska menu 
 * oraz połączenie widoku z kontrolerem logiki ({@link EditorController}).
 */
public class Main extends Application {
    
    /** Obszar roboczy, na którym renderowane są figury geometryczne. */
    private Pane workspace;

    /**
     * Standardowa metoda rozruchowa aplikacji JavaFX.
     * 
     * @param args Argumenty wiersza poleceń.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Metoda inicjalizująca i budująca główne okno programu.
     * Konfiguruje układ wizualny (BorderPane), maskę przycinającą płótno,
     * system menu oraz obsługę systemowych okien wyboru plików (zapis/odczyt).
     * 
     * @param primaryStage Główne okno aplikacji dostarczane przez platformę JavaFX.
     * @throws Exception W przypadku błędu podczas inicjalizacji widoku.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // 1. TWORZENIE PASKA MENU (MenuBar)
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
        MenuItem itemInfo = new MenuItem("Informacje o programie"); 
        menuPomoc.getItems().addAll(itemInstrukcja, itemInfo);

        // Podpięcie akcji do menu Pomoc
        itemInstrukcja.setOnAction(e -> showHelpDialog());
        itemInfo.setOnAction(e -> showInfoDialog());
        
        menuBar.getMenus().addAll(menuPlik, menuFigury, menuPomoc);

        // 2. OBSZAR ROBOCZY (Pane) z maską przycinającą
        workspace = new Pane();
        workspace.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-width: 2px;");
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.widthProperty().bind(workspace.widthProperty());
        clip.heightProperty().bind(workspace.heightProperty());
        workspace.setClip(clip);

        // 3. TWORZENIE KONTROLERA
        EditorController controller = new EditorController(workspace);

        // Podpinamy akcje pod menu figur
        itemOkrag.setOnAction(e -> controller.setTool(EditorController.Tool.CIRCLE));
        itemProstokat.setOnAction(e -> controller.setTool(EditorController.Tool.RECTANGLE));
        itemWielokat.setOnAction(e -> controller.setTool(EditorController.Tool.POLYGON));

        // 4. OBSŁUGA ZAPISU I ODCZYTU (FileChooser)
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Pliki Edytora Graficznego", "*.edy"));

        itemZapisz.setOnAction(e -> {
            java.io.File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    FileManager.saveToFile(controller.getShapesAsData(), file);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        itemWczytaj.setOnAction(e -> {
            java.io.File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    List<ShapeData> data = FileManager.loadFromFile(file);
                    controller.loadShapesFromData(data);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 5. SKŁADANIE GŁÓWNEGO OKNA
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);  
        borderPane.setCenter(workspace); 
        BorderPane.setMargin(workspace, new Insets(10));
        borderPane.setStyle("-fx-background-color: #F0F8FF;");

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setTitle("Geometrical Figure Editor");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Wyświetla informacyjne okno dialogowe z danymi o programie oraz autorze.
     */
    private void showInfoDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacje o programie");
        alert.setHeaderText("Geometrical Figure Editor");
        alert.setContentText("Przeznaczenie: Rysowanie i edycja figur geometrycznych.\nAutor: Artiom Borokhov");
        alert.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    /**
     * Wyświetla okno dialogowe z instrukcją obsługi edytora dla użytkownika końcowego.
     */
    private void showHelpDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instrukcja użytkownika");
        alert.setHeaderText("Jak korzystać z Edytora Graficznego");
        
        String instrukcja = "1. RYSOWANIE: Wybierz figurę z menu 'Figury'. Kliknij lewym przyciskiem myszy (LPM) na płótnie i przeciągnij, aby narysować kształt.\n\n"
                          + "2. PRZESUWANIE: Kliknij narysowaną figurę (LPM), aby ją zaznaczyć (pojawi się czerwona ramka). Z wciśniętym LPM możesz ją przesuwać.\n\n"
                          + "3. SKALOWANIE: Najedź kursorem na figurę i użyj rolki myszy (Scroll), aby ją powiększyć lub pomniejszyć.\n\n"
                          + "4. OBRACANIE: Przytrzymaj klawisz CTRL i użyj rolki myszy, aby obrócić figurę.\n\n"
                          + "5. KOLORY: Kliknij figurę prawym przyciskiem myszy (PPM), aby otworzyć menu zmiany koloru.\n\n"
                          + "6. ZAPIS/ODCZYT: Użyj menu 'Plik', aby zapisać swoje dzieło do pliku *.edy lub wczytać poprzedni projekt.";
                          
        alert.setContentText(instrukcja);
        alert.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(400); 

        alert.showAndWait();
    }
}