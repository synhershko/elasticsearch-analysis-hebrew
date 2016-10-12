package com.code972.elasticsearch.plugins;

import com.code972.hebmorph.datastructures.DictHebMorph;
import com.code972.hebmorph.hspell.HSpellDictionaryLoader;
import org.elasticsearch.SpecialPermission;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * This class will try to locate the dictionary to load, and call the DictionaryLoader class with the files it found
 * to initialize loading them and initializing the HebMorph analyzers.
 */
public class DictReceiver {
    private static String[] filePaths = {"plugins/analysis-hebrew/dictionary.dict", "/var/lib/hebmorph/dictionary.dict",
                                            "plugins/analysis-hebrew/hspell-data-files/", "/var/lib/hspell-data-files/"};
    private static DictHebMorph dict = null;

    public static DictHebMorph getDictionary() {
        if (dict == null) {
            dict = setDefaultDictionary();
        }
        return dict;
    }

    private static class LoadDictAction implements PrivilegedAction<DictHebMorph> {

        private final String path;
        private final HSpellDictionaryLoader loader;

        public LoadDictAction(final String path) {
            this.path = path;
            this.loader = new HSpellDictionaryLoader();
        }

        @Override
        public DictHebMorph run() {
            final File file = new File(path);
            if (file.exists()) {
                try {
                    return loader.loadDictionaryFromPath(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public static boolean setDictionary(String path) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // unprivileged code such as scripts do not have SpecialPermission
            sm.checkPermission(new SpecialPermission());
        }

        if (path != null) {
            final DictHebMorph tmp = AccessController.doPrivileged(new LoadDictAction(path));
            if (dict != null) {
                dict = tmp;
                return true;
            }
        }
        return false;
    }

    private static DictHebMorph setDefaultDictionary() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // unprivileged code such as scripts do not have SpecialPermission
            sm.checkPermission(new SpecialPermission());
        }

        for (final String path : filePaths) {
            final DictHebMorph dict = AccessController.doPrivileged(new LoadDictAction(path));
            if (dict != null)
                return dict;
        }
        throw new IllegalArgumentException("Could not load any dictionary. Aborting!");
    }
}
