package edu.uci.ics.hcheng10.service.billing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class thumbnailResponseModel extends ResponseModel {
    @JsonProperty(value = "thumbnails", required = true)
    private thumbnailList [] thumbnails;

    public static class thumbnailList {
        @JsonProperty(value = "movie_id", required = true)
        private String movie_id;
        @JsonProperty(value = "title", required = true)
        private String title;
        @JsonProperty(value = "backdrop_path", required = true)
        private String backdrop_path;
        @JsonProperty(value = "poster_path", required = true)
        private String poster_path;

        @JsonCreator
        public thumbnailList(@JsonProperty(value = "movie_id", required = true) String movie_id, @JsonProperty(value = "title", required = true) String title,
                             @JsonProperty(value = "backdrop_path", required = true) String backdrop_path, @JsonProperty(value = "poster_path", required = true) String poster_path) {
            this.movie_id = movie_id;
            this.title = title;
            this.backdrop_path = backdrop_path;
            this.poster_path = poster_path;
        }

        @JsonProperty("movie_id")
        public String getMovie_id() {
            return movie_id;
        }

        @JsonProperty("title")
        public String getTitle() {
            return title;
        }

        @JsonProperty("backdrop_path")
        public String getBackdrop_path() {
            return backdrop_path;
        }

        @JsonProperty("poster_path")
        public String getPoster_path() {
            return poster_path;
        }
    }

    @JsonCreator
    public thumbnailResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode, @JsonProperty(value = "message",required = true) String message,
                                  @JsonProperty(value = "thumbnails", required = true) thumbnailList [] thumbnails) {
        super(resultCode, message);
        this.thumbnails = thumbnails;
    }

    @JsonProperty("thumbnails")
    public thumbnailList [] getThumbnails() {return thumbnails;}
}