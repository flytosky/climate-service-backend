package controllers;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.OutputFile;
import models.OutputFileRepository;
import models.Picture;
import models.PictureRepository;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

@Named
@Singleton
public class UtilController extends Controller {

	private final PictureRepository pictureRepository;
	private final OutputFileRepository outputTextRepository;
	private static boolean downloadTrigger = false;

	@Inject
	public UtilController(PictureRepository pictureRepository,
			OutputFileRepository outputTextRepository) {
		this.pictureRepository = pictureRepository;
		this.outputTextRepository = outputTextRepository;
	}

	public Result uploadPicture() {
		byte[] bytes = request().body().asRaw().asBytes();
		try {
			Blob image = new javax.sql.rowset.serial.SerialBlob(bytes);
			Picture picture = new Picture(image);
			picture.setDownload(false);
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
		System.out.println(downloadTrigger);
		if (downloadTrigger == false) {
			downloadTrigger = true;
			new Downloader(7200);
		}

		// Parse JSON file
		String url = json.findPath("url").asText();
		try {
			Picture picture = new Picture(url);
			picture.setDownload(false);
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
			if (picture.isDownload())
				imageNumber++;
		}
		for (Picture picture : picList) {
			if (!picture.isDownload()) {
				try {
					url = new URL(picture.getUrl());
					DataInputStream dataInputStream = new DataInputStream(
							url.openStream());
					String imageName = "picture/" + imageNumber + ".jpeg";
					FileOutputStream fileOutputStream = new FileOutputStream(
							new File(imageName));
					picture.setUrl(imageName);
					picture.setDownload(true);
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
		}
		return ok("Images are downloaded successfully");
	}

	public Result addOutputFile() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("OutputText not saved, expecting Json data");
			return badRequest("OutputText not saved, expecting Json data");
		}

		// Parse JSON file
		String url = json.findPath("url").asText();
		try {
			OutputFile outputFile = new OutputFile(url);
			outputFile.setDownload(false);
			outputTextRepository.save(outputFile);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("OutputText not saved");
			return badRequest("OutputText not saved");
		}

		return ok("OutputText is stored");
	}

	public Result downloadOutputFile() {
		List<OutputFile> fileList = outputTextRepository.findAll();
		URL url = null;
		int fileNumber = 0;

		for (OutputFile outputFile : fileList) {
			if (outputFile.isDownload())
				fileNumber++;
		}

		for (OutputFile outputFile : fileList) {
			if (!outputFile.isDownload()) {
				try {
					url = new URL(outputFile.getUrl());
					DataInputStream dataInputStream = new DataInputStream(
							url.openStream());
					String textName = "output/" + fileNumber + ".nc";
					FileOutputStream fileOutputStream = new FileOutputStream(
							new File(textName));
					outputFile.setUrl(textName);
					outputFile.setDownload(true);
					outputTextRepository.save(outputFile);

					byte[] buffer = new byte[1024];
					int length;

					while ((length = dataInputStream.read(buffer)) != -1) {
						fileOutputStream.write(buffer, 0, length);
					}

					dataInputStream.close();
					fileOutputStream.close();
					fileNumber++;
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return badRequest("OutputText download failed");
				} catch (IOException e) {
					e.printStackTrace();
					return badRequest("OutputText download failed");
				}
			}
		}
		return ok("OutputText are downloaded successfully");
	}

	public OutputFile getOutputText(long id) {
		return outputTextRepository.findOne(id);
	}
	
	private class Downloader{  
	    Timer timer;  
	    public Downloader(int seconds){  
	        timer = new Timer();  
	        timer.schedule(new DownloadTask(), 0, seconds*1000);  
	    }  
	    class DownloadTask extends TimerTask{  
	        public void run(){  
	            System.out.println("Downloader is running!"); 
	            downloadPicture();
	            downloadOutputFile(); 
	        }  
	    }  
	}
}
