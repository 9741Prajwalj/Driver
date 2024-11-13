package com.mlt.driver.models;

public class NotificationItem {
    private String message;
    private String dateTime;
    private int imageResource; // Resource ID for image
    private int pdfThumbnailResource; // Resource ID for PDF thumbnail
    private boolean hasImage;
    private boolean hasPdf;
    private boolean hasVideo;

    public NotificationItem(String message, String dateTime, int imageResource, int pdfThumbnailResource, boolean hasImage, boolean hasPdf, boolean hasVideo) {
        this.message = message;
        this.dateTime = dateTime;
        this.imageResource = imageResource;
        this.pdfThumbnailResource = pdfThumbnailResource;
        this.hasImage = hasImage;
        this.hasPdf = hasPdf;
        this.hasVideo = hasVideo;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getPdfThumbnailResource() {
        return pdfThumbnailResource;
    }

    public boolean hasImage() {
        return hasImage;
    }

    public boolean hasPdf() {
        return hasPdf;
    }

    public boolean hasVideo() {
        return hasVideo;
    }
}
