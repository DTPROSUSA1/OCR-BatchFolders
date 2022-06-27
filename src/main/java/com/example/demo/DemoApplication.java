package com.example.demo;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.util.FileSystemUtils;
import java.io.File;  



@SpringBootApplication
public class DemoApplication {

	public static void main(String[] argv) throws IOException
    {
		//load config.properties
		Properties prop = new Properties();
	
		prop.load(new FileInputStream("C:/Users/IshaanGupta/OneDrive - DTPROSUSA/Documents/demo/src/main/resources/config.properties"));
		String watch_folder_directory = prop.getProperty("watch_folder_directory");
		String target_directory = prop.getProperty("target_directory");
		String s = prop.getProperty("search_string");
		String max1 = prop.getProperty("max_files");
		int max = Integer.parseInt(max1);		


		//Code
		File[] files = new File(watch_folder_directory).listFiles(); 
		new File(target_directory + "Batch1").mkdirs();
		int count = 0;
		int batch_number = 1;
		boolean found = false;
		

		for(File f : files){
			if(f.getName().toLowerCase().indexOf(s.toLowerCase()) != -1){
		
					File[] files1 = new File(target_directory).listFiles(); 
					for (File f1 : files1){
						String find1 = f1.getName();
						if (find1.contains("Batch")){
							batch_number++;
							count++;
							found = true;
						}
					}

					//String temp = f.getName();
					//String temp1 = (watch_folder_directory + temp);
					//File tempFile = new File(temp1);
					//boolean exists = tempFile.exists();
					//System.out.println(exists);
					//File direc = new File(watch_folder_directory);
					//int fileSize = direc.list().length;
					//System.out.println(fileSize);

					if (count == max || found == true){
						batch_number++;
						count = 0;
						String bname = String.valueOf(batch_number);
						String batch_name1 = "Batch" + bname;
						new File(target_directory + batch_name1).mkdirs();
					}

					String x1 = f.getName();
					String x2 = (watch_folder_directory + x1);
					//System.out.println(x2);
					File fileToCopy = new File(x2);
					FileInputStream inputStream = new FileInputStream(fileToCopy);
					FileChannel inChannel = inputStream.getChannel();

					String ss = String.valueOf(batch_number);
					String batch_name = "Batch" + ss;
					String x3 = (target_directory+batch_name+"/"+x1);
					//System.out.println(x3);
					File newFile = new File(x3);
					FileOutputStream outputStream = new FileOutputStream(newFile);
					FileChannel outChannel = outputStream.getChannel();

					inChannel.transferTo(0, fileToCopy.length(), outChannel);
					inputStream.close();
					outputStream.close();
					count++;

					//deltes original files
					fileToCopy.delete();
			}
		}
    }

}


