package mehdi.bad.addons.Config.Setting;

import mehdi.bad.addons.Config.Property;

import java.lang.reflect.Field;

public class ColorSetting extends Setting {

    public int color;

    public ColorSetting(Property property, Field field) {
        super(property, field);
        this.color = property.color();
    }

    public boolean set(Object value) {
        return super.set(value);
    }
}
