package core.project.youtube.entities.channels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



@JsonIgnoreProperties(ignoreUnknown = true)
public class Statistics {
    public long subscriberCount;
    public long videoCount;
    public long viewCount;
    public long commentCount;
}
