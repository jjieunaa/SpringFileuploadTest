package kr.kwangan2.fileupload.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.kwangan2.fileupload.domain.AttachFileDTO;
import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnailator;

@Controller
@Log4j
public class UploadController {
	
	@GetMapping("/uploadAjax")
	public void uploadAjax() {
		
	}
	
	@PostMapping(value = "/uploadAjaxAction", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<List<AttachFileDTO>> uploadAjaxPost(MultipartFile[] uploadFile) {
		List<AttachFileDTO> list = new ArrayList<AttachFileDTO>();
		
		String uploadFolder = "C:/upload";
		
		String uploadFolderPath = getFolder();
		
		File uploadPath = new File(uploadFolder, uploadFolderPath);
		
		if (uploadPath.exists() == false) {
			uploadPath.mkdirs();
		}
		
		for (MultipartFile multipartFile : uploadFile) {
			log.info("-----------------------------------------------------------------------------");
			
			AttachFileDTO attachDTO = new AttachFileDTO();
			
			String originalFilename = multipartFile.getOriginalFilename();
			attachDTO.setFileName(originalFilename);
			
			log.info("upload file name: " + originalFilename);
			log.info("upload file size: " + multipartFile.getSize());
			log.info("isEmpty: " + multipartFile.isEmpty());
			log.info("getName: " + multipartFile.getName());
			
			try {
				byte[] bytes = multipartFile.getBytes();
				int bytesLength = bytes.length;
				String str = "";
				
				for (int i=0; i<bytesLength; i++) {
					str += Integer.toBinaryString(bytes[i]);
				}
				
				log.info("getBytes: " + str);
				log.info("getInputStream: " + multipartFile.getInputStream());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			log.info("-----------------------------------------------------------------------------");
			
			originalFilename.substring(originalFilename.lastIndexOf("/") + 1);
			
			UUID uuid = UUID.randomUUID();
			originalFilename = uuid.toString() + "_" + originalFilename;
			
			try {
				File saveFile = new File(uploadPath, originalFilename);
				
				multipartFile.transferTo(saveFile);
				
				attachDTO.setUuid(uuid.toString());
				attachDTO.setUploadPath(uploadFolderPath);
				
				if (checkImageType(saveFile)) {
					
					attachDTO.setImage(true);
					
					FileOutputStream thumbnail = new FileOutputStream(new File(uploadPath, "thumb_" + originalFilename));
					
					Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnail, 100, 100);
					thumbnail.close();
				}
				list.add(attachDTO);
			} catch (Exception ex) {
				ex.printStackTrace();
			}	// try-catch
		}	// for
		return new ResponseEntity(list, HttpStatus.OK);
	}	// uploadAjaxPost
	
	private String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String str = sdf.format(date);
		
		return str.replace("-", File.separator);
	}
	
	private boolean checkImageType(File file) {
		try {
			String contentType = Files.probeContentType(file.toPath());
			return contentType.startsWith("image");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return false;
	}

}	// class
