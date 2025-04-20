package Util;
import java.util.*;
import java.io.*;
import com.google.gson.Gson;

public class TexttoJSON {

	public static void main(String[] args) {
		String inputFile = "C:/Users/Sam/Downloads/extracted_text.txt";  // Your TXT file
        String outputFile = "C:/Users/Sam/Downloads/output.json";

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);  // Store each line as an element in the list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = gson.toJson(lines);

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Conversion completed! JSON saved to " + outputFile);

	}

}
