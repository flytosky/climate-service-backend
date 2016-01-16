package controllers;

import javax.inject.Named;
import javax.inject.Singleton;

import models.*;
import play.mvc.*;

import javax.inject.Inject;

import java.sql.*;

@Named
@Singleton
public class UtilController extends Controller {
	
	private final PictureRepository pictureRepository;
	
	@Inject
	public UtilController(PictureRepository pictureRepository) {
		this.pictureRepository = pictureRepository;
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
			return badRequest("Climate Service not saved");
        }
		
		return ok("Image is stored");
    }
	
    public Picture getPicture(long id) {
        return pictureRepository.findOne(id);
    }
    
}
