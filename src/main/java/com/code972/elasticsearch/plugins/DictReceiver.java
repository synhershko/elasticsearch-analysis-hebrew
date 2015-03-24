package com.code972.elasticsearch.plugins;

import com.code972.hebmorph.DictionaryLoader;
import com.code972.hebmorph.datastructures.DictHebMorph;
import com.code972.hebmorph.hspell.HSpellLoader;

import java.io.File;
import java.io.IOException;

/**
 * Created by Egozy on 03/12/2014.
 */
public class DictReceiver {
    private static String home = System.getProperty("user.home");
    private static String[] filePaths = {"plugins/analysis-hebrew/dictionary.dict", "/var/lib/hebmorph/dictionary.dict", home + "/hebmorph/dictionary.dict", 
                                            "plugins/analysis-hebrew/hspell-data-files/", "/var/lib/hspell-data-files/", home + "/hspell-data-files/"};
    private static DictHebMorph dict = null;

    public static DictHebMorph getDictionary() {
        if (dict == null) {
            dict = setDefaultDictionary();
        }
        return dict;
    }

    public static boolean setDictionary(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    dict = DictionaryLoader.loadDictFromPath(path);
                    System.out.println("Successfully loaded from: " + path);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static DictHebMorph setDefaultDictionary() {
        File file;
        for (String path : filePaths) {
            file = new File(path);
            if (file.exists()) {
                try {
                    DictHebMorph ret = DictionaryLoader.loadDictFromPath(path);
                    System.out.println("Successfully loaded from: " + file.getAbsolutePath());
                    return ret;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new IllegalArgumentException("Could not load any dictionary. Aborting!");
    }
}
