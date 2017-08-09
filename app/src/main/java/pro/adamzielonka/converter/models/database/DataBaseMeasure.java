package pro.adamzielonka.converter.models.database;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class DataBaseMeasure {

    public String uid;
    public String author;
    public String author_small;
    public String title;
    public String title_small;
    public String file;
    public Long version;
    public String units_symbols;
    public String units_names;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public DataBaseMeasure() {

    }

    public DataBaseMeasure(String uid, String author, String title, String units_symbols, String units_names, Long version) {
        this.uid = uid;
        this.author = author;
        this.author_small = author.toLowerCase();
        this.title = title;
        this.title_small = title.toLowerCase();
        this.units_symbols = units_symbols;
        this.units_names = units_names;
        this.version = version;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("author_small", author_small);
        result.put("title", title);
        result.put("title_small", title_small);
        result.put("file", file);
        result.put("version", version);
        result.put("units_symbols", units_symbols);
        result.put("units_names", units_names);
        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }

}
