package com.example.courseinstructionyoutube;

import java.util.List;

// YouTube API response for channel information
public class YouTubeChannelResponse {
    public List<Item> items;

    public class Item {
        public Snippet snippet;
        public Statistics statistics;

        public class Snippet {
            public String title;
            public String description;
            public Thumbnails thumbnails;

            public class Thumbnails {
                public Default _default;

                public class Default {
                    public String url;
                }
            }
        }

        public class Statistics {
            public String subscriberCount;
        }
    }
}
