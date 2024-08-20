package mangaDownloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

public class MangaDownloader {

  private WebDriver driver;

  public void downloadManga(String url, String saveDirectory) throws Exception {

    System.setProperty("webdriver.chrome.driver",
        "C:\\WebDriver\\chromedriver-win64\\chromedriver.exe");
    driver = new ChromeDriver();
    driver.manage().window().maximize();
    Scanner scanner = new Scanner(System.in);

    try {
      driver.get(url);

      List<WebElement> chapterElements = driver.findElements(
          By.xpath("//div[@class='chapters']//a[contains(@class, 'chapter-link')]"));

      // Создаем коллекцию для хранения ссылок
      List<String> chapterLinks = new ArrayList<>();

      // Проходимся по всем найденным элементам и добавляем ссылки в коллекцию
      for (WebElement element : chapterElements) {
        String link = element.getAttribute("href"); // Получаем значение атрибута href
        chapterLinks.add(link); // Добавляем ссылку в коллекцию
      }

      Collections.reverse(chapterLinks);

      // Выводим количество собранных ссылок
      System.out.println("Количество найденных глав: " + chapterLinks.size());

      if (chapterLinks.isEmpty()) {
        throw new Exception("Не удалось найти главы манги.");
      }

      System.out.println("Сколько глав вы хотите скачать?");
      int chapterCount = scanner.nextInt();

      for (int chapterNum = 1; chapterNum <= chapterCount; chapterNum++) {
        String link = chapterLinks.get(chapterNum);
        driver.get(link);
        downloadChapter(saveDirectory, chapterNum);
      }
    } finally {
      driver.quit();
    }
  }

  private List<String> saveImgLinks() throws Exception {

    Actions builder = new Actions(driver);
    Action keyRightPressed = builder.sendKeys(Keys.ARROW_RIGHT).build();

    WebElement pageCount = driver.findElement(By.xpath("//span[@class='pages-count']"));
    pageCount.getText();
    int countText = Integer.parseInt(pageCount.getText());

    List<String> imageLinks = new ArrayList<>();

    for (int i = 1; i <= countText; i++) {
      List<WebElement> imgLink = driver.findElements(
          By.xpath("//img[contains(@class, 'manga-img_')]"));
      for (WebElement element : imgLink) {
        String link = element.getAttribute("src"); // Получаем значение атрибута href
        imageLinks.add(link); // Добавляем ссылку в коллекцию
      }
      keyRightPressed.perform();
      Thread.sleep(500);
    }

    return imageLinks;
  }

  private void downloadChapter(String saveDirectory, int chapterNum) throws Exception {

    List<String> imgLinks = saveImgLinks();

    File directory = new File(saveDirectory);
    if (!directory.exists()) {
      directory.mkdirs();
    }

    // Перебираем все ссылки на изображения и скачиваем каждую
    for (int i = 0; i < imgLinks.size(); i++) {
      String imageUrl = imgLinks.get(i);
      try {
        // Определяем имя файла для сохранения изображения
        String fileName =
            chapterNum + "_" + i + imageUrl.substring(imageUrl.lastIndexOf("=") + 10) + ".jpg";
        File outputFile = new File(directory, fileName);

        // Скачиваем изображение и сохраняем его в указанной директории
        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream()) {
          Files.copy(in, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
          System.out.println("Скачано: " + outputFile.getAbsolutePath());
        }
      } catch (IOException e) {
        System.err.println("Ошибка при скачивании изображения: " + imageUrl);
        e.printStackTrace();
      }
    }
  }
}
