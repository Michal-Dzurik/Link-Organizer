package sk.po.spse.dzurikm.linkorganizer.models;

public class Folder {
    private String name,description;
    private int id,colorId;

    public Folder(int id,String name,String description,int colorId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.colorId = colorId;
    }

    public Folder() {
        this.colorId = -1;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
