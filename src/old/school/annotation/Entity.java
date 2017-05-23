package old.school.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by slavik on 23.05.17.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    String tableName();
}
