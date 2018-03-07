package core.project.reports;

import com.mashape.unirest.http.exceptions.UnirestException;
import core.project.Main;
import core.project.youtube.entities.channels.ChannelsResponse;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import core.project.tools.DateUtils;

import java.io.IOException;

public class MediaResonance {
    protected VBox inputVBox;
    protected VBox outputVBox;
    protected Button execute;
    protected Button back;
    protected Button run;

    public MediaResonance(VBox inputVBox, VBox outputVBox, Button execute, Button back){
        this.inputVBox = inputVBox;
        this.outputVBox = outputVBox;
        this.execute = execute;
        this.back = back;
    }

    // заполняєм елементами Input box
    public void show(){
        HBox channelId = new HBox();
        channelId.setSpacing(10);

        Label lblTitle = new Label("Media Resonance Info");
        lblTitle.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC,12));

        Label lblChannelId = new Label("Channel Id");
        channelId.getChildren().add(lblChannelId);

        TextField textChannelId = new TextField("UC_x5XG1OV2P6uZZ5FSM9Ttw");
        textChannelId.setPrefWidth(250);
        channelId.getChildren().add(textChannelId);

        run = new Button("Run");
        run.setOnMouseClicked(event -> {
            // запускаем запрос
            new Thread(()->{
                showResults(textChannelId);
            }).start();
        });

        inputVBox.getChildren().addAll(lblTitle, channelId, run);
    }

    // выводим результаты запроса в Output box
    private void showResults(TextField textField){
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
            int countOFComments = 0;
            HTTPRequestChannelComments httpRequestChannelComments = new HTTPRequestChannelComments();
            if (Main.settingsSet.getUseCache()){
                try {
                    countOFComments = httpRequestChannelComments.startFromJSON(textField.getText());
                } catch (UnirestException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                countOFComments = httpRequestChannelComments.start(textField.getText());
            }

            final int countOfComments = countOFComments;

            Label gap1 = new Label();
            Label gap2 = new Label();

            Platform.runLater(()-> {
                Label lblTitle = new Label("Media Resonance Info");
                lblTitle.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC,12));
                Label lblChannelTitle = new Label("Channel Title:   " + chResponse.items.get(0).snippet.title);
                Label lblPublishedAt = new Label("Published At:     " + DateUtils.convertDateToString(chResponse.items.get(0).snippet.publishedAt));
                Label lblSubscriberCount = new Label("Subscribers Count:    " + chResponse.items.get(0).statistics.subscriberCount);
                Label lblVideoCount = new Label("Video Count:   " + chResponse.items.get(0).statistics.videoCount);
                Label lblViewCount = new Label("View Count:     " + chResponse.items.get(0).statistics.viewCount);
                Label lblCommentsCount = new Label("Comments Count:     " + countOfComments);
                outputVBox.getChildren().addAll(gap1, lblTitle, gap2,
                        lblChannelTitle, lblPublishedAt,
                        lblSubscriberCount, lblVideoCount, lblViewCount,lblCommentsCount);
            });
        }

        long duration = System.currentTimeMillis() - startTime;
        final long dr = duration;

        Platform.runLater(()-> {
            if (Main.settingsSet.getShowTime()){
                Label gap = new Label();
                Label lblExecutionTime = new Label("Execution time:     " + dr);
                outputVBox.getChildren().addAll(gap,lblExecutionTime);
            }
            execute.setDisable(false);
            back.setDisable(false);
            run.setDisable(false);
        });
    }


    // очищаем Output box
    protected void clearOutputVBox(){
        if (outputVBox != null){
            outputVBox.getChildren().clear();
            drawOutputTitle(outputVBox);
        }
    }

    // рисуем название Output box
    private void drawOutputTitle(VBox outputVBox){
        Text outputTitle = new Text("Output");
        outputTitle.setTranslateX(10);
        outputTitle.setTranslateY(10);
        outputTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        outputVBox.getChildren().add(outputTitle);
    }
}