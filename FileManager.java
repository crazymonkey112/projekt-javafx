import java.io.*;
import java.util.List;

/**
 * Klasa narzędziowa obsługująca zapis i odczyt stanu aplikacji 
 * z wykorzystaniem binarnej serializacji obiektów.
 */
public class FileManager {
    
    /**
     * Zapisuje listę obiektów transferowych (DTO) do wskazanego pliku.
     * 
     * @param dataList Lista figur do zapisania.
     * @param file     Plik docelowy.
     * @throws IOException Jeśli wystąpi problem z dostępem do dysku lub strumienia zapisu.
     */
    public static void saveToFile(List<ShapeData> dataList, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(dataList);
        }
    }

    /**
     * Wczytuje listę obiektów transferowych ze wskazanego pliku.
     * 
     * @param file Plik źródłowy.
     * @return Odzyskana lista figur (ShapeData).
     * @throws IOException            Jeśli plik nie istnieje lub jest uszkodzony.
     * @throws ClassNotFoundException Jeśli dane w pliku nie pasują do definicji klasy ShapeData.
     */
    @SuppressWarnings("unchecked")
    public static List<ShapeData> loadFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ShapeData>) ois.readObject();
        }
    }
}