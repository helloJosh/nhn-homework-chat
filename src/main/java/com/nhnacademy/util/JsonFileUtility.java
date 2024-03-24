package com.nhnacademy.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.form.ConfigurationForm;

public class JsonFileUtility {
    private static final String filePath = "./configuration.json";

    private JsonFileUtility() {
    }

    public static ConfigurationForm jsonFileLoad(){
        ObjectMapper objectMapper = new ObjectMapper();
        try(RandomAccessFile file = new RandomAccessFile(filePath,"r")){

            return readFromFile(file, objectMapper);
        } catch (IOException e){
            System.err.println(e.getMessage());
        }

        return new ConfigurationForm();
    }

    public static void jsonFileSave(ConfigurationForm configurationForm) {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "rw");
             FileChannel channel = file.getChannel()) {
            FileLock lock = channel.lock();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ConfigurationForm configurationloadForm = readFromFile(file, objectMapper);
                System.out.println(configurationloadForm);

                // 추가된 데이터를 기존 데이터에 병합
                mergeConfigurationForms(configurationloadForm, configurationForm);
                System.out.println(configurationloadForm);
                // 병합된 데이터를 파일에 저장
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
            // 파일이 비어있을 경우 빈 객체를 반환
            configurationloadForm = new ConfigurationForm();
        } else {
            // 파일이 비어있지 않을 경우 파일에서 JSON 데이터 읽기
            configurationloadForm = objectMapper.readValue(file, ConfigurationForm.class);
            file.setLength(0);
        }
        return configurationloadForm;
    } 
    
    public static <T> List<T> deepCopy(List<T> original) {
        List<T> copiedList = new ArrayList<>(original.size());
        for (T item : original) {
            if (item instanceof List) {
                // 리스트의 요소가 리스트인 경우 재귀적으로 깊은 복사 수행
                copiedList.add((T) deepCopy((List<?>) item));
            } else {
                // 리스트의 요소가 리스트가 아닌 경우 복사하여 새로운 리스트에 추가
                copiedList.add(item);
            }
        }
        return copiedList;
    }

    // 기존 데이터와 새로운 데이터를 병합
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
