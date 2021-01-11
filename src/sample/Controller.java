package sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.json.JSONObject;

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

//    @FXML
//    void initialize() {
//    }

    @FXML
    public void checkWeather(){
        String getUserCity = city.getText().trim();
            String output = getUrlContent("http://api.openweathermap.org/data/2.5/weather?q=" + getUserCity + "&appid=someToken&units=metric");

            if(!output.isEmpty()){
                JSONObject obj = new JSONObject(output);
                tempInfo.setText("Температура: " + obj.getJSONObject("main").getDouble("temp"));
                tempFeel.setText("Ощущается как: " + obj.getJSONObject("main").getDouble("feels_like"));
                tempMax.setText("Максимум: " + obj.getJSONObject("main").getDouble("temp_max"));
                tempMin.setText("Минимум: " + obj.getJSONObject("main").getDouble("temp_min"));
                pressureInfo.setText("Давление: " + obj.getJSONObject("main").getDouble("pressure"));
                humidityInfo.setText("Влажность: " + obj.getJSONObject("main").getDouble("humidity"));
                windSpeed.setText("Скорость ветра: " + obj.getJSONObject("wind").getDouble("speed"));
            }
    }

    private static String getUrlContent(String urlAddress){
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
            //System.out.println("Упс! Такого города я не знаю :(");
            printAlert();
        }
        return content.toString();
    }

    private static void printAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Сообщение об ошибке");
        alert.setHeaderText("Город не найден");
        alert.setContentText("Возможно Вы некорректно ввели название города. Попробуйте снова.");

        alert.showAndWait();
    }

}
