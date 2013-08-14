
package qszhu.trakr.target;

import android.text.TextUtils;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import qszhu.trakr.R;

@ParseClassName("Target")
public class Target extends ParseObject {

    public static final String COL_NAME = "name";
    public static final String COL_DESCRIPTION = "summary";
    public static final String COL_CREATOR = "creator";

    public String getName() {
        return getString(COL_NAME);
    }

    public Target setName(String name) {
        put(COL_NAME, name);
        return this;
    }

    public String getDescription() {
        return getString(COL_DESCRIPTION);
    }

    public Target setDescription(String description) {
        put(COL_DESCRIPTION, description);
        return this;
    }

    public ParseUser getCreator() {
        return getParseUser(COL_CREATOR);
    }

    private Target setCreator() {
        put(COL_CREATOR, ParseUser.getCurrentUser());
        return this;
    }

    public int getValidationError() {
        if (TextUtils.isEmpty(getName())) {
            return R.string.error_missing_target_name;
        }
        if (getCreator() == null) {
            setCreator();
        }
        return 0;
    }

}
