package mangaDownloader;

import java.util.Scanner;

public class Main {

  public static void main(String[] args) {

    MangaDownloader downloader = new MangaDownloader();

    System.out.println("Введите ссылку на сайт с мангой");

    Scanner scanner = new Scanner(System.in);
    String url = scanner.nextLine();

    if (url.isEmpty()) {
      System.out.println("Еще раз");
    }

    try {
      downloader.downloadManga(url, "C:/Manga");
      System.out.println("Скачивание завершено успешно!");
    } catch (Exception e) {
      System.err.println("Ошибка при скачивании: " + e.getMessage());
    } finally {
      scanner.close();
    }
  }
}
