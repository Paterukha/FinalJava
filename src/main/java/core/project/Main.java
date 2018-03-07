package core.project;

import com.alibaba.fastjson.JSON;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import core.project.tools.FileUtils;
import core.project.tools.Key;
import core.project.tools.MyObjectMapper;
import core.project.tools.SettingsSet;
import core.project.screens.SettingsScreen;
import core.project.screens.YouTubeAnalyticsScreen;

import java.io.FileNotFoundException;
import java.io.IOException;



// Главный экран
public class Main extends Application {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 275;
    private GridPane grid = new GridPane();
    public static final String PATH_TO_SETTINGS = "settings/settings.ini";
    public static SettingsSet settingsSet;

    public static void main(String[] args) {
        String json = null;
        try {
            //Key.store();
            // загружаем ключ из файла
            Key.load();
            json = FileUtils.readFromFile(PATH_TO_SETTINGS);
            settingsSet = JSON.parseObject(json,SettingsSet.class);
        } catch (FileNotFoundException e){
            settingsSet = new SettingsSet();
            settingsSet.setUseCache(false);
            settingsSet.setShowTime(false);
            settingsSet.setPathToCache("");
            System.out.println("The system cannot find the file with settings: \"" + PATH_TO_SETTINGS + "\"");
            System.out.println("Once the Save button for settings is pressed it will be created automatically.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //на случай если нужно будет использовать парсер JSONа з Unirest asObject
        MyObjectMapper myObjectMapper = new MyObjectMapper();

        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //рисуем стартовое окно в GridPane
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, WIDTH, HEIGHT);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Main Screen");

        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label appName = new Label("YouTube Monitor");
        grid.add(appName, 0, 1);

        Label versionNumber = new Label("version # 1.9");
        grid.add(versionNumber, 1, 1);

        Button buttonYouTubeAnalytics = new Button("YouTube Analytics");
        buttonYouTubeAnalytics.setOnMouseClicked(event -> {
            // инициализация окна YouTubeAnalyticsScreen
            YouTubeAnalyticsScreen youTubeAnalyticsScreen = new YouTubeAnalyticsScreen();
            // запускаем новое окно в модальном виде
            youTubeAnalyticsScreen.show(event);
        });
        buttonYouTubeAnalytics.setDisable(Key.myKey == null);

        HBox hbBtn1 = new HBox(10);
        hbBtn1.setAlignment(Pos.CENTER);
        hbBtn1.getChildren().add(buttonYouTubeAnalytics);
        grid.add(hbBtn1, 0, 2);

        Button buttonSettings = new Button("SettingsScreen");
        buttonSettings.setOnMouseClicked(event -> {
            // инициализация окна SettingsScreen
            SettingsScreen settingsScreen = new SettingsScreen();
            // запускаем новое окно в модальном виде
            settingsScreen.show(event);
        });

        HBox hbBtn2 = new HBox(10);
        hbBtn2.setAlignment(Pos.CENTER);
        hbBtn2.getChildren().add(buttonSettings);
        grid.add(hbBtn2, 1, 2);

        primaryStage.show();

    }
}