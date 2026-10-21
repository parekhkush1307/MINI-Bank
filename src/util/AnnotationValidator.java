package util;

import model.Account;
import model.annotation.MaxLength;
import model.annotation.Positive;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab 07 – Uses reflection to validate an object's fields annotated with
 * {@code @Positive} and {@code @MaxLength}.
 */
public final class AnnotationValidator {

    private AnnotationValidator() { /* utility class */ }

    /**
     * Validates all annotated fields of {@code obj} and returns a String array
     * of error messages. An empty array means the object is valid.
     *
     * @param obj any object to validate
     * @return array of validation error messages (may be empty)
     */
    public static String[] validate(Object obj) {
        List<String> errors = new ArrayList<>();
        Class<?> clazz = obj.getClass();

        // Walk the class hierarchy so we also check superclass fields
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = null;
                try {
                    value = field.get(obj);
                } catch (IllegalAccessException e) {
                    continue; // unlikely after setAccessible(true)
                }

                // Check @Positive
                if (field.isAnnotationPresent(Positive.class)) {
                    Positive ann = field.getAnnotation(Positive.class);
                    if (value instanceof Number n && n.longValue() <= 0) {
                        errors.add("Field '" + field.getName() + "': " + ann.message());
                    }
                }

                // Check @MaxLength (Lab 07 Supplementary)
                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength ann = field.getAnnotation(MaxLength.class);
                    if (value instanceof String s && s.length() > ann.value()) {
                        errors.add("Field '" + field.getName() + "': " + ann.message()
                                + " (max " + ann.value() + ", got " + s.length() + ")");
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}
