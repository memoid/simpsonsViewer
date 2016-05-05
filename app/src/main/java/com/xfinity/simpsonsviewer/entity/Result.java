
package com.xfinity.simpsonsviewer.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Result {

    @SerializedName("RelatedTopics")
    @Expose
    private List<RelatedTopic> RelatedTopics = new ArrayList<>();

    public List<RelatedTopic> getRelatedTopics() {
        return RelatedTopics;
    }

    public void setRelatedTopics(List<RelatedTopic> relatedTopics) {
        RelatedTopics = relatedTopics;
    }

}
