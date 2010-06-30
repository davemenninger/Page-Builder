package davesPageMaker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfLayer;
import com.itextpdf.text.pdf.PdfWriter;

public class MyPageBuilder {

	public static void main(String[] args) {
		// step 1: creation of a document-object
		Document document = new Document(PageSize.LETTER);
		try {
			// step 2:
			// we create a writer
			PdfWriter writer = PdfWriter.getInstance(
			// that listens to the document
					document,
					// and directs a PDF-stream to a file
					new FileOutputStream("results/davesPageMaker/my_book.pdf"));
			writer.setPdfVersion(PdfWriter.VERSION_1_5);
			// step 3: we open the document
			document.open();
			// step 4:
			PdfContentByte cb = writer.getDirectContent();

			
			//something about args.length should be put in here
			 
			//getting command line arguments
			String arg1_directory = args[0];
			System.out.println("Argument 1, directory was: ".concat(arg1_directory));
			
			String arg2_image_scale = args[1];
			System.out.println("Argument 2, image scale was: ".concat(arg2_image_scale));
			
			String arg3_font_size = args[2];
			System.out.println("Argument 3, font size was: ".concat(arg3_font_size));
			
			//reading a directory full of image files
			File dir = new File(arg1_directory);
			FilenameFilter images_filter = new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        //return !name.startsWith(".");
			        return name.endsWith(".tif");
			    }
			};
			String[] list_of_image_files = dir.list(images_filter);
			//sort this arry

			if (list_of_image_files == null) {
			    // Either dir does not exist or is not a directory
				System.out.println("dir not found or is empty: ".concat(arg1_directory));
			} else {
				System.out.println("Listing image contents of ".concat(arg1_directory).concat("..."));
			    for (int i=0; i<list_of_image_files.length; i++) {
			        // Get filename of file or directory
			        String filename = list_of_image_files[i];
			        System.out.println("-> ".concat(filename));
			    }
			}
			int num_images_found = list_of_image_files.length;
			System.out.println("...found ".concat(Integer.toString(num_images_found)).concat(" images."));
			
			//reading in the corresponding text files from the same directory
			FilenameFilter text_filter = new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        //return !name.startsWith(".");
			        return name.endsWith(".txt");
			    }
			};
			String[] list_of_text_files = dir.list(text_filter);
			//sort this array

			if (list_of_text_files == null) {
			    // Either dir does not exist or is not a directory
				System.out.println("dir not found or is empty: ".concat(arg1_directory));
			} else {
				System.out.println("Listing .txt contents of ".concat(arg1_directory).concat("..."));
			    for (int i=0; i<list_of_text_files.length; i++) {
			        // Get filename of file or directory
			        String filename = list_of_text_files[i];
			        System.out.println("-> ".concat(filename));
			    }
			}
			int num_text_found = list_of_text_files.length;
			System.out.println("...found ".concat(Integer.toString(num_text_found)).concat(" texts."));
			
			int num_pages = 0;
			//check to make sure the directory is copacetic
			if ( num_images_found == num_text_found ){
				System.out.println("Number of images matches number of test files.  :)");
				num_pages = num_images_found;
			}
			else if( num_images_found < num_text_found ){
				System.out.println("More text files than image files.  :(");
			}
			else if( num_images_found > num_text_found ){
				System.out.println("More image files than text files.  :(");
			}
			
			//amount to scale image by (percent):
			//  note: if this number is too big, the program crashes
			float img_scale_percent = 25; 
			img_scale_percent = Float.valueOf(arg2_image_scale);
			
			//font type, size, color:
			float font_size = 12;
			font_size = Float.valueOf(arg3_font_size);
			Font my_font = FontFactory.getFont(FontFactory.HELVETICA, font_size, Font.NORMAL, new BaseColor(0, 0, 0));

			//loop that creates many pages
			for (int i=1; i <= num_pages; i++){
				//get the image file for this page
				String img_path = "results/in_action/chapter12/img2.tif";
				img_path = list_of_image_files[i-1];
				System.out.println("-> read image file ".concat(Integer.toString(i)).concat(": ").concat(img_path));
				Image img_n = Image.getInstance(arg1_directory.concat(img_path));
				
				//get the OCR text out of the file for this page
				String text_path = "results/in_action/chapter12/text2.txt";
				text_path = list_of_text_files[i-1];
				char[] buf = new char[512];
				int len = 0;
				StringBuilder builder = new StringBuilder();
				FileReader reader = new FileReader(arg1_directory.concat(text_path));
				while (  (    (len = reader.read(buf, 0, buf.length)) != -1) ){
					builder.append(buf, 0, len);
				}
				System.out.println("-> read text file ".concat(Integer.toString(i)).concat(": ").concat(text_path));
				String text_n = builder.toString();
				
				//Build a page with a nested layers; one: scanned image, two: invisible text layer
				PdfLayer page_n = new PdfLayer("Page ".concat(Integer.toString(i)),writer);
				PdfLayer page_n_image = new PdfLayer("Page ".concat(Integer.toString(i)).concat(" Image"), writer);
				PdfLayer page_n_text = new PdfLayer("Page ".concat(Integer.toString(i)).concat(" Text"), writer);

				page_n.addChild(page_n_image);
				page_n.addChild(page_n_text);

				cb.beginLayer(page_n);
				ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("page ".concat(Integer.toString(i))), 50, 775, 0);
				cb.endLayer();
				
				cb.beginLayer(page_n_image);
				img_n.scalePercent(img_scale_percent);
				document.add(img_n);
				cb.endLayer();
				
				cb.beginLayer(page_n_text);
				
				//put a white semi-transparent box as a base
				cb.saveState();
				PdfGState gs1 = new PdfGState();
				gs1.setFillOpacity(0.9f);
				cb.setGState(gs1);
				cb.setColorFill(new GrayColor(255));
				cb.rectangle(25, 25, PageSize.LETTER.getWidth() - 50, PageSize.LETTER.getHeight() - 50);
				cb.fillStroke();
				cb.restoreState();
				
				//put the text from the input file
				ColumnText column_n = new ColumnText(cb);
				column_n.setSimpleColumn(36, 36, PageSize.LETTER.getWidth() - 36, PageSize.LETTER.getHeight() - 36);
				column_n.addText(new Phrase( text_n, my_font ) );
				column_n.go();
				
				cb.endLayer();
				
				//make the text layer initially invisible
				page_n_text.setOn(false);
				
				document.newPage();

			}

		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
 
		//close the document
		document.close();
		System.out.println("File generated: my_book.pdf");
	}
}
