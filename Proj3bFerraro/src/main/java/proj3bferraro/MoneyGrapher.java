// Zachary Ferraro
// IT313 Project 3b
// JFreeChart Graphs
//
//This is a program that reads a text file of a list of currencies, then uses
//that list to retrieve exchange rates for each of those currencies from an API
//and then graphs those exchange rates in a bar chart using JFreeChart, then
//writes that graph to a file called ExchangeRates.png
//
// Note that unindented "//" indicates a line of debugging code

package proj3bferraro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

//External libraries are imported based on the Maven pom.xml file
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

//Gson is a google library to interpret and parse JSon objects
import com.google.gson.*;

public class MoneyGrapher {

	public static void main(String[] args) {
		
		String fileName = "currencies.txt";
		ArrayList<String> currencies = getCurrencies(fileName);
		
//		System.out.println(currencies.get(1));
		
        // Initialize a new dataset to use in the bar graph
        DefaultCategoryDataset barChartDataset = new DefaultCategoryDataset();
        
        //Iterate through each currency in the currencies.txt doc
		for (String targetCurrency : currencies) {
			
			//Get the URL for the target currency by passing into getUrlString method
			String url = getUrlString(targetCurrency);
			
			//Get the exchange rate for the target currency by passing the url and the currency Strings
			//into the getExchangeRate method
			double exchangeRate = getExchangeRate(url, targetCurrency);
			
			//Add the current currency and its exchange rate to the data set
			barChartDataset.addValue(exchangeRate, "rate", targetCurrency);
		}
		
		
		try {
                      
            // Define JFreeChart object that creates line chart.
            JFreeChart barChartObject = ChartFactory.createBarChart(
                "Currency Exchange Rates (to EUR)", "Currency", "Exchange Rate To EUR", barChartDataset,
                PlotOrientation.VERTICAL, 
                false,  // Include legend.
                false,  // Include tooltips.
                false); // Include URLs.               
                          
             // Write line chart to a file.               
             int imageWidth = 640;
             int imageHeight = 480;                
             
             //Write to the file ExchangeRates.png
             File barChart = new File("ExchangeRates.png");              
             ChartUtilities.saveChartAsPNG(barChart, barChartObject, imageWidth, imageHeight); 
        }
      
        catch (Exception i)
        {
            System.out.println("There was an error creating your chart: " + i);
        }
		
	}
	
	//Pass a file into this method to loop through it and return an ArrayList of
	//each line, which will be a set of ISO 4217 currency codes.
	public static ArrayList<String> getCurrencies(String fileName) {
		
		//Initialize a new scanner and array list to be used inside a
		//try/catch and while loop
		Scanner readCurrencies = null;
		ArrayList<String> currencies = new ArrayList<String>();
		
//		int i = 0;
		
		//Create target File with the filename string passed into the method
		File file = new File(fileName);
		
		//Surround the scanner implementation with a try catch
		try {
			readCurrencies = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
		
		//Read each line from the file and add to the ArrayList 
		while(readCurrencies.hasNextLine()) {
			currencies.add(readCurrencies.nextLine());
//			System.out.println(currencies.get(i));
//			i++;
		}
		
		//Close Scanner
		readCurrencies.close();
		
		//Return the ArrayList of all the currency codes
		return currencies;
		
	}
	
	//Pass a currency code and returns a String of a target url
	public static String getUrlString(String targetCurrency) {
		
		String prefix = "http://data.fixer.io/api/latest?access_key=";
		//My key for fixer.io
		String accessKey = "2b87551b1f467cf9ee35df5ba2397009";
		String insert = "&symbols=";
		
		//combine url with target currency code
		String urlString = prefix + accessKey + insert + targetCurrency;
		
//		System.out.println(urlString);
		
		return urlString;
	}
	
	//Pass the URL String and the target currency code to this method to get the exchange rate
	//by getting the full JSON and parsing it using gson library
	public static double getExchangeRate(String urlString, String targetCurrency) {
		
		//Declare URL for use in the try/catch
		URL url;
		
		//Initialize the Scanner so it can be used outside the try/catch
		Scanner in = null;
		String jsonString= "";
		
		//In a try/catch retrieve the input stream of the URL return via Scanner
		try {
			url = new URL(urlString);
			in = new Scanner(url.openStream());
		} catch(IOException e) {
			System.out.println("Either the URL is invalid, or the link is not resolving.");
		}
		
		String fileLine;
		
		//Loop through every line on the page and add to String
		while (in.hasNextLine()) {
			fileLine = in.nextLine();
			
//			System.out.println(fileLine);
			
			jsonString += fileLine;	
		}
		
//		System.out.println(jsonString);
		
		//Create gson parser, parse the string of JSON created by the scanner, then get the 
		//specific exchange rate field as a JSON primitive, and parse that into a double.
		JsonParser parser = new JsonParser();
		JsonElement rootElement = parser.parse(jsonString);
		JsonObject rootObject = rootElement.getAsJsonObject();
		JsonPrimitive exchangeRatePrimitive = rootObject.getAsJsonObject("rates").getAsJsonPrimitive(targetCurrency);
		double exchangeRate = exchangeRatePrimitive.getAsDouble();
		
//		System.out.println(exchangeRate);
		
		//close the Scanner
		in.close();
		
		//Return the exchange rate double
		return exchangeRate; 
	}

}
