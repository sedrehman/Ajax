package p1;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Server {
	private static final int PORT = 8080;
	private static FileReaderHelper helper = new FileReaderHelper();
	private static boolean isPost = false;
	private static int FILE_COUNTER = 0;
	private static final String FILE_NAME = "file";
	private static String output = "";
	
	public static void main(String[] args) {
		//Header header ;
		Map<String, String> headers;
		boolean moveon = false;
		try {
			ServerSocket server = new ServerSocket(PORT);
			System.out.println("listenning to port 8080 . . . ");
			Socket client;
			InputStream is;
			while(true) {
				moveon = false;
				isPost = false;
				headers = new HashMap<>();
				
				client = server.accept();
				is = client.getInputStream();
				
				//~~~~~~~~~~~~~~~read first line ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				int byte_in;
				byte[] whole_line = new byte[1000];
				int counter = 0;
				while((byte_in = is.read()) != 0xA) {
					whole_line[counter++] = (byte) byte_in;
					if(counter > 999) {
						moveon = true;
						break;
					}
				}
				if(moveon) {
					continue;
				}
				byte[] actual_line = new byte[counter];
				
				while(counter >= 1) {
					actual_line[--counter] = whole_line[counter];
				}
				String line = new String(actual_line, StandardCharsets.UTF_8);
				if(line.length() < 3) {
					continue;
				}
				System.out.println(line);
				//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				//System.out.println("################## HEADER ####################");

				String[] type = line.split(" ");
				if(type[0].equals("POST")) {
					isPost = true;
					headers.put("Path", type[1] );
					
					while( (is.available() > 0) && (line.length() > 2) ) {
						//~~~~~~~~~~~~~~~~~~~~~ read till new line ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
						counter = 0;
						while( (byte_in = is.read()) != 0xA) {
							whole_line[counter++] = (byte) byte_in;
						}
						actual_line = new byte[counter];
						
						while(counter >= 1) {
							actual_line[--counter] = whole_line[counter];
						}
						line = new String(actual_line, StandardCharsets.UTF_8);
						//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

						type = line.split(":");
						switch(type[0]) {
						case "Content-Length":
							headers.put("Content-Length", type[1].trim());
							break;
						case "Content-Type":
							
							
							String[] content = type[1].split(";");
							String content_type = content[0].trim();
							if(content_type.equals("multipart/form-data")) {
								headers.put("boundary", content[1].trim().split("=")[1].trim());
							}
							headers.put("Content-Type", content[0].trim());
							break;
						}
					}
					
				}else {
					//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ GET~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					headers.put("Path", type[1].trim() );
					while( (is.available() > 0) && line.length() > 0 ) {
						
						//~~~~~~~~~~~~~~~~~~~~~ read till new line ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
						counter = 0;
						while((byte_in = is.read()) != 0xA) {
							whole_line[counter++] = (byte) byte_in;
						}
						actual_line = new byte[counter];
						
						while(counter >= 1) {
							actual_line[--counter] = whole_line[counter];
						}
						line = new String(actual_line, StandardCharsets.UTF_8);
						//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
						type = line.split(":");
						switch(type[0]) {
						case "Accept":
							headers.put("Accept", type[1].split(",")[0].trim());
							break;
						case "Cookie":
							headers.put("Cookie", type[1].trim());
							break;
						}
					}
					handleGet(new Header(), headers, client);
				}
				//System.out.println("############### End of HEADER #################");
				if(isPost) {
					byte[] data;
					System.out.println("post request: content-type="+ headers.get("Content-Type") + " length=" + headers.get("Content-Length"));
					System.out.print("########### about to read data..");
					int data_size = Integer.parseInt(headers.get("Content-Length")) ;
					data = new byte[data_size ];
					int offset = data_size/30;
					for(int i = 0; i< data_size; i++ ) {
						data[i] = (byte) is.read();
						
						if(i % offset == 0) {
							System.out.print(".");
						}
					}
					System.out.print("done reading...###########\n\n");
					if(headers.get("Content-Type").equals("text/plain")) {
						handlePostPlainData(data, headers, data_size, client);
					}else {
						handlePostData(data, headers, data_size, client);
					}
					
				}
				
				is.close();
				client.close();
				System.out.println("\n\n");
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static void handlePostPlainData(byte[] body, Map<String, String> headers, int body_size, Socket client) throws IOException {
		WriteHelper helper = new WriteHelper();
		int counter = 0;
		int byte_in;
		byte[] whole_line = new byte[1000];
		byte[] actual_line;
		String line;
		for(int i = 0; i< body_size; i++) {
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Get line~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			counter = 0;
			while(i < body_size && (byte_in = body[i]) != 0xA) {
				whole_line[counter++] = (byte) byte_in;
				i++;
			}
			actual_line = new byte[counter];
			
			while(counter >= 1) {
				actual_line[--counter] = whole_line[counter];
			}
			line = new String(actual_line, StandardCharsets.UTF_8);
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if(line.length() > 2) {
				
				System.out.println("~"+line);
				line = line.replaceAll("<", "&lt;");
				line = line.replaceAll(">", "&gt;");
				line = line.replaceAll("&", "&amp;");
				line = line.replaceAll("\\{", " ");
				line = line.replaceAll("\\}", " ");
				line = line.replaceAll("\"", "");
				System.out.println("~~ " + line);
				output += line + "\n";
			}
		}
		
		WriteHelper writer = new WriteHelper();
		writer.writeStringToFile(output, "output.txt");
	}

	private static void sendPostResponse(Map<String, String> headers, Header header, Socket client) {
		String body = helper.getHttpResponse("/post");
		String head = header.createHeader("Content-Type: text/html\n", "", body.length());
		try {
			PrintWriter writer = new PrintWriter(client.getOutputStream());
			writer.println(head);
			writer.flush();
			writer.println(body);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private static void handleGet(Header header, Map<String, String> parameters, Socket client) {
		String head = "";
		String body = "";
		boolean dontsend = false;
		String path = "../images/"+ parameters.get("Path");
		
		switch(parameters.get("Accept")) {
		case "text/html":
			System.out.println("Responding with html");
			
			body = helper.getHttpResponse("/");
			head = header.createHeader("Content-Type: text/html\n", "", body.length());
			
			break;
		
		case "text/css":
			System.out.println("Responding with css");
			body = helper.getCssResponse();
			head = header.createHeader("Content-Type: text/css\n", "", body.length());
			break;
		
		case "*/*":
			if(parameters.get("Path").contains(".txt")) {
				System.out.println("Responding with :" + parameters.get("Path"));
				body = helper.getHttpResponse(parameters.get("Path"));
				System.out.println(body);
				head = "HTTP/1.1 200 OK\nContent-Type: text/plain\nContent-length:" + body.length() +"\n";
			}else {
				System.out.println("Responding with js");
				body = helper.getJSResponse();
				head = header.createHeader("Content-Type: text/javascript\n", "", body.length());
			}
			
			break;
		
		case "image/webp":
			if(parameters.get("Path").contains("favicon")) {
				break;
			}
			System.out.println("Responding with img");
			dontsend = true;
			helper.sendImageFile(path, client);
			break;
			
		default:
			System.out.println("404");
			head = header.create404Header();
			body = "THE CONTENT WAS NOT FOUND";
		}
		
		if(!dontsend) {
			try {
				PrintWriter writer = new PrintWriter(client.getOutputStream());
				writer.println(head);
				writer.flush();
				writer.println(body);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
	}
	
	private static void handlePostData(byte[] body, Map<String, String> headers, int body_size, Socket client) throws IOException {
		
		ArrayList<String> datas = new ArrayList<>();
		String endkey = headers.get("boundary").trim() + "--";
		WriteHelper helper = new WriteHelper();
		int counter = 0;
		int byte_in;
		byte[] whole_line = new byte[1000];
		byte[] actual_line;
		String line;
		String type[];
		boolean isImage = false;
		for(int i = 0; i< body_size; i++) {
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Get line~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			counter = 0;
			while((byte_in = body[i]) != 0xA) {
				whole_line[counter++] = (byte) byte_in;
				i++;
			}
			actual_line = new byte[counter];
			
			while(counter >= 1) {
				actual_line[--counter] = whole_line[counter];
			}
			line = new String(actual_line, StandardCharsets.UTF_8);
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if(line.length() > 2) {
				System.out.println(line);
				type = line.split(":");
				if(type.length > 1) {
					String temp[] = type[1].split(";");

					switch(type[0].trim()) {
					case "Content-Disposition":
						headers.put("Content-Disposition", temp[0].trim());
						//Content-Disposition: form-data; name="upload"; filename="icon.png"
						//   temp=                 0           1                  2
						for(int k =1; k < temp.length; k++) {
							temp[k] = temp[k].replaceAll("\"", "");
							String val[] = temp[k].trim().split("=");
							//headers.put(val[0], val[1]);
							if(val[0].contains("name")) {
								datas.add(val[1].replaceAll("\"", ""));
							}
						}
						break;
					case "Content-Type":

						headers.put("Content-Type", temp[0].trim());
						//Content-Type: image/png
						//  temp=          0
						if(temp[0].contains("image")) {
							isImage = true;
							headers.put("filetype", temp[0].trim().split("/")[1]);
						}
						break;
					}
					if(isImage) {
						
						// handle image from i to (end - endKey.length())
						String img_type = headers.get("filetype");
						System.out.println("%%%%%%%%%%%%%%%%%% "+ img_type +" image %%%%%%%%%%%%%%%%%%%%%%%%%");
						byte[] file_ = Arrays.copyOfRange(body, i + 3, body_size- (endkey.length() +6));
						helper.writeImageToFile(file_, FILE_NAME + Integer.toString(FILE_COUNTER++) , img_type);
						i = body_size;
						break;
					}
				}
				else if(line.contains(endkey)) {
					//we are done. break everything.
					i = body_size;
					break;
				}else {
					if(!line.contains(headers.get("boundary").trim())) {
						datas.add(line.trim());
					}
				}
			}
			
		}
		if(isImage) {
			sendPostResponse(headers, new Header(), client);
		}else {
			String output = datas.toString();
			WriteHelper writer = new WriteHelper();
			writer.writeStringToFile(output, "output.txt");
		}
	}
	

	
}
