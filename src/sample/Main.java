package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * Данное приложение создано в рамках ЛР-1,2 по курсу ОРПО в ТУСУР/2021.
     *
     * Приложение показывает погоду "за окном" по введенному пользователем названию города, используя бесплатный API openweathermap.org.
     * При ввода кокретной даты и названия города, ввиду отсутствия соответствующего бесплатного функционала openweathermap.org, приложение
     * выдает псевдослучайные погодные данные. Так же приложение сохраняет все запросы пользователя и по его запросу показывает наиболее
     * популярный город из запросов.
     */

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample2.fxml"));
        primaryStage.setTitle("Weather Forecast");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}