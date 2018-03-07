package core.project.reports;

import com.alibaba.fastjson.JSON;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import core.project.Main;
import core.project.tools.FileUtils;
import core.project.tools.Key;
import core.project.youtube.entities.channels.ChannelsResponse;

import java.io.IOException;


public class HTTPRequest {
    private static final String SEARCH_LINK = "https://www.googleapis.com/youtube/v3/channels";
    // ключ грузим из файла
    //private static final String MY_KEY = "AIzaSyDwu_AH-9_PNHCKIiIzJ-uqXGwNWOfAURw";
    private FileUtils fileUtils = new FileUtils();

    // получение результатов запроса через HTTP
    public ChannelsResponse getHTTPResponse(String id) throws UnirestException {
        HttpResponse<ChannelsResponse> response = Unirest.get(SEARCH_LINK)
                .queryString("part", "snippet,statistics")
                .queryString("id", id)
                .queryString("key", Key.myKey)
                .asObject(ChannelsResponse.class);
        ChannelsResponse result = response.getBody();

        // если в настройках включена опция использования кэша, то
        // сохраняем результат в JSON формате в кэш для будущего использования
        if (Main.settingsSet.getUseCache()){
            String json = JSON.toJSONString(result);
            try {
                fileUtils.writeToFile(json, getJSONPath(id));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    // получение  результатов запроса из предыдущего сохраненного JSON (если он существует)
    public ChannelsResponse getJSONResponse(String id) throws UnirestException, IOException {
        ChannelsResponse result = null;
        // если сохранен JSON локально, то тянем из него
        if (!fileUtils.fileExists(getJSONPath(id))){
            result = getHTTPResponse(id);
        //если локально сохраненного JSON не существует, то тянем через HTTP
        } else {
            String json;
            json = fileUtils.readFromFile(getJSONPath(id));
            result = JSON.parseObject(json,ChannelsResponse.class);
        }

        return result;
    }

    private String getJSONPath(String id){
        return Main.settingsSet.getPathToCache() + "\\" + id + ".json";
    }

}
