import java.io.*;
import java.util.List;

public class FileManager {
    
    // Zapisuje listę obiektów ShapeData do wskazanego pliku
    public static void saveToFile(List<ShapeData> dataList, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(dataList);
        }
    }

    // Odczytuje listę obiektów ShapeData z pliku
    @SuppressWarnings("unchecked")
    public static List<ShapeData> loadFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ShapeData>) ois.readObject();
        }
    }
}