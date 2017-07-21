package pro.adamzielonka.converter.models.database;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String photo;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String photo) {
        this.username = username;
        this.email = email;
        this.photo = photo;
    }

}
// [END blog_user_class]
