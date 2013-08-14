
package qszhu.trakr;

import android.content.Context;

public enum Unit {
    Chapter(1, R.string.unit_chapter),
    Page(2, R.string.unit_page);

    private final int mValue;
    private final int mRes;

    Unit(int value, int res) {
        mValue = value;
        mRes = res;
    }

    public int getValue() {
        return mValue;
    }

    public String getName(Context context) {
        return context.getString(mRes);
    }

    public static Unit fromName(Context context, String name) {
        for (Unit unit : Unit.values()) {
            if (unit.getName(context).equals(name)) {
                return unit;
            }
        }
        return null;
    }

    public static Unit fromPosition(Context context, int position) {
        String[] array = context.getResources().getStringArray(R.array.units_array);
        return Unit.fromName(context, array[position]);
    }

}
