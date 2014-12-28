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
    private static String[] gZipFilePaths = {"plugins/analysis-hebrew/dictH.gz", "/var/lib/hebmorph/dictH.gz", home + "/hebmorph/dictH.gz"};
    private static String[] hspellFilePaths = {"plugins/analysis-hebrew/hspell-data-files/", "/var/lib/hspell-data-files/", home + "/hspell-data-files/"};
    private static DictHebMorph dict = setDictionary();

    public static DictHebMorph getDictionary() {
        return dict;
    }

    private static DictHebMorph setDictionary() {
        File file;
        for (String path : gZipFilePaths) {
            file = new File(path);
            if (file.exists()) {
                try {
                    System.out.println("Successfully loaded from: " + file.getAbsolutePath());
                    return DictionaryLoader.loadDicAndPrefixesFromGzip(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Can't load from: " + file.getAbsolutePath());
        }
        for (String path : hspellFilePaths) {
            file = new File(path);
            if (file.exists() && file.isDirectory()) {
                try {
                    System.out.println("Successfully loaded from: " + file.getAbsolutePath());
                    HSpellLoader loader = new HSpellLoader(file, true);
                    return new DictHebMorph(loader.loadDictionaryFromHSpellData(), HSpellLoader.readDefaultPrefixes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Can't load from: " + file.getAbsolutePath());
        }
        throw new IllegalArgumentException("Could not load any dictionary. Aborting!");
    }
}
