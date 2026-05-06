package com.furnistyle.storage;

import java.io.*;

public class FileStorage {
    public void save(ApplicationData data, File file) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(data);
        }
    }

    public ApplicationData load(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            return (ApplicationData) inputStream.readObject();
        }
    }
}
