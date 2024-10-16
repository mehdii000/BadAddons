package mehdi.bad.addons.Config.Setting;

import mehdi.bad.addons.Config.Property;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

public class Setting {

    public Setting parent = null;

    public Property annotation;

    public int height = 0;

    public Field field;

    public ArrayList sons = new ArrayList();

    public String name;

    public int y = -1;

    public int width = 0;

    public boolean illegal;

    public String description;

    public int x = -1;

    public Setting(Property property, Field field) {
        this.annotation = property;
        this.field = field;
        this.name = property.name();
        this.illegal = property.illegal();
        this.description = property.description();
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public int getIndent(int var1) {
        return this.getIndent(var1, this);
    }

    public boolean equals(Object var1) {
        return var1 instanceof Setting && ((Setting) var1).name.equals(this.name);
    }

    public int getIndent(int var1, Setting var2) {
        return var2.parent != null ? var2.getIndent(var1 + 10, var2.parent) : var1;
    }

    public boolean set(Object object) {
        try {
            this.field.set(object.getClass(), object);
            return true;
        } catch (Exception var3) {
            return false;
        }
    }

    public ArrayList getSons() {
        ArrayList var1 = new ArrayList();
        Iterator var2 = this.sons.iterator();

        Setting var3;
        while (var2.hasNext()) {
            var3 = (Setting) var2.next();
            if (var3.sons.size() == 0) {
                var1.add(var3);
            }
        }

        var2 = this.sons.iterator();

        while (var2.hasNext()) {
            var3 = (Setting) var2.next();
            if (var3.sons.size() != 0) {
                var1.add(var3);
                var1.addAll(var3.getSons());
            }
        }

        return var1;
    }

    public Object get(Class var1) {
        try {
            return var1.cast(this.field.get(Object.class));
        } catch (Exception var3) {
            return null;
        }
    }
}
