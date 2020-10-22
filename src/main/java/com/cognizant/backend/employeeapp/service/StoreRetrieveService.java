package com.cognizant.backend.employeeapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StoreRetrieveService {

	private final Path root = Paths.get("uploads");

	public void init() {
		try {
			Files.createDirectory(root);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}

	public List<String> readAndValidate(MultipartFile file) {
		Path savedFilePath;
		ArrayList<String> errorMsgs = new ArrayList<String>();

		try {
            //Check whether the file is empty
			if (file.getSize() == 0) {
				errorMsgs.add("File is empty");
				return errorMsgs;
			}

			Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
			savedFilePath = this.root.resolve(file.getOriginalFilename());

			BufferedReader br = Files.newBufferedReader(savedFilePath);
			String line;
			while ((line = br.readLine()) != null) {

				System.out.println(line);
				String[] columns = line.split(",");
				//Check if there is too many or too few columns
				if (columns.length > 4 || columns.length < 4) {
					errorMsgs.add("Please make sure there are only 4 columns per row");
					return errorMsgs;
				}
				//Check if salary is incorrectly formatted
				try {
					Float.parseFloat(columns[3]);
				} catch (NumberFormatException e) {
					errorMsgs.add("Unable to parse salary, make sure it is formatted correctly");
					return errorMsgs;
				}
				//Check if salary is negative or not
				if(Float.parseFloat(columns[3]) < 0) {
					errorMsgs.add("An entered salary has been detected to be negative");
					return errorMsgs;
				}
				
			}

		} catch (Exception e) {
			// throw new RuntimeException("Could not store the file. Error: " +
			// e.getMessage());
			e.printStackTrace();
		}

		return errorMsgs;
	}

}
