package mehdi.bad.addons.Config.modules;

public abstract class MovableModule {

    protected String name;
    protected int x;
    protected int y;
    protected int width, ogWidth;
    protected int height, ogHeight;
    protected boolean shouldKeepRatio;

    public MovableModule(String name, int x, int y, int width, int height, boolean shouldKeepRatio) {
        initialize(name, x, y, width, height, shouldKeepRatio);
    }

    public MovableModule(String name, int x, int y, int width, int height) {
        initialize(name, x, y, width, height, false);
    }

    private void initialize(String name, int x, int y, int width, int height, boolean shouldKeepRatio) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.ogWidth = width;
        this.ogHeight = height;
        this.width = ogWidth;
        this.height = ogHeight;
        this.shouldKeepRatio = shouldKeepRatio;
    }


    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDimensions(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void render();

    public boolean shouldKeepRatio() { return shouldKeepRatio; }

    public int getOriginalWidth() {
        return ogWidth;
    }
    public int getOriginalHeight() {
        return ogHeight;
    }

}