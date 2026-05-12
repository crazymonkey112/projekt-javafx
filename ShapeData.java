import java.io.Serializable;
import java.util.List;

/**
 * Klasa transferowa (DTO) przechowująca surowe dane figur do zapisu w pliku.
 */
public class ShapeData implements Serializable {
    
    /** Unikalny indentyfikator */
    private static final long serialVersionUID = 1L;

    public enum ShapeType { RECTANGLE, CIRCLE, POLYGON }
    
    public ShapeType type;
    
    /** Wspólne parametry transformacji */
    public double translateX, translateY, scaleX, scaleY, rotate;
    
    /** Kolor w formacie HEX (bo klasa Color nie jest serializowalna). */
    public String hexColor;

    /** Zmienne dla Prostokąta. */
    public double x, y, width, height;

    /** Zmienne dla Okręgu. */
    public double centerX, centerY, radius;

    /** Płaska lista współrzędnych [x, y] wierzchołków Wielokąta. */
    public List<Double> points;
}