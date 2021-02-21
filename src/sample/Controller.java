package sample;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow;
import org.json.JSONObject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import static sample.SQLHandler.getAllCitiesOfDatabase;
import static sample.SQLHandler.showTopCityOfRequests;

public class Controller {

    @FXML
    private TextField city;

    @FXML
    private Text tempInfo;

    @FXML
    private Text tempFeel;

    @FXML
    private Text tempMax;

    @FXML
    private Text tempMin;

    @FXML
    private Text pressureInfo;

    @FXML
    private Text humidityInfo;

    @FXML
    private Text windSpeed;

    @FXML
    private Text cloudsInfo;

    @FXML
    private TextArea topCity;

    @FXML
    private TextField dataDay;

    @FXML
    private TextField dataMonth;

    @FXML
    private TextField dataYear;

//    @FXML
//    void initialize() {
//    }

    @FXML
    public void checkWeather() {

        // получение данных из полей, заполненных пользователем
        String getUserCity = city.getText().trim();
        String day = dataDay.getText().trim();
        String month = dataMonth.getText().trim();
        String year = dataYear.getText().trim();

        // подключение к БД, добавление запроса в БД и метод удаление пустых полей в БД
        SQLHandler.connect();
        SQLHandler.addToDatabase(getUserCity);
        SQLHandler.deleteAllEmptyNotes();

        // получение ответа с метеоданными
        String output = getUrlContent("http://api.openweathermap.org/data/2.5/weather?q="
                                            + getUserCity + "&appid=ccb82a3c7dd962895a039007f0b8c827&units=metric");

        DateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();

        // логика обработки полученного ответа в формате JSON и вывод данных в GUI -
        // если пользователь ввел корректные данные для текущего дня - он получает ответ с реальными метеосведениями,
        // если данные некорректные - пользователю выходит окно ошики с пояснениями, иначе пользователю выдаются
        // псевдослучайные метеоданные

        if(!output.isEmpty()){
            JSONObject obj = new JSONObject(output);
            if((day.isEmpty() && month.isEmpty() && year.isEmpty()) || (currentDate.format(date).equals(day+"-"+month+"-"+year))){
                tempInfo.setText("Температура: " + obj.getJSONObject("main").getDouble("temp"));
                tempFeel.setText("Ощущается как: " + obj.getJSONObject("main").getDouble("feels_like"));
                tempMax.setText("Максимум: " + obj.getJSONObject("main").getDouble("temp_max"));
                tempMin.setText("Минимум: " + obj.getJSONObject("main").getDouble("temp_min"));
                pressureInfo.setText("Давление: " + obj.getJSONObject("main").getDouble("pressure"));
                humidityInfo.setText("Влажность: " + obj.getJSONObject("main").getDouble("humidity"));

            }else if(day.isEmpty() || month.isEmpty() || year.isEmpty() || (!checkCorrectInputData(day, month, year))){
                printAlert("Ошибка ввода даты", "Проверьте корректность введенной даты.");
            }else{
                Random random = new Random();
                int randomIndex = random.nextInt(3);
                tempInfo.setText("Температура: " + (obj.getJSONObject("main").getDouble("temp") + randomIndex));
                tempFeel.setText("Ощущается как: " + (obj.getJSONObject("main").getDouble("feels_like") + randomIndex));
                tempMax.setText("Максимум: " + (obj.getJSONObject("main").getDouble("temp_max") + randomIndex));
                tempMin.setText("Минимум: " + (obj.getJSONObject("main").getDouble("temp_min") + randomIndex));
                pressureInfo.setText("Давление: " + (obj.getJSONObject("main").getDouble("pressure") + randomIndex));
                humidityInfo.setText("Влажность: " + (obj.getJSONObject("main").getDouble("humidity") + randomIndex));
            }
            windSpeed.setText("Скорость ветра: " + obj.getJSONObject("wind").getDouble("speed"));
            int cloudValue = obj.getJSONObject("clouds").getInt("all");
            String clouds = "";
            if (cloudValue > 0 && cloudValue <= 25){
                clouds = "малооблачно";
            }else if(cloudValue > 25 && cloudValue <= 50){
                clouds = "переменная";
            }else if(cloudValue > 50 && cloudValue <= 85){
                clouds = "облачно, возможны осадки";
            }else {
                clouds = "пасмурно, осадки";
            }
            cloudsInfo.setText("Облачность: " + clouds);
        }
    }

    // метод для формирования строки запроса
    private String getUrlContent(String urlAddress){
        StringBuilder content = new StringBuilder();
        try{
            URL url = new URL(urlAddress);
            URLConnection urlConn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String line;
            while((line = reader.readLine()) != null){
                content.append(line + "\n");
            }
            reader.close();
        }catch (Exception ex){
            printAlert("Ошибка ввода города", "Возможно Вы некорректно ввели название города. Попробуйте снова.");
        }
        return content.toString();
    }

    // метод вывода окна ошибки с пояснениями о причинах её возникновения
    private static void printAlert(String title, String messageToUser){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(messageToUser);
        alert.showAndWait();
    }

    // метод вывода наиболее популярного города из запросов
    @FXML
    public void checkTopCity() {
        topCity.clear();
        topCity.setText(showTopCityOfRequests(getAllCitiesOfDatabase()));
    }

    // проверки на корректность даты, введенной пользователем
    public static boolean checkCorrectInputData(String day, String month, String year){
        int currentDay = 0;
        int currentMonth = 0;
        int checkYear = 0;
        try {
            currentDay = (int) Integer.parseInt(day);
            currentMonth = (int) Integer.parseInt(month);
            checkYear = (int) Integer.parseInt(year);
        }catch(Exception ex){
            printAlert("Ошибка ввода даты", "Проверьте корректность ввода года в дате");
        }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if(checkYear < currentYear || checkYear - currentYear != 0){
            return false;
        }
        if(isYearLeap(currentMonth)){
            if(currentMonth == 2 && currentDay > 29){
                return false;
            }
        }
        if(currentMonth < 1 || currentMonth > 12){
            return false;
        }
        checkHowMuchDaysInMonth(currentDay, currentMonth);

        return true;
    }

    // проверка года на високосность
    private static boolean isYearLeap(int year) {
        if (year % 100 == 0 && year % 400 == 0) {
            return true;
        } else if (year % 4 == 0 && year % 100 > 0){
            return true;
        } else if (year % 100 == 0) {
            return false;
        } else
            return false;
    }

    // проверка корректности введенного числа в зависимости от месяца
    private static boolean checkHowMuchDaysInMonth(int day, int month){
        if(day < 1 || day > 31){
            return false;
        }
        if(month == 4 || month == 6 || month == 9 || month == 11){
            if(day > 30){
                return false;
            }
        }else{
            if(day > 31){
                return false;
            }
        }
        return true;
    }
}