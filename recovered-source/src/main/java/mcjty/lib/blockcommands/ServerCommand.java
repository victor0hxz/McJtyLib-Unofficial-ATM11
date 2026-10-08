package mcjty.lib.blockcommands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface ServerCommand {
   Class type() default void.class;

   Class<? extends ISerializer> serializer() default ISerializer.class;
}
