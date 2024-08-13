package pa.centric.client.modules;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Type {
    Combat(0, "B"), Movement(0, "C"), Render(0, "D"), Player(0,"G" ), Util(0, "E"), Configs(0, "H");

    public double anim;
    public final String icon;


}
