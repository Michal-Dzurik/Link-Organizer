package sk.po.spse.dzurikm.linkorganizer.models;

public class Link {
    private int id,folder_id;
    private String name,description,href;

    public Link(int id, String name, String description, int folder_id) {
        this.id = id;
        this.folder_id = folder_id;
        this.name = name;
        this.description = description;
    }

    public Link(int id, int folder_id, String name, String description, String href) {
        this.id = id;
        this.folder_id = folder_id;
        this.name = name;
        this.description = description;
        this.href = href;
    }

    public Link(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Link() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(int folder_id) {
        this.folder_id = folder_id;
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
