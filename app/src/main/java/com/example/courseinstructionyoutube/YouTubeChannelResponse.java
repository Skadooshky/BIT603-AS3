package com.example.courseinstructionyoutube;

import java.util.List;

// YouTube API response for channel information
public class YouTubeChannelResponse {
    public List<Item> items;

    public static class Item {
        public Snippet snippet;
        public Statistics statistics;

        public static class Snippet {
            public String title;
            public Thumbnails thumbnails;

            public static class Thumbnails {
                public Default _default;

                public static class Default {
                    public String url;
                }
            }
        }

        public static class Statistics {
            public String subscriberCount;
        }
    }
}
