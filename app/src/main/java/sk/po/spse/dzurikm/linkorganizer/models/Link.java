package sk.po.spse.dzurikm.linkorganizer.models;

public class Link {
    private int id, folderId,colorId;
    private String name,description,href;

    public Link(int id, String name, String description, int folderId) {
        this.id = id;
        this.folderId = folderId;
        this.name = name;
        this.description = description;
    }

    public Link(int id, int folderId, String name, String description, String href,int colorId) {
        this.id = id;
        this.folderId = folderId;
        this.name = name;
        this.description = description;
        this.href = href;
        this.colorId = colorId;
    }

    public Link(String name, String description) {
        this.name = name;
        this.description = description;
        this.colorId = -1;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public Link() {
        this.colorId = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
