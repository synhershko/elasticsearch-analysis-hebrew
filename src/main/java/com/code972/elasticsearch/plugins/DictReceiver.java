package com.code972.elasticsearch.plugins;

import com.code972.hebmorph.DictionaryLoader;
import com.code972.hebmorph.datastructures.DictHebMorph;
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
    private static DictHebMorph dict = null;

    public static DictHebMorph getDictionary() {
        return dict;
    }

    private static class LoadDictAction implements PrivilegedAction<DictHebMorph> {

        private final String path;
        private final DictionaryLoader loader;

        public LoadDictAction(final String path, final DictionaryLoader loader) {
            this.path = path;
            this.loader = loader;
        }

        @Override
        public DictHebMorph run() {
            final File file = new File(path);
            if (file.exists()) {
                try {
                    return loader.loadDictionaryFromPath(path);
                } catch (IOException e) {
                    // TODO remove printing
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public static boolean setDictionary(DictionaryLoader loader, String path) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // unprivileged code such as scripts do not have SpecialPermission
            sm.checkPermission(new SpecialPermission());
        }

        if (path != null) {
            final DictHebMorph tmp = AccessController.doPrivileged(new LoadDictAction(path, loader));
            if (dict != null) {
                dict = tmp;
                return true;
            }
        }

        return false;
    }

    public static DictHebMorph setDictionary(DictionaryLoader loader) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // unprivileged code such as scripts do not have SpecialPermission
            sm.checkPermission(new SpecialPermission());
        }

        for (final String path : loader.dictionaryPossiblePaths()) {
            final DictHebMorph dict = AccessController.doPrivileged(new LoadDictAction(path, loader));
            if (dict != null)
                return dict;
        }
        throw new IllegalArgumentException("Could not load any dictionary. Aborting!");
    }
}
