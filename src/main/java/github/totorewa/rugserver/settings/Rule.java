package github.totorewa.rugserver.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Rule {
    /**
     * Description of rule
     */
    String desc();
    /**
     * Category of rule
     */
    String[] categories();
    /**
     * Additional remarks or information
     */
    String[] remarks() default {};
    /**
     * List of potential input options when changing the rule
     */
    String[] options() default {};
    /**
     * Restrict input to values present in options[]
     */
    boolean strict() default false;
    Class<? extends Validator>[] validator() default {};
}
