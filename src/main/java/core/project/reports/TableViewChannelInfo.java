package core.project.reports;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import core.project.Main;
import core.project.youtube.entities.channels.Channel;

import java.util.Date;

public class TableViewChannelInfo {
    private Channel[] channels;
    private VBox outputVBox;
    private Boolean showCommentsCount;  // идентификатор показывать комменты или нет

    public TableViewChannelInfo(Channel[] channels, VBox outputVBox, Boolean showCommentsCount){
        this.channels = channels;
        this.outputVBox = outputVBox;
        this.showCommentsCount = showCommentsCount;
    }

    public void show(long startTime){

        TableView<ChannelSimple> table = new TableView<ChannelSimple>();

        // Create column id (Data type of String).
        TableColumn<ChannelSimple, String> idCol //
                = new TableColumn<ChannelSimple, String>("Channel Id");

        // Create column title (Data type of String).
        TableColumn<ChannelSimple, String> titleCol //
                = new TableColumn<ChannelSimple, String>("Channel Title");

        // Create column publishedAt (Data type of Date).
        TableColumn<ChannelSimple, Date> publishedAtCol//
                = new TableColumn<ChannelSimple, Date>("Published At");

        // Create column subscriberCount (Data type of Long).
        TableColumn<ChannelSimple, Long> subscriberCountCol//
                = new TableColumn<ChannelSimple, Long>("Subscribers Count");

        // Create column videoCount (Long).
        TableColumn<ChannelSimple, Long> videoCountCol//
                = new TableColumn<ChannelSimple, Long>("Video Count");

        // Create column viewCount (Long).
        TableColumn<ChannelSimple, Long> viewCountCol //
                = new TableColumn<ChannelSimple, Long>("View Count");

        // Defines how to fill data for each cell.
        // Get value from property of ChannelSimple. .
        idCol.setCellValueFactory(new PropertyValueFactory<ChannelSimple, String>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<ChannelSimple, String>("title"));
        publishedAtCol.setCellValueFactory(new PropertyValueFactory<ChannelSimple, Date>("publishedAt"));
        subscriberCountCol.setCellValueFactory(new PropertyValueFactory<ChannelSimple, Long>("subscriberCount"));
        videoCountCol.setCellValueFactory(new PropertyValueFactory<ChannelSimple, Long>("videoCount"));
        viewCountCol.setCellValueFactory(new PropertyValueFactory<ChannelSimple, Long>("viewCount"));

        // Display row data
        ObservableList<ChannelSimple> list = getChannelList();

        table.getColumns().addAll(idCol, titleCol, publishedAtCol, subscriberCountCol, videoCountCol, viewCountCol);

        // если нужно показать комменты:
        if (showCommentsCount){
            // Create column commentsCount (Long).
            TableColumn<ChannelSimple, Long> commentsCountCol //
                    = new TableColumn<ChannelSimple, Long>("Comments Count");
            commentsCountCol.setCellValueFactory(new PropertyValueFactory<ChannelSimple, Long>("commentsCount"));
            table.getColumns().add(commentsCountCol);
        }

        // заполняем таблицу
        table.setItems(list);

        Label gap1 = new Label();
        Label gap2 = new Label();
        Label lblTitle = new Label(showCommentsCount?"Sort Channels By Media Resonance":"Sort Channels By Data");
        lblTitle.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 12));
        outputVBox.getChildren().addAll(gap1, lblTitle, gap2, table);

        long duration = System.currentTimeMillis() - startTime;

        if (Main.settingsSet.getShowTime()) {
            Label gap = new Label();
            Label lblExecutionTime = new Label("Execution time:     " + duration);
            outputVBox.getChildren().addAll(gap, lblExecutionTime);
        }
    }

    // получаем list для заполнения клеток таблицы
    private ObservableList<ChannelSimple> getChannelList() {
        ObservableList<ChannelSimple> list = FXCollections.observableArrayList();
        for (Channel channel:channels) {
            ChannelSimple channelSimple = (showCommentsCount?
                    new ChannelSimple(channel.id,
                            channel.snippet.title,
                            channel.snippet.publishedAt,
                            channel.statistics.subscriberCount,
                            channel.statistics.videoCount,
                            channel.statistics.viewCount,
                            channel.commentsCount):
                    new ChannelSimple(channel.id,
                            channel.snippet.title,
                            channel.snippet.publishedAt,
                            channel.statistics.subscriberCount,
                            channel.statistics.videoCount,
                            channel.statistics.viewCount)
            );
            list.add(channelSimple);
        }
        return list;
    }

    // дополнительный класс каналов для вывода в TableView
    public class ChannelSimple {
        private String id;
        private String title;
        private Date publishedAt;
        private Long subscriberCount;
        private Long videoCount;
        private Long viewCount;
        private Long commentsCount;

        public ChannelSimple(String id, String title, Date publishedAt, Long subscriberCount, Long videoCount, Long viewCount){
            this.id = id;
            this.title = title;
            this.publishedAt = publishedAt;
            this.subscriberCount = subscriberCount;
            this.videoCount = videoCount;
            this.viewCount = viewCount;
        }

        public ChannelSimple(String id, String title, Date publishedAt, Long subscriberCount, Long videoCount, Long viewCount, Long commentsCount){
            this(id,title,publishedAt,subscriberCount,videoCount,viewCount);
            this.commentsCount = commentsCount;
        }

        public String getId(){return id;}
        public String getTitle(){return title;}
        public Date getPublishedAt(){return publishedAt;}
        public Long getSubscriberCount(){return subscriberCount;}
        public Long getVideoCount(){return videoCount;}
        public Long getViewCount(){return viewCount;}
        public Long getCommentsCount(){return commentsCount;}

        public void setId(String id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setPublishedAt(Date publishedAt) {
            this.publishedAt = publishedAt;
        }

        public void setSubscriberCount(Long subscriberCount) {
            this.subscriberCount = subscriberCount;
        }

        public void setVideoCount(Long videoCount) {
            this.videoCount = videoCount;
        }

        public void setViewCount(Long viewCount) {
            this.viewCount = viewCount;
        }

        public void setCommentsCount(Long commentsCount){this.commentsCount = commentsCount;}
    }
}