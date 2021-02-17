package com.organizer.contest.remix.web.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
@Component
public class MultipartFileHandleUtil {
    private static final String UPLOADS_DIR = "uploads/";

    public static boolean isMultipartFileEmpty(MultipartFile file){
        return file.isEmpty() || file.getOriginalFilename().length() == 0;
    }

    public static boolean isMultipartFileImage(MultipartFile file){
        return Pattern.matches("[^\\/*?<>:|\"]*\\.(jpg|png)", file.getOriginalFilename());
    }

    public static boolean isMultipartFileAudio(MultipartFile file){
        return Pattern.matches("[^\\/*?<>:|\"]*\\.(wav|mp3)", file.getOriginalFilename());
    }

    public boolean isMultipartFileImageInvalidAndLoadModel(MultipartFile file, Model model) {
        if (isMultipartFileEmpty(file)) {
            return false;
        }
        final boolean isInvalid = !isMultipartFileImage(file);
        if (isInvalid) {
            model.addAttribute("imageFileError", "Submit picture [.jpg, .png]");
        }
        return isInvalid;
    }

    public boolean isMultipartFileAudioInvalidAndLoadModel(MultipartFile file, Model model) {
        if (isMultipartFileEmpty(file)) {
            return false;
        }
        final boolean isInvalid = !isMultipartFileAudio(file);
        if (isInvalid) {
            model.addAttribute("audioFileError", "Submit audio file [.wav, .mp3]");
        }
        return isInvalid;
    }

    public static void handleMultipartFile(MultipartFile file, String dir) {
        String name = file.getOriginalFilename();
        long size = file.getSize();
        log.info("File: " + name + ", Size: " + size);
        try {
            File currentDir = new File(UPLOADS_DIR + dir);
            if(!currentDir.exists()) {
                currentDir.mkdirs();
            }
            String path = currentDir.getAbsolutePath() + "/" + file.getOriginalFilename();
//            path = new File(path).getAbsolutePath();
//            log.info(path);
            File f = new File(path);
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(f));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
