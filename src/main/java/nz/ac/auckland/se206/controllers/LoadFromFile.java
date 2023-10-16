package nz.ac.auckland.se206.controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadFromFile {
  public static String[] loadFromFile(String filePath) {
    List<String> words = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] lineWords = line.split("\\s+");
        for (String word : lineWords) {
          words.add(word);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    String[] wordArray = new String[words.size()];
    for (int i = 0; i < words.size(); i++) {
      wordArray[i] = words.get(i);
    }
    return wordArray;
  }
}
