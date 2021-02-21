package sample;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SQLHandler {
    private static Connection connection;
    private static Statement statement;

    // подключение драйвера для БД, установление соединения с БД
    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:src/requests.db");
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // отключение от БД
    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // добавление запроса пользователя в БД
    public static boolean addToDatabase(String cityName){
        try {
            statement.executeUpdate("INSERT INTO requests (cityName) VALUES ('"+cityName+"')");
            return true;
        } catch (SQLException e) {
            return false;
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // вспомогательный метод для поиска наиболее популярного города из запросов: получает из БД список всех городов
    public static ArrayList<String> getAllCitiesOfDatabase() {
        ArrayList<String> cities = new ArrayList<>();
        connect();
        try{
            ResultSet resultSet = statement.executeQuery("SELECT cityName FROM requests");
            while (resultSet.next()){
                cities.add(resultSet.getString("cityName"));
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        disconnect();
        return cities;
    }

    // возвращает наиболее популярный город из пользовательских запросов
    public static String showTopCityOfRequests(ArrayList<String> list){
        Map<String, Integer> cities = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            String tmpCity = list.get(i);
            if(!cities.containsKey(tmpCity)){
                cities.put(tmpCity, 1);
            }else{
                cities.put(tmpCity, cities.get(tmpCity) + 1);
            }
        }
        int max = 0;
        String topCity = "";
        for (Map.Entry<String, Integer> item : cities.entrySet()) {
            if(item.getValue() > max){
                max = item.getValue();
                topCity = item.getKey();
            }
        }
        return topCity;
    }

    // удаление пустых записей из БД
    public static void deleteAllEmptyNotes(){
        connect();
        try(PreparedStatement statement = connection.prepareStatement("DELETE FROM requests WHERE cityName = ?")){
            statement.setString(1, "null");
            statement.setString(1, "");
            statement.execute();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        disconnect();
    }
}
