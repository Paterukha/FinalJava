package core.project.screens;

import com.alibaba.fastjson.JSON;
import core.project.Main;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import core.project.tools.FileUtils;
import core.project.tools.SettingsSet;

import java.io.IOException;



// Экран с настройками
public class SettingsScreen {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 200;
    private static final String DEFAULT_CACHE_FOLDER = "cache";

    private SettingsSet settingsSet;
    private FileUtils fileUtils = new FileUtils();

    public SettingsScreen(){
        this.settingsSet = Main.settingsSet;
    }


    public void show(Event eventLast){
        Stage stage = new Stage();
        GridPane grid = new GridPane();     //grid для удобства выравнивания, а можна и Pane root
        stage.setTitle("SettingsScreen");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        stage.setScene(new Scene(grid, WIDTH, HEIGHT));

        stage.initModality(Modality.WINDOW_MODAL);      //запускаем окно в модальном виде для того, чтобы стартовое окно было неактивным

        //указываем ниже владельца модального окна по параметру event (в нашому случае - это стартовое окно)
        stage.initOwner(
                ((Node)eventLast.getSource()).getScene().getWindow() );

        // заполняем элементами окно
        // создаем элементы, создаем контейнеры и запихиваем элементы в контейнеры

        Button back = new Button("Back");
        back.setOnMouseClicked(event -> {
            // закрываем активное окно
            ((Node)(event.getSource())).getScene().getWindow().hide();
        });

        CheckBox cbUseCache = new CheckBox("Use Cache");

        CheckBox cbShowTime = new CheckBox("Show Execution Time");

        Label lblPathToCache = new Label("Path to cache");

        TextField txtPathToCache = new TextField("");
        txtPathToCache.setPrefWidth(300);

        Button save = new Button("Save");
        save.setOnMouseClicked(event -> {
            saveSettings(cbUseCache,cbShowTime,txtPathToCache);
            // закрываем активное окно
            ((Node)(event.getSource())).getScene().getWindow().hide();
        });

        Text scenetitle = new Text("SettingsScreen");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 1, 1);

        // контейнер для чекбокса  Use Cache
        HBox hbox2 = new HBox(10);
        hbox2.setAlignment(Pos.CENTER_LEFT);
        hbox2.setPrefWidth(WIDTH);
        hbox2.setPrefHeight(HEIGHT/5);
        hbox2.getChildren().add(cbUseCache);
        grid.add(hbox2, 0, 1);


        // контейнер для  выбора пути к кэшу
        HBox hbox3 = new HBox(10);
        hbox3.setAlignment(Pos.CENTER_LEFT);
        hbox3.setPrefWidth(WIDTH);
        hbox3.setPrefHeight(HEIGHT/5);
        hbox3.getChildren().addAll(lblPathToCache,txtPathToCache);
        grid.add(hbox3, 0, 2,2,1);

        // контейнер для  чекбокса showTime
        HBox hbox4 = new HBox(10);
        hbox4.setAlignment(Pos.CENTER_LEFT);
        hbox4.setPrefWidth(WIDTH);
        hbox4.setPrefHeight(HEIGHT/5);
        hbox4.getChildren().add(cbShowTime);
        grid.add(hbox4, 0, 3);

        // контейнер для кнопки Back
        HBox hbox5 = new HBox(10);
        hbox5.setAlignment(Pos.CENTER_RIGHT);
        hbox5.setPrefWidth(WIDTH/2);
        hbox5.setPrefHeight(HEIGHT/5);
        hbox5.getChildren().add(back);
        grid.add(hbox5, 0, 4);

        // контейнер для кнопки Save
        HBox hbox6 = new HBox(10);
        hbox6.setAlignment(Pos.CENTER_LEFT);
        hbox6.setPrefWidth(WIDTH/2);
        hbox6.setPrefHeight(HEIGHT/5);
        hbox5.getChildren().add(save);
        grid.add(hbox6, 1, 4);

        readSettings(cbUseCache,cbShowTime,txtPathToCache);
        if ((settingsSet.getPathToCache().equals(null)) || (settingsSet.getPathToCache().equals(""))) {
            txtPathToCache.setText(fileUtils.getApplicationPath() + "\\" + DEFAULT_CACHE_FOLDER);
        }

        stage.show();
    }

    // сохраняем настройки
    private void saveSettings(CheckBox cbUseCache, CheckBox cbShowTime, TextField txtPathToCache){
        settingsSet.setUseCache(cbUseCache.isSelected()?true:false);
        settingsSet.setShowTime(cbShowTime.isSelected()?true:false);
        settingsSet.setPathToCache(txtPathToCache.getText());
        Main.settingsSet = settingsSet;

        String json = JSON.toJSONString(settingsSet);

        // если файл с настройками не существует, создаем его перед сохранением данных
        if (!fileUtils.fileExists(fileUtils.getApplicationPath() + "/" + Main.PATH_TO_SETTINGS)) {
            try {
                fileUtils.createFile(fileUtils.getApplicationPath() + "/" + Main.PATH_TO_SETTINGS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // записываем настройки в файл в JSON формате
        try {
            fileUtils.writeToFile(json, Main.PATH_TO_SETTINGS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // проверяем существует ли указанная папка с кэшем; если нет, то создаем
        if (!fileUtils.dirExists(settingsSet.getPathToCache())) {
            try {
                fileUtils.createDir(settingsSet.getPathToCache());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // выводим прочитанные из файла настройки в поля на екране
    private void readSettings(CheckBox cbUseCache, CheckBox cbShowTime, TextField txtPathToCache){
        cbUseCache.setSelected(settingsSet.getUseCache());
        cbShowTime.setSelected(settingsSet.getShowTime());
        txtPathToCache.setText(settingsSet.getPathToCache());
    }
}
