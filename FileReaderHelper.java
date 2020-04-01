package p1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class FileReaderHelper {
	
	protected String getHttpResponse( String choice) {
		String response = "";
		switch(choice) {
		case "/":
			response =  readFileToString("../website/first.html");
			return response;
		case "/post":
			response = readFileToString("../website/first_part.html");
			response += sendAllImages();
			response += readFileToString("../website/second.html");
			
			System.out.println(response);
			return response;
		case"/after.txt":
			response = readFileToString("../website" + choice);
			return response;
		case "/output.txt":
			response = readFileToString("../website" + choice);
			return response;
		default:
		}
		return response;
	}
	
	protected String getCssResponse() {		
		String response = readFileToString("../website/style.css");
		return response;
	}
	
	protected String getJSResponse() {
		String response = readFileToString("../website/script.js");
		return response;
	}
	
	protected String readFileToString(String path) {
		URL url = this.getClass().getResource(path);
		if(url == null) {
			System.out.println(path + "    is incorrect");
			return null;
		}
		String result = "";
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(url.getFile()));
			BufferedReader reader = new BufferedReader(isr);
			String line = reader.readLine();
			while(line != null) {
				result += line + "\n";
				line = reader.readLine();
			}
			
			reader.close();
			isr.close();
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	protected void sendImageFile(String path, Socket socket) {
		 
		URL url = this.getClass().getResource(path);
		
		File file = new File(url.getFile());
		try {
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			writer.println("HTTP/1.1 200 OK\nContent-Type: image/jpeg\nContent-length:"+ Long.toString(file.length())+"\n");
			writer.flush();
			
			OutputStream os = socket.getOutputStream();
			byte[] buffer = new byte[1024];
            int bytesRead;
            InputStream is = new FileInputStream(file);
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
			
			writer.close();
			os.close();
			is.close();
			socket.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected String readBody(int len, Socket client, InputStream is) throws IOException {
		
		byte[] dataBytes = new byte[len];
		System.out.println("before~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		is.read(dataBytes);
		String s = new String(dataBytes, StandardCharsets.UTF_8);
		System.out.println("here~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		return s;
	}
	
	protected String sendAllImages() {
		URL url = this.getClass().getResource("../images");
		File folder = new File(url.getFile());
		ArrayList<String> filenames = listFilesForFolder(folder);
		String img_tag = "";
		
		for(int i = 0; i< filenames.size(); i++) {
			img_tag += "<p><img src="+ filenames.get(i)+ " alt=\":)\" width=60%></p>\n";
		}
		
		return img_tag;
	}
	
	private ArrayList<String> listFilesForFolder(final File folder) {
		System.out.println(folder.getAbsolutePath());
		ArrayList<String> filenames = new ArrayList<>();
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            System.out.println(fileEntry.getName());
	            filenames.add(fileEntry.getName());
	        }
	    }
	    return filenames;
	}
	
}





