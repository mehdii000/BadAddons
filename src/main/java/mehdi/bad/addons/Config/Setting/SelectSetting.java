package mehdi.bad.addons.Config.Setting;

import java.lang.reflect.Field;

import mehdi.bad.addons.Config.Property;

public class SelectSetting extends Setting {

    public String[] options;

    public SelectSetting(Property var1, Field var2) {
        super(var1, var2);
        this.options = var1.options();
    }

    public boolean set(Object var1) {
        if (((Number) var1).intValue() > this.options.length - 1) {
            return super.set(0);
        } else {
            return ((Number) var1).intValue() < 0 ? super.set(this.options.length - 1) : super.set(var1);
        }
    }
}
