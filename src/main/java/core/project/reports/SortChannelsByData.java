package core.project.reports;

import com.mashape.unirest.http.exceptions.UnirestException;
import core.project.Main;
import core.project.youtube.entities.channels.Channel;
import core.project.youtube.entities.channels.ChannelsResponse;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.IOException;


public class SortChannelsByData {
    private VBox inputVBox;
    private VBox outputVBox;
    private Button run;
    private Boolean showCommentsCount;  // идентификатор показывать комменты или нет

    private static final String INPUT_CHANNEL_ARRAY =
                    "UC_x5XG1OV2P6uZZ5FSM9Ttw" + "\n"
                    + "UC-kpTYxx7mgvcSL7g93JLDw" + "\n"
                    + "UCufHx6C7e4t9Ib-IQP7PoeA" + "\n"
                    + "UCLM-4fcdyy2CQK-xURlvdjQ" + "\n"
                    + "UCWJ2lWNubArHWmf3FIHbfcQ" + "\n"
                    + "UCGRIgtqal_cC7V_cD-Zylkg" + "\n"
                    + "UCNTpErdUqTDqWq7YG1nasOQ" + "\n"
                    + "UCE_M8A5yxnLfW0KghEeajjw" + "\n"
                    + "UCpvg0uZH-oxmCagOWJo9p9g" + "\n"
                    + "UC2EU93iTrieTLeYdIO0uF7g" + "\n";

    public SortChannelsByData(VBox inputVBox, VBox outputVBox, Boolean showCommentsCount){
        this.inputVBox = inputVBox;
        this.outputVBox = outputVBox;
        this.showCommentsCount = showCommentsCount;
    }

    // заполняем елементами Input box
    public void show(){

        Label lblTitle = new Label(showCommentsCount?"Sort Channels By Media Resonance":"Sort Channels By Data");
        lblTitle.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 12));

        Label lblChannelId = new Label("Channel Ids");

        // окно, в которое вводим массив каналов
        TextArea txtChannelsArray = new TextArea();
        txtChannelsArray.setPrefWidth(200);
        txtChannelsArray.setText(INPUT_CHANNEL_ARRAY);

        run = new Button("Run");
        run.setOnMouseClicked(event -> {
            // запускаем запрос
            new Thread(()->{
                // выводим результат в Output box с помощью елемента TableView
                long startTime = System.currentTimeMillis();
                TableViewChannelInfo tableViewChannelInfo = new TableViewChannelInfo(clearNulls(getChannels(getChannelsResponses(txtChannelsArray.getText()))),
                                                                                        outputVBox,showCommentsCount);
                Platform.runLater(()-> {
                    clearOutputVBox();
                    tableViewChannelInfo.show(startTime);
                });
            }).start();
        });

        inputVBox.getChildren().addAll(lblTitle, lblChannelId, txtChannelsArray, run);
    }

    // получаем массив id каналов из окна ввода
    private String[] getChannelsArray(String string){
        String delims = "\n";
        String[] result = string.split(delims);
        return result;
    }

    // метод запуска получения response
    private ChannelsResponse[] getChannelsResponses(String string){
        String[] channelsArray = getChannelsArray(string);
        ChannelsResponse[] channelsResponses = new ChannelsResponse[channelsArray.length];
        HTTPRequest httpRequest = new HTTPRequest();
        HTTPRequestChannelComments httpRequestChannelComments = new HTTPRequestChannelComments();
        long commentsCount;
        for (int i = 0; i < channelsResponses.length; i++){
            if (Main.settingsSet.getUseCache()){
                try {
                    channelsResponses[i] = httpRequest.getJSONResponse(channelsArray[i]);
                    if (showCommentsCount && channelsResponses[i].items.size()>0){
                        commentsCount = httpRequestChannelComments.startFromJSON(channelsArray[i]);
                        channelsResponses[i].items.get(0).commentsCount = commentsCount;
                    }
                } catch (UnirestException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    channelsResponses[i] = httpRequest.getHTTPResponse(channelsArray[i]);
                    if (showCommentsCount && channelsResponses[i].items.size()>0){
                        commentsCount = httpRequestChannelComments.start(channelsArray[i]);
                        channelsResponses[i].items.get(0).commentsCount = commentsCount;
                    }
                } catch (UnirestException e) {
                    e.printStackTrace();
                }
            }
        }

        return channelsResponses;
    }

    // получаем массив каналов с даными
    private Channel[] getChannels(ChannelsResponse[] channelsResponses){
        Channel[] channels = new Channel[channelsResponses.length];
        for (int i = 0; i < channels.length; i++){
            channels[i] = (channelsResponses[i].items.size()>0?channelsResponses[i].items.get(0):null);
        }
        return channels;
    }

    // очистка массиву от null
    private Channel[] clearNulls(Channel[] channels){
        int j = 0;
        for (int i = 0; i < channels.length; i++){
            if (channels[i] == null){
                j++;
            }
        }
        Channel[] result = new Channel[channels.length - j];
        j = 0;
        for (int i = 0; i < channels.length; i++){
            if (channels[i] != null){
                result[j++] = channels[i];
            }
        }
        return result;
    }

    // очищаем Output box
    private void clearOutputVBox(){
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
