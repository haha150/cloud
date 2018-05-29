/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.hops.main;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import se.kth.hops.HopsHelper;
import java.util.Optional;
import java.lang.String;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class Main {

  public static void main(String[] args) {
    String hopsIp = "10.132.0.2";
    long fileSize = 0;
    long position =0;
    Optional<byte[]> readResult;
    String hopsPort = "8020";
    String user = "ubuntu";
    Configuration hdfsConfig = new Configuration();
    String hopsURL = "hdfs://" + hopsIp + ":" + hopsPort;
    hdfsConfig.set("fs.defaultFS", hopsURL);
    UserGroupInformation ugi = UserGroupInformation.createRemoteUser(user);
    String filePath = "/dark/darkfile";
    Path path = Paths.get("/home/ubuntu/1GB_file");
    int process = 1;			//changing process value to create different lar files (upload or download)

    if (process == 0){			//UPLOAD (append)
	try{

		long startTime = System.nanoTime();
		HopsHelper.simpleCreate(ugi, hdfsConfig, filePath);
		byte[] data = Files.readAllBytes(path);
		HopsHelper.append(ugi, hdfsConfig, filePath, data);
		long stopTime = System.nanoTime();
		long result = TimeUnit.SECONDS.convert(stopTime - startTime, TimeUnit.NANOSECONDS);
		System.out.println("File took: " + result + " seconds to upload.");

	} catch (Exception e){
		System.out.println("Exception occurred (append)");
	}
    }
    
    else if(process == 1){		//UPLOAD (append + flush)
    	try{
		long startTime = System.nanoTime();
		HopsHelper.simpleCreate(ugi, hdfsConfig, filePath);
		byte[] data = Files.readAllBytes(path);
		HopsHelper.append(ugi, hdfsConfig, filePath, data);
		HopsHelper.flush(ugi, hdfsConfig, filePath);
		long stopTime = System.nanoTime();
		long result = TimeUnit.SECONDS.convert(stopTime - startTime, TimeUnit.NANOSECONDS);
		System.out.println("File took: " + result + " seconds to upload.");

	} catch (Exception e){
		System.out.println("Exception occurred (append + flush)");
	}
    }

    else if(process == 2){		//DOWNLOAD
	try{

		long startTime = System.nanoTime();
		fileSize = HopsHelper.length(ugi, hdfsConfig, filePath);
		readResult = HopsHelper.read(ugi, hdfsConfig, filePath, position, (int)fileSize);
		try (FileOutputStream fos = new FileOutputStream("/home/ubuntu/downloadedFile_1GB")) {
			fos.write(readResult.get());
		} catch (Exception e){
			System.out.println("Exception occurred (read 02)");
		}

		long stopTime = System.nanoTime();
		long result = TimeUnit.SECONDS.convert(stopTime - startTime, TimeUnit.NANOSECONDS);
		System.out.println("File took: " + result + " seconds to download.");

	} catch (Exception e){
		System.out.println("Exception occurred (read 01)");
	}
    }

    else{			
   	 System.out.println("Processing problem!!!");
    }
    
  }
}



