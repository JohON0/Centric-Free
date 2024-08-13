package pa.centric.util.render.shader;

import pa.centric.util.render.shader.core.Shader;
import pa.centric.util.render.shader.impl.AlphaShader;

import java.util.ArrayList;
import java.util.List;

public class Shaders {

    public List<Shader> shaderList = new ArrayList<>();

    public Shaders() {

        shaderList.addAll(List.of(
           new AlphaShader("")
        ));

    }

}
