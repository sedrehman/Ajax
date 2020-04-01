package p1;

public class Header {
	
	public Header() {
		//nada;
	}


	public  static String chop(String in) {
	    if ((in != null) && (in.length() > 0) && (in.charAt(in.length() - 1) == '\n' )) {
	        in = in.substring(0, in.length() - 1);
	    }
	    return in;
	}
	
	public String createHeader(String type, String cookie, double size ) {
		return ( "HTTP/1.1 200 OK\nLocation: /\n" + type + cookie + Double.toString(size) + "\n");
	}
	
	public String create301Header(String location) {
		 return "HTTP/1.1 301 OK\nLocation: "+ location + "\n"; 
	}
	
	public String create404Header() {
		return "HTTP/1.1 404 Not Found\n";
	}
}
