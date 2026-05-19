package net.dobiasovsky.lastmission;

import java.util.Locale;
import java.util.ResourceBundle;

public final class LocalizationManager {
    private static ResourceBundle bundle;
    private static Locale currentLocale;

    static {
        // Initialize with system locale
        currentLocale = Locale.getDefault();
        loadBundle();
    }

    private LocalizationManager() {
        // Utility class
    }

    /**
     * Load resource bundle for current locale
     */
    private static void loadBundle() {
        try {
            bundle = ResourceBundle.getBundle(
                    "net.dobiasovsky.lastmission.messages",
                    currentLocale,
                    LocalizationManager.class.getClassLoader()
            );
        } catch (Exception e) {
            // Fallback to English
            bundle = ResourceBundle.getBundle(
                    "net.dobiasovsky.lastmission.messages",
                    Locale.ENGLISH,
                    LocalizationManager.class.getClassLoader()
            );
        }
    }

    /**
     * Get localized message by key
     */
    public static String getMessage(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key; // Return key if translation not found
        }
    }

    /**
     * Get localized message by key and format with arguments using String.format
     */
    public static String getMessage(String key, Object... args) {
        try {
            String message = bundle.getString(key);
            return String.format(message, args);
        } catch (Exception e) {
            return key; // Return key if translation not found
        }
    }

    /**
     * Set locale and reload bundle
     */
    public static void setLocale(Locale locale) {
        currentLocale = locale;
        loadBundle();
    }

    /**
     * Get current locale
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}


