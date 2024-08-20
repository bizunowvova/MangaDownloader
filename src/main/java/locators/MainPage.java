package locators;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class MainPage implements MainPageLocators {

  private final SelenideElement allChapterButton = $(ALL_CHAPTER_BUTTON);
}
