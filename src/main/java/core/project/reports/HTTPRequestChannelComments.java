package core.project.reports;

import com.alibaba.fastjson.JSON;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import core.project.Main;
import core.project.tools.Key;
import core.project.youtube.entities.channels.ChannelsResponse;
import core.project.tools.FileUtils;

import java.io.IOException;

public class HTTPRequestChannelComments {
    private static final String SEARCH_LINK = "https://www.googleapis.com/youtube/v3/commentThreads";
    // ключ грузим из файла
    //private static final String MY_KEY = "AIzaSyDwu_AH-9_PNHCKIiIzJ-uqXGwNWOfAURw";
    private static int countOfAllComments;
    private FileUtils fileUtils = new FileUtils();

    public int start(String channelid)  {

        countOfAllComments = 0;
        HttpResponse<ChannelsResponse> response = null;
        try {
            response = Unirest.get(SEARCH_LINK)
                    .queryString("part", "snippet,replies")
                    .queryString("allThreadsRelatedToChannelId", channelid)
                    .queryString("maxResults", "100")
                    .queryString("key", Key.myKey)
                    .asObject(ChannelsResponse.class);

            countOfAllComments = countOfAllComments + response.getBody().getPageInfo().getTotalResults();

            if (response.getBody().getPageInfo().getTotalResults() >= 100) {
                readPages(response.getBody().nextPageToken,channelid);
            }

            // если в настройках включена опция использования кэша, то
            // сохраняем результат в JSON формате в кэш для будущего использования
            if (Main.settingsSet.getUseCache()){
                String json = JSON.toJSONString(countOfAllComments);
                try {
                    fileUtils.writeToFile(json, getJSONPath(channelid));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return countOfAllComments;
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        System.out.println("HTTPRequestChannelComments eror");
        return -1;
    }

    private static void readPages(String nextPageToken, String channelid) throws UnirestException {

        HttpResponse<ChannelsResponse> response = Unirest.get(SEARCH_LINK)
                .queryString("part", "snippet,replies")
                .queryString("pageToken", nextPageToken)
                .queryString("allThreadsRelatedToChannelId",channelid)
                .queryString("maxResults", "100")
                .queryString("key", Key.myKey)
                .asObject(ChannelsResponse.class);


        countOfAllComments = countOfAllComments + response.getBody().getPageInfo().getTotalResults();

        if (response.getBody().getPageInfo().getTotalResults() < 100) {
            return;
        } else {
            readPages(response.getBody().nextPageToken,channelid);
        }
    }

    // получение  результатов запроса из предыдущего сохраненного JSON (если он существует)
    public int startFromJSON(String channelid) throws UnirestException, IOException {
        int result = 0;
        // если сохранен JSON локально, то тянем из него
        if (!fileUtils.fileExists(getJSONPath(channelid))){
            result = start(channelid);
            //если локально сохраненного JSON не существует, то тянем через HTTP
        } else {
            String json;
            json = fileUtils.readFromFile(getJSONPath(channelid));
            result = (int)JSON.parse(json);
        }

        return result;
    }

    private String getJSONPath(String id){
        return Main.settingsSet.getPathToCache() + "\\" + id + "_comments.json";
    }
}