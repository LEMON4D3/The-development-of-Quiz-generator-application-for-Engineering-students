package Util;
import org.apache.pdfbox.*;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.*;
import java.io.FileWriter;
import java.io.File;
public class PDFManager {

	public static void main(String[] args) throws Exception {
		File pdfFile = new File("C:/Users/Sam/Downloads/poopoo.pdf");
		File textFilePath= new File("C:/Users/Sam/Downloads/extracted_text.txt");
		PDDocument pdDocument = Loader.loadPDF(pdfFile);
		PDFTextStripper pdfStripper = new PDFTextStripper();
		String text = pdfStripper.getText(pdDocument);
		System.out.println(text);
		
		try (FileWriter writer = new FileWriter(textFilePath)) {
			writer.write(text);
		}
        System.out.println("Text extracted and saved successfully.");
        
	}
}
