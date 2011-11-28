package yuku.alkitab.kjvthml;

import java.io.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.ext.*;

public class Proses {
	private final SAXParserFactory factory = SAXParserFactory.newInstance();
	private Handler handler;
	PrintWriter pw;
	String redTerakhir = null;
	String scTerakhir = null;

	public static void main(String[] args) throws Exception {
		new Proses().loadUpTheData();
	}
	
	public void loadUpTheData() throws Exception {
		handler = new Handler();
		InputStream in = new FileInputStream("/Users/yuku/Downloads/kjv.xml");
		pw = new PrintWriter(new File("/Users/yuku/Downloads/kjv.proses"), "utf-8");

		SAXParser parser = factory.newSAXParser();
		parser.getXMLReader().setFeature("http://xml.org/sax/features/namespaces", true);
		parser.parse(in, handler);
		pw.close();
	}

	public class Handler extends DefaultHandler2 {
		String[] tree = new String[80];
		int depth = 0;
		StringBuilder b = new StringBuilder();
		boolean simpan = true;
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			tree[depth++] = localName;
			
			if (b.length() != 0) {
				System.out.println(b);
				output(b);
				b.setLength(0);
			}
			
			System.out.print("(start:) "); cetak();
			String alamat = alamat();
			if (alamat.endsWith("/scripture")) {
				String parsed = attributes.getValue("parsed");
				System.out.println(parsed);
				output("\n#ayat:" + parsed + "\n");
			} else if (alamat.endsWith("/sup") || alamat.endsWith("/h4") || alamat.endsWith("/h3") || alamat.endsWith("/h2") || alamat.endsWith("/h1")) {
				simpan = false;
				System.out.println("(simpan=false)");
			}
			
			if (alamat.startsWith("/ThML/ThML.body/div1/div2/div3/p")) {
				if (alamat.endsWith("/i")) {
					output("<i>");
					System.out.println("<i>");
				}
				if (alamat.endsWith("/span") && "red".equals(attributes.getValue("class"))) {
					output("<red>");
					System.out.println("<red>");
					redTerakhir = alamat;
				}
				if (alamat.endsWith("/span") && "sc".equals(attributes.getValue("class"))) {
					output("<sc>");
					System.out.println("<sc>");
					scTerakhir = alamat;
				}
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (simpan) b.append(ch, start, length);
		}
		
		private void cetak() {
			for (int i = 0; i < depth; i++) {
				System.out.print('/');
				System.out.print(tree[i]);
			}
			System.out.println();
		}
		
		private StringBuilder a = new StringBuilder();
		private String alamat() {
			a.setLength(0);
			for (int i = 0; i < depth; i++) {
				a.append('/').append(tree[i]);
			}
			return a.toString();
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (b.length() != 0) {
				System.out.println(b);
				output(b);
				b.setLength(0);
			}
			
			System.out.print("(end:) "); cetak();
			
			String alamat = alamat();
			if (alamat.startsWith("/ThML/ThML.body/div1/div2/div3/p")) {
				if (alamat.endsWith("/i")) {
					output("</i>");
					System.out.println("</i>");
				}
				if (alamat.endsWith("/span") && alamat.equals(redTerakhir)) {
					output("</red>");
					System.out.println("</red>");
					redTerakhir = null;
				}
				if (alamat.endsWith("/span") && alamat.equals(scTerakhir)) {
					output("</sc>");
					System.out.println("</sc>");
					scTerakhir = null;
				}
			}
			
			if (alamat.endsWith("/sup") || alamat.endsWith("/h4") || alamat.endsWith("/h3") || alamat.endsWith("/h2") || alamat.endsWith("/h1")) {
				simpan = true;
				System.out.println("(simpan=true)");
			}
			
			tree[--depth] = null;
		}
		
	}

	public void output(Object x) {
		if (x.equals("<sc>") || x.equals("</sc>")) {
			return;
		}
		
		if (scTerakhir != null) {
			pw.print(x.toString().toUpperCase());
		} else {
			pw.print(x);
		}
	}
}
