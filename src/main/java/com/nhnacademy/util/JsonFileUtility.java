package com.nhnacademy.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.form.ConfigurationForm;

public class JsonFileUtility {
    private static final String FILE_PATH = "./configuration.json";

    private JsonFileUtility() {
    }

    public static ConfigurationForm jsonFileLoad(){
        ObjectMapper objectMapper = new ObjectMapper();
        try(RandomAccessFile file = new RandomAccessFile(FILE_PATH,"r")){

            return readFromFile(file, objectMapper);
        } catch (IOException e){
            System.err.println(e.getMessage());
        }

        return new ConfigurationForm();
    }

    public static void jsonFileSave(ConfigurationForm configurationForm) {
        try (RandomAccessFile file = new RandomAccessFile(FILE_PATH, "rw");
             FileChannel channel = file.getChannel()) {
            FileLock lock = channel.lock();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ConfigurationForm configurationloadForm = readFromFile(file, objectMapper);
                System.out.println(configurationloadForm);

                mergeConfigurationForms(configurationloadForm, configurationForm);
                System.out.println(configurationloadForm);
                
                objectMapper.writeValue(file, configurationloadForm);
            } finally {
                lock.release();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    // 파일에서 JSON 데이터 읽기
    private static ConfigurationForm readFromFile(RandomAccessFile file, ObjectMapper objectMapper) throws IOException {
        ConfigurationForm configurationloadForm;
        if (file.length() == 0) {
            configurationloadForm = new ConfigurationForm();
        } else {
            configurationloadForm = objectMapper.readValue(file, ConfigurationForm.class);
            file.setLength(0);
        }
        return configurationloadForm;
    } 
    
    public static <T> List<T> deepCopy(List<T> original) {
        List<T> copiedList = new ArrayList<>(original.size());
        for (T item : original) {
            if (item instanceof List) {
                copiedList.add((T) deepCopy((List<?>) item));
            } else {
                copiedList.add(item);
            }
        }
        return copiedList;
    }

    private static void mergeConfigurationForms(ConfigurationForm existingForm, ConfigurationForm newForm) {
        System.out.println(existingForm);
        System.out.println(newForm);
        existingForm.getClientIdList().addAll(newForm.getClientIdList());
        existingForm.getDenyClientIdList().addAll(newForm.getDenyClientIdList());
        existingForm.getConnect().addAll(newForm.getConnect());
        existingForm.getDisconnect().addAll(newForm.getDisconnect());
        System.out.println(existingForm);
    }
}
