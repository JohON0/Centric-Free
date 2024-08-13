package pa.centric.client.modules;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface ModuleAnnotation {
    String name();

    String desc() default "";

    int key() default 0;

    Type category();
}
