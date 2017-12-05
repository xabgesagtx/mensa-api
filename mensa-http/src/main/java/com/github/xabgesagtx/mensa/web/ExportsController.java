package com.github.xabgesagtx.mensa.web;

import com.github.xabgesagtx.mensa.common.MensaConstants;
import com.github.xabgesagtx.mensa.exports.ExportsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("exports")
@Slf4j
public class ExportsController {

	@Autowired
	private ExportsManager exportsManager;

	@RequestMapping("")
	public ModelAndView all(ModelAndView modelAndView) {
		modelAndView.setViewName("exports");
		modelAndView.addObject("exports", exportsManager.getExports());
		return modelAndView;
	}

	@RequestMapping("{dateTime}")
	public ResponseEntity<InputStreamResource> getExport(@PathVariable("dateTime") @DateTimeFormat(pattern = MensaConstants.EXPORT_DATE_FORMAT) LocalDateTime dateTime) {
		Optional<File> exportFile = exportsManager.getExportAsFile(dateTime);
		return exportFile.map(this::toInputStreamResource).orElseGet(() -> ResponseEntity.notFound().build());
	}

	private ResponseEntity<InputStreamResource> toInputStreamResource(File file) {
		InputStreamResource resource = null;
		try {
			FileInputStream stream = new FileInputStream(file);
			resource = new InputStreamResource(stream);
		} catch (FileNotFoundException e) {
			log.error("Failed to get input stream for {}", file.getAbsolutePath());
		}
		if (resource == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok()
					.contentLength(file.length())
					.contentType(MediaType.parseMediaType("application/zip"))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
					.body(resource);
		}
	}

}
