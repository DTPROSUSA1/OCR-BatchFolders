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
					}
					//System.out.println(batch_number);
					//System.out.println(x4);
					//System.out.println(direc.list().length);
					

					String x1 = f.getName();
					String x2 = (watch_folder_directory + x1);
					File fileToCopy = new File(x2);
					FileInputStream inputStream = new FileInputStream(fileToCopy);
					FileChannel inChannel = inputStream.getChannel();

					String x3 = (target_directory+batch_name2+"/"+x1);
					File newFile = new File(x3);
					FileOutputStream outputStream = new FileOutputStream(newFile);
					FileChannel outChannel = outputStream.getChannel();

					inChannel.transferTo(0, fileToCopy.length(), outChannel);
					inputStream.close();
					outputStream.close();
					

					//deletes original files
					fileToCopy.delete();
			}
		}
    }

}


