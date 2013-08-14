
package qszhu.trakr;

import android.content.Context;

public enum Repeat {

    EVERY_DAY(1, R.string.repeat_day),
    EVERY_WEEK(2, R.string.repeat_week),
    EVERY_MONTH(4, R.string.repeat_month);

    private final int mValue;
    private final int mRes;

    Repeat(int value, int res) {
        mValue = value;
        mRes = res;
    }

    public int getValue() {
        return mValue;
    }

    public String getName(Context context) {
        return context.getString(mRes);
    }

    public static Repeat fromName(Context context, String name) {
        for (Repeat repeat : Repeat.values()) {
            if (repeat.getName(context).equals(name)) {
                return repeat;
            }
        }
        return null;
    }

    public static Repeat fromPosition(Context context, int position) {
        String[] array = context.getResources().getStringArray(R.array.repeats_array);
        return Repeat.fromName(context, array[position]);
    }

}
