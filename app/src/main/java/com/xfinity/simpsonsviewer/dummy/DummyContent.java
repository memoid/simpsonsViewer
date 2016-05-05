package com.xfinity.simpsonsviewer.dummy;

import com.xfinity.simpsonsviewer.entity.RelatedTopic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<RelatedTopic> characters = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Character> ITEM_MAP = new HashMap<>();

    private static void addItem(RelatedTopic item) {
        characters.add(item);
        ITEM_MAP.put(item.getFirstURL(), new Character(item.getText(), item.getResult(), item.getIcon().getURL()));
    }

    private static Character createDummyItem(int position) {
        return new Character(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class Character {
        public final String id;
        public final String content;
        public final String details;

        public Character(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
