package core.project.reports;

import com.mashape.unirest.http.exceptions.UnirestException;
import core.project.youtube.entities.channels.ChannelsResponse;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import core.project.Main;
import core.project.tools.DateUtils;

import java.io.IOException;


public class CompareGlobalChannelInfo extends GlobalChannelInfo {
    private VBox outVBox1;
    private VBox outVBox2;

    public CompareGlobalChannelInfo(VBox inputVBox, VBox outputVBox, Button execute, Button back) {
        super(inputVBox, outputVBox, execute, back);
    }

    // заполняем елементами Input box
    public void show(){
        HBox channelId1 = new HBox();
        channelId1.setSpacing(10);

        Label lblTitle = new Label("Compare Global Channel Info");
        lblTitle.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC,12));

        Label lblChannelId1 = new Label("Channel Id #1");
        channelId1.getChildren().add(lblChannelId1);

        TextField textChannelId1 = new TextField("UC_x5XG1OV2P6uZZ5FSM9Ttw");
        textChannelId1.setPrefWidth(250);
        channelId1.getChildren().add(textChannelId1);

        HBox channelId2 = new HBox();
        channelId2.setSpacing(10);

        Label lblChannelId2 = new Label("Channel Id #2");
        channelId2.getChildren().add(lblChannelId2);

        TextField textChannelId2 = new TextField("UC-kpTYxx7mgvcSL7g93JLDw");
        textChannelId2.setPrefWidth(250);
        channelId2.getChildren().add(textChannelId2);

        run = new Button("Run");
        run.setOnMouseClicked(event -> {
            // запускаєм запрос
            new Thread(()->{
                show2Results(textChannelId1,textChannelId2);
                //showResults(textChannelId1,outputVBox);
            }).start();
        });

        inputVBox.getChildren().addAll(lblTitle, channelId1, channelId2, run);
    }

    private void show2Results(TextField ChannelId1, TextField ChannelId2){
        Label gap1 = new Label();
        outVBox1 = new VBox();
        outVBox2 = new VBox();
        Label lblTitle = new Label("Compare Global Channel Info");
        lblTitle.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC,12));
        Line line = new Line(10,10,200,10);
        showResults(ChannelId1,outVBox1);
        showResults(ChannelId2,outVBox2);
        Platform.runLater(()-> {
            outputVBox.getChildren().addAll(gap1, lblTitle, outVBox1, line, outVBox2);
        });
    }

    // выводим результаты запроса в Output box
    private void showResults(TextField textField, VBox outVBox){
        long startTime = System.currentTimeMillis();

        Platform.runLater(()-> {
            execute.setDisable(true);
            back.setDisable(true);
            run.setDisable(true);
            clearOutputVBox();
        });
        ChannelsResponse channelsResponse = null;
        HTTPRequest httpRequest = new HTTPRequest();
        if (Main.settingsSet.getUseCache()){
            try {
                channelsResponse = httpRequest.getJSONResponse(textField.getText());
            } catch (UnirestException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                channelsResponse = httpRequest.getHTTPResponse(textField.getText());

            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }

        if (channelsResponse.items.size() > 0){
            final ChannelsResponse chResponse = channelsResponse;

            Label gap1 = new Label();

            Platform.runLater(()-> {
                Label lblChannelTitle = new Label("Channel Title:   " + chResponse.items.get(0).snippet.title);
                Label lblPublishedAt = new Label("Published At:     " + DateUtils.convertDateToString(chResponse.items.get(0).snippet.publishedAt));
                Label lblSubscriberCount = new Label("Subscribers Count:    " + chResponse.items.get(0).statistics.subscriberCount);
                Label lblVideoCount = new Label("Video Count:   " + chResponse.items.get(0).statistics.videoCount);
                Label lblViewCount = new Label("View Count:     " + chResponse.items.get(0).statistics.viewCount);
                outVBox.getChildren().addAll(gap1,
                        lblChannelTitle, lblPublishedAt,
                        lblSubscriberCount, lblVideoCount, lblViewCount);
            });
        }

        long duration = System.currentTimeMillis() - startTime;
        final long dr = duration;

        Platform.runLater(()-> {
            if (Main.settingsSet.getShowTime()){
                Label gap = new Label();
                Label lblExecutionTime = new Label("Execution time:     " + dr);
                outVBox.getChildren().addAll(gap,lblExecutionTime);
            }
            execute.setDisable(false);
            back.setDisable(false);
            run.setDisable(false);
        });
    }

}
