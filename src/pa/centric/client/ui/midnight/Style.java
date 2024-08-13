package pa.centric.client.ui.midnight;


import pa.centric.util.render.ColorUtil;

public class Style {
    public String name;
    public int[] colors;

    public Style(String name, int... colors) {
        this.name = name;
        this.colors = colors;
    }


    public int getColor(int index) {
        if (name.equals("Àסעמכüפמ")) {
            return ColorUtil.astolfo(10,index, 1F, 1.0F, 1.0F);
        }
        return ColorUtil.gradient(5,
                index, colors);
    }

}