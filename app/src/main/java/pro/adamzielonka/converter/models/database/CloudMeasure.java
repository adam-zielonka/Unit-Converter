package pro.adamzielonka.converter.models.database;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class CloudMeasure {

    public String uid;
    public String author;
    public String title;
    public Integer version;
    public String units_symbols;
    public String units_names;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public CloudMeasure() {
        // Default constructor required for calls to DataSnapshot.getValue(CloudMeasure.class)
    }

    public CloudMeasure(String uid, String author, String title, String units_symbols, String units_names, Integer version) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.units_symbols = units_symbols;
        this.units_names = units_names;
        this.version = version;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("version", version);
        result.put("units_symbols", units_symbols);
        result.put("units_names", units_names);
        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
