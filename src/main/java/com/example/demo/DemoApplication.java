package com.example.demo;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.util.FileSystemUtils;
import java.io.File;  


@SpringBootApplication
// Create batches and caLL POST api
public class DemoApplication {
	//make list of all batches
	private ArrayList<Batch> batches = new ArrayList<Batch>();

	private String getQuery(ArrayList<Parameter> params) throws UnsupportedEncodingException 
	{
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (Parameter pair : params)
		{
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}
	//example run for test purposes only
    // public void DemoGet() throws IOException{
	// 	URL url = new URL("https://api.apis.guru/v2/list.json");
	// 	HttpURLConnection con = (HttpURLConnection) url.openConnection();
	// 	con.setRequestMethod("GET");
	// 	con.setRequestProperty("Content-Type", "application/json");
	// 	con.setConnectTimeout(5000);
	// 	con.setReadTimeout(5000);
	// 	int status = con.getResponseCode();
	// 	BufferedReader in = new BufferedReader(
	// 	new InputStreamReader(con.getInputStream()));
	// 	String inputLine;
	// 	StringBuffer content = new StringBuffer();
	// 	while ((inputLine = in.readLine()) != null) {
	// 		content.append(inputLine);
	// 	}
	// 	in.close();
	// 	con.disconnect();
	// 	System.out.println(content.toString());
	// }
	//Send all batches for the post api
	public void POSTRequest() throws IOException {
		if(batches.size() == 0) {
			System.out.println("No batches found for posting");
			return;
		}
		for(Batch batch : batches){
			File[] files = new File(batch.Path).listFiles(); 
			for (File file : files){
				ArrayList<Parameter> params = new ArrayList<Parameter>();
				//replace this hardcoded parameters with all batches
				params.add(new Parameter("operation", "aem_invoke_workflow"));
				params.add(new Parameter("fileNameLocation", file.getAbsolutePath()));
				params.add(new Parameter("batchId", batch.Name));

				URL obj = new URL("http://localhost:4502/libs/csv/components/structure/page/txt.servlet");
				String basicAuth = "Basic " + new String("YWRtaW46YWRtaW4=");
				HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
				postConnection.setRequestProperty ("Authorization", basicAuth);
				postConnection.setRequestMethod("POST");
				postConnection.setRequestProperty("userId", "a1bcdefgh");
				postConnection.setRequestProperty("Content-Type", "application/json");
				postConnection.setDoOutput(true);
				OutputStream os = postConnection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(params));
				writer.flush();
				writer.close();
				os.close();
				int responseCode = postConnection.getResponseCode();
				System.out.println("POST Response Code :  " + responseCode);
				System.out.println("POST Response Message : " + postConnection.getResponseMessage());
				if (responseCode == HttpURLConnection.HTTP_CREATED) { //success
					BufferedReader in = new BufferedReader(new InputStreamReader(
						postConnection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
			
					while ((inputLine = in .readLine()) != null) {
						response.append(inputLine);
					} in .close();
			
					// print result
					System.out.println(response.toString());
				} else {
					System.out.println("POST NOT WORKED");
				}
			}
		}
	}

	public boolean CreateBatches(){
		//load config.properties
		Properties prop = new Properties();
	
		try {
			InputStream input  = getClass().getClassLoader().getResourceAsStream("config.properties");
			prop.load(input);
		} catch (FileNotFoundException e) {
			System.out.println("Failed to load Properties");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		String watch_folder_directory = prop.getProperty("watch_folder_directory");
		String target_directory = prop.getProperty("target_directory");
		String s = prop.getProperty("search_string");
		String max1 = prop.getProperty("max_files");
		int max = Integer.parseInt(max1);		


		//Code
		File[] files = new File(watch_folder_directory).listFiles(); 
		String batch1dir = target_directory + "Batch1";
		new File(batch1dir).mkdirs();
		batches.add(new Batch("Batch1",batch1dir));
		int batch_number = 0;


		File[] files1 = new File(target_directory).listFiles(); 
			for (File f1 : files1){
				String find1 = f1.getName();
				if (find1.contains("Batch")){
					batch_number++;
				}
			}


		for(File f : files){
			if(f.getName().toLowerCase().indexOf(s.toLowerCase()) != -1){
					
					String ss1 = String.valueOf(batch_number);
					String batch_name2 = "Batch" + ss1;
					String x4 = (target_directory+batch_name2);
					File direc = new File(x4);
					if (direc.list().length == max){
						//int fileSize = direc.list().length;
						batch_number+=1;
						String ss2 = String.valueOf(batch_number);
						String batch_name3 = "Batch" + ss2;
						String x5 = (target_directory+batch_name3);
						batch_name2 = batch_name3;
						new File(x5).mkdirs();
						batches.add(new Batch(batch_name3,x5));
					}
					//System.out.println(batch_number);
					//System.out.println(x4);
					//System.out.println(direc.list().length);
					
					boolean matches = false;

					String x1 = f.getName();
					String x2 = (watch_folder_directory + x1);
					File fileToCopy = new File(x2);
					FileInputStream inputStream;
					try {
						inputStream = new FileInputStream(fileToCopy);
					} catch (FileNotFoundException e) {
						System.out.println("Failed to find " + fileToCopy);
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
					FileChannel inChannel = inputStream.getChannel();

					String x3 = (target_directory+batch_name2+"/"+x1);

					File[] files10 = new File(target_directory+batch_name2).listFiles(); 
					for (File f4 : files10){
						String similar = f4.getName();
						if (similar.contains(x1)){
							matches = true;
						}
					}
					
					if (matches == false){
						File newFile = new File(x3);
						FileOutputStream outputStream;
						try {
							outputStream = new FileOutputStream(newFile);
						} catch (FileNotFoundException e) {
							System.out.println("Failed to find " + newFile);
							// TODO Auto-generated catch block
							e.printStackTrace();
							return false;
						}
						FileChannel outChannel = outputStream.getChannel();

						try {
							inChannel.transferTo(0, fileToCopy.length(), outChannel);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("Failed to copy file");
							e.printStackTrace();
							return false;
						}
						try {
							inputStream.close();
							outputStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("Failed to close input and output streams");
							e.printStackTrace();
						}
						
					}else{
						try {
							inputStream.close();
						} catch (IOException e) {
							System.out.println("Failed to close input stream");
							e.printStackTrace();
						}
					}
					//deletes original files
					fileToCopy.delete();
					matches = false;
			}
		}
		return true;
	}
	public static void main(String[] argv) throws IOException
    {
		DemoApplication app = new DemoApplication();
		if(app.CreateBatches()){
			//app.DemoGet();
			app.POSTRequest();
		}else{
			System.out.println("CreateBatches Failed");
		}
		
    }

}


