package uk.co.rockstable.experiements.proberecord;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class Utils {
    private static final Unsafe UNSAFE = findUnsafe();

    public static RuntimeException rethrow(Throwable exception) {
        throw new RuntimeException(exception);
    }

    public static int modPowerOfTwo(int a, int b) {
        return a & (b - 1);
    }

    public static long modPowerOfTwo(long a, int b) {
        return a & (b - 1);
    }

    public static void storeFence() {
        UNSAFE.storeFence();
    }

    private static Unsafe findUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException se) {
            return AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {
                @Override
                public Unsafe run() {
                    try {
                        Class<Unsafe> type = Unsafe.class;
                        try {
                            Field field = type.getDeclaredField("theUnsafe");
                            field.setAccessible(true);
                            return type.cast(field.get(type));

                        } catch (Exception e) {
                            for (Field field : type.getDeclaredFields()) {
                                if (type.isAssignableFrom(field.getType())) {
                                    field.setAccessible(true);
                                    return type.cast(field.get(type));
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Unsafe unavailable", e);
                    }
                    throw new RuntimeException("Unsafe unavailable");
                }
            });
        }
    }
}
