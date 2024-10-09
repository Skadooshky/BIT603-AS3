package com.example.courseinstructionyoutube;

import java.util.List;

public class YouTubeVideosResponse {

    public List<Item> items;

    // Inner class Item
    public static class Item {
        public Id id;
        public Snippet snippet;

        // Inner class Id (for holding videoId)
        public static class Id {
            public String videoId;
        }

        // Inner class Snippet (for holding video details like title, thumbnail, etc.)
        public static class Snippet {
            public String title;
            public Thumbnails thumbnails;

            // Inner class Thumbnails
            public static class Thumbnails {
                public Thumbnail _default;
                public Thumbnail medium;
                public Thumbnail high;

                // Inner class Thumbnail (for holding the thumbnail URL)
                public static class Thumbnail {
                    public String url;
                }
            }
        }
    }
}
