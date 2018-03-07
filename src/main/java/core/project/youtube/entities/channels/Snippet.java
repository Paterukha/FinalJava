package core.project.youtube.entities.channels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;



@JsonIgnoreProperties(ignoreUnknown = true)
public class Snippet {
    public String title;
    public Date publishedAt;
}
