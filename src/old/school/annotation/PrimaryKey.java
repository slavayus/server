package old.school.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by slavik on 24.05.17.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
        String columName();
}
