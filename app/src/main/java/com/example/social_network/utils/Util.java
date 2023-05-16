package com.example.social_network.utils;

public class Util {
    public static class PerformReaction {
        String userId, postId, postOwnerId, previousReactionType, newReactionType;

        public PerformReaction(String userId, String postId, String postOwnerId, String previousReactionType, String newReactionType) {
            this.userId = userId;
            this.postId = postId;
            this.postOwnerId = postOwnerId;
            this.previousReactionType = previousReactionType;
            this.newReactionType = newReactionType;
        }
    }
}
