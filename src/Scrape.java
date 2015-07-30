
import org.jsoup.Jsoup;
//import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Scrape{
	
	private ArrayList<Book> found = new ArrayList<Book>();
	private ArrayList<Book> notInStock = new ArrayList<Book>();
	private double qIssue;
	
	public void getScrape(String query, double issue) throws IOException{
	  String url = "http://www.mycomicshop.com/search?q="+query;
	  qIssue = issue;
	  Document doc = Jsoup.connect(url).get();
	  wipe(doc);
			
	}
	
	public void wipe(Document data){	
	  Elements content = data.select("td[itemtype=http://schema.org/Product]");
	  ArrayList clean = new ArrayList();
	  double price = 0;
	  
	  for(Element line: content){
		 
		  String condition = line.getElementsByAttributeValue("itemprop", "itemCondition").attr("content");
		  String name = line.getElementsByAttributeValue("itemprop", "name").attr("content").trim();
		  //
		  String[] possibleIssue =name.split("\\s+",-1);
		 
		  clean =  clean(possibleIssue);
		  
		  name = (String) clean.get(0);
		  int year =  (int) clean.get(1);
		 // System.out.println("*"+clean+'*');
		  try{
		   price = Double.parseDouble(line.getElementsByAttributeValue("itemprop", "price").attr("content"));	
		   MCSBook temp = new MCSBook(name, qIssue, condition,price, year);
		   found.add(temp);
		 }
		  catch(Exception e){
			  price = 0.0;
			   MCSBook temp = new MCSBook(name, qIssue, condition,price, year);
			   notInStock.add(temp);
		  }
		  
	  }	
	}
	
	public ArrayList clean(String[] dirty){
		ArrayList data = new ArrayList();
		String searchable = Double.toString(qIssue);
		String label =  "";
		int titleEnd = 0;
		String title = "";
		int year = 0;
		searchable = searchable.substring(0, (searchable.length()-2));
		for (int i = dirty.length -1; i > 0; i--) {
			
			if(dirty[i].startsWith(searchable)){
				label = dirty[i];
			}else if(dirty[i].startsWith("(") && dirty[i].endsWith(")")){
				year =  Integer.parseInt(dirty[i].substring(1, 5)); 
				titleEnd = i;
			}
			
		}

		//if(titleEnd == 0){
			for(int i  = 0; i <= titleEnd; i++){
				title += dirty[i]+" ";
			}
		/*}else{
			for(int i  = 0; i <= titleEnd; i++){
				title += dirty[i]+" ";
			}
		}*/
		
		if(title.isEmpty()){
			System.out.println(Arrays.asList(dirty));
			
			System.exit(0);
		}
		
		title = title.trim();
		data.add(title);
		data.add(year);
		data.add(label);
		
		return data;
	}
	
	public ArrayList<Book> getFoundBooks(){
		return found;
		
	}
	
	public void listOfFoundBooks(){
		for(Book item: found){
			item.summery();
		}
	}
	
	
}
