package pro.adamzielonka.converter.models.database;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class CloudMeasure {

    public String uid;
    public String author;
    public String photo;
    public String title;
    public String file;
    public Integer version;
    public String units_symbols;
    public String units_names;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public CloudMeasure() {

    }

    public CloudMeasure(String uid, String author, String title, String units_symbols, String units_names, Integer version, String photo) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.units_symbols = units_symbols;
        this.units_names = units_names;
        this.version = version;
        this.photo = photo;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("file", file);
        result.put("version", version);
        result.put("units_symbols", units_symbols);
        result.put("units_names", units_names);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("photo", photo);

        return result;
    }

}
