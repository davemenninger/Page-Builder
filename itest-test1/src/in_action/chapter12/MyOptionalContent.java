
/* in_action/chapter12/MyOptionalContent.java */
 
package in_action.chapter12;
 
import java.io.*;
//import java.util.ArrayList;
 
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*; 


public class MyOptionalContent {
 
	public static void main(String[] args) {
		System.out.println("-> file generated: my_optional_content.pdf");
		// step 1: creation of a document-object
		Document document = new Document();
		try {
			// step 2:
			// we create a writer
			PdfWriter writer = PdfWriter.getInstance(
			// that listens to the document
					document,
					// and directs a PDF-stream to a file
					new FileOutputStream("results/in_action/chapter12/my_optional_content.pdf"));
			writer.setPdfVersion(PdfWriter.VERSION_1_5);
			// step 3: we open the document
			document.open();
			// step 4:
			PdfContentByte cb = writer.getDirectContent();

			//get the OCR text out of a file
			char[] buf = new char[512];
			int len = 0;
			StringBuilder builder = new StringBuilder();
			FileReader reader = new FileReader("results/in_action/chapter12/text2.txt");
			while (  (    (len = reader.read(buf, 0, buf.length)) != -1) ){
				builder.append(buf, 0, len);
			}
			String text1 = builder.toString();
			
			//path to the image file
			String img_path = "results/in_action/chapter12/img2.tif";

			//loop that creates many pages
			for (int i=1; i <= 3; i++){

				//Build a page with a nested invisible text layer
				PdfLayer page_n = new PdfLayer("Page ".concat(Integer.toString(i)),writer);
				PdfLayer page_n_image = new PdfLayer("Page ".concat(Integer.toString(i)).concat(" Image"), writer);
				PdfLayer page_n_text = new PdfLayer("Page ".concat(Integer.toString(i)).concat(" Text"), writer);

				page_n.addChild(page_n_image);
				page_n.addChild(page_n_text);

				cb.beginLayer(page_n);
				ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("this is page".concat(Integer.toString(i))), 50, 775, 0);
				cb.endLayer();
				
				cb.beginLayer(page_n_image);
				Image img_n = Image.getInstance(img_path);
				img_n.scalePercent(25);
				document.add(img_n);
				cb.endLayer();
				
				cb.beginLayer(page_n_text);
				
				cb.saveState();
				PdfGState gs1 = new PdfGState();
				gs1.setFillOpacity(0.9f);
				cb.setGState(gs1);
				cb.setColorFill(new GrayColor(255));
				cb.rectangle(20, 20, 500, 800);
				cb.fillStroke();
				cb.restoreState();
				
				ColumnText column_n = new ColumnText(cb);
				column_n.addText(new Phrase(text1.concat("this is the OCR".concat(Integer.toString(i)))));
				column_n.setSimpleColumn(20, 20, 500, 800);
				column_n.go();
				
				cb.endLayer();
				
				page_n_text.setOn(false);
				
				document.newPage();

			}

		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
 
		// step 5: we close the document
		document.close();
	}
}