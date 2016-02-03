package controllers;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.OutputText;
import models.OutputTextRepository;
import models.Picture;
import models.PictureRepository;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

@Named
@Singleton
public class UtilController extends Controller {

	private final PictureRepository pictureRepository;
	private final OutputTextRepository outputTextRepository;

	@Inject
	public UtilController(PictureRepository pictureRepository, OutputTextRepository outputTextRepository) {
		this.pictureRepository = pictureRepository;
		this.outputTextRepository = outputTextRepository;
	}

	public Result uploadPicture() {
		byte[] bytes = request().body().asRaw().asBytes();
		try {
			Blob image = new javax.sql.rowset.serial.SerialBlob(bytes);
			Picture picture = new Picture(image);
			pictureRepository.save(picture);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Picture not saved");
			return badRequest("Picture not saved");
		}

		return ok("Image is stored");
	}

	public Picture getPicture(long id) {
		return pictureRepository.findOne(id);
	}

	public Result addPicture() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("Picture not saved, expecting Json data");
			return badRequest("Picture not saved, expecting Json data");
		}

		// Parse JSON file
		String url = json.findPath("url").asText();
		try {
			Picture picture = new Picture(url);
			pictureRepository.save(picture);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Picture not saved");
			return badRequest("Picture not saved");
		}

		return ok("Image is stored");
	}

	public Result downloadPicture() {
		List<Picture> picList = pictureRepository.findAll();
		URL url = null;
		int imageNumber = 0;

		for (Picture picture : picList) {
			 try {
			 url = new URL(picture.getUrl());
			 DataInputStream dataInputStream = new
			 DataInputStream(url.openStream());
			 String imageName = "picture/" + imageNumber + ".jpeg";
			 FileOutputStream fileOutputStream = new FileOutputStream(new
			 File(imageName));
			 picture.setUrl(imageName);
			 pictureRepository.save(picture);
			
			 byte[] buffer = new byte[1024];
			 int length;
			
			 while ((length = dataInputStream.read(buffer)) != -1) {
			 fileOutputStream.write(buffer, 0, length);
			 }
			
			 dataInputStream.close();
			 fileOutputStream.close();
			 imageNumber++;
			 } catch (MalformedURLException e) {
			 e.printStackTrace();
			 return badRequest("Picture download failed");
			 } catch (IOException e) {
			 e.printStackTrace();
			 return badRequest("Picture download failed");
			 }
		}
		return ok("Images are downloaded successfully");
	}
	
	public Result addOutputText() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("OutputText not saved, expecting Json data");
			return badRequest("OutputText not saved, expecting Json data");
		}

		// Parse JSON file
		String url = json.findPath("url").asText();
		try {
			OutputText outputText = new OutputText(url);
			outputTextRepository.save(outputText);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("OutputText not saved");
			return badRequest("OutputText not saved");
		}

		return ok("OutputText is stored");
	}

	public Result downloadOutputText() {
		List<OutputText> textList = outputTextRepository.findAll();
		URL url = null;
		int textNumber = 0;

		for (OutputText outputText : textList) {
			 try {
			 url = new URL(outputText.getUrl());
			 DataInputStream dataInputStream = new
			 DataInputStream(url.openStream());
			 String textName = "text/" + textNumber + ".nc";
			 FileOutputStream fileOutputStream = new FileOutputStream(new
			 File(textName));
			 outputText.setUrl(textName);
			 outputTextRepository.save(outputText);
			
			 byte[] buffer = new byte[1024];
			 int length;
			
			 while ((length = dataInputStream.read(buffer)) != -1) {
			 fileOutputStream.write(buffer, 0, length);
			 }
			
			 dataInputStream.close();
			 fileOutputStream.close();
			 textNumber++;
			 } catch (MalformedURLException e) {
			 e.printStackTrace();
			 return badRequest("OutputText download failed");
			 } catch (IOException e) {
			 e.printStackTrace();
			 return badRequest("OutputText download failed");
			 }
		}
		return ok("OutputText are downloaded successfully");
	}

	public OutputText getOutputText(long id) {
		return outputTextRepository.findOne(id);
	}
}
