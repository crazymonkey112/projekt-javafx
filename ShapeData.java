import java.io.Serializable;
import java.util.List;

public class ShapeData implements Serializable {
    // Unikalny identyfikator wersji
    private static final long serialVersionUID = 1L;

    public enum ShapeType { RECTANGLE, CIRCLE, POLYGON }
    
    public ShapeType type;
    
    // Wspólne właściwości transformacji
    public double translateX;
    public double translateY;
    public double scaleX;
    public double scaleY;
    public double rotate;
    
    // Kolor zapisujemy jako zwykły tekst, bo klasa Color nie jest serializowalna
    public String hexColor;

    // Zmienne dla Prostokąta
    public double x, y, width, height;

    // Zmienne dla Okręgu
    public double centerX, centerY, radius;

    // Zmienne dla Wielokąta
    public List<Double> points;
}