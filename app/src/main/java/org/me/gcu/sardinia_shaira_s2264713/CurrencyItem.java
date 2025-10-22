package org.me.gcu.sardinia_shaira_s2264713;

public class CurrencyItem {

    private String title;
    private String category;
    private String pubDate;
    private String description;

    public CurrencyItem() {

    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String temp) {
        this.title = temp;
    }

    public void setCategory(String temp) {
        this.category = temp;
    }

    public void setPubDate(String temp) {
        this.pubDate = temp;
    }
    public void setDescription(String temp) {
        this.description = temp;
    }

    @Override
    public String toString() {
        return "CurrencyItem{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
