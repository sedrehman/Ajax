package p1;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class WriteHelper {
	
	public void writeStringToFile(String output, String name) throws IOException{
		URL url = this.getClass().getResource("../website");
		File folder = new File(url.getFile());
		FileOutputStream fos = new FileOutputStream(folder.getAbsolutePath()+ File.separator + name);
		fos.write(output.getBytes());
		fos.close();
	}
	
	public void appendStringToFile(String output, String path) {
		
	}
	
	public void writeImageToFile(byte[] data, String name, String type) throws IOException {
		URL url = this.getClass().getResource("../images");
		File folder = new File(url.getFile());
		String sep = File.separator;
		FileOutputStream fos = new FileOutputStream(folder.getAbsoluteFile()+ sep + name + "." + type);
		fos.write(data);
		fos.close(); 
	}
}
