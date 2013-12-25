package com.code972.elasticsearch.analysis;

import com.code972.hebmorph.LookupTolerators;
import com.code972.hebmorph.MorphData;
import com.code972.hebmorph.StreamLemmatizer;
import com.code972.hebmorph.Tokenizer;
import com.code972.hebmorph.datastructures.DictRadix;
import com.code972.hebmorph.hspell.LingInfo;
import com.code972.hebmorph.hspell.Loader;
import com.code972.hebmorph.lemmafilters.BasicLemmaFilter;
import com.code972.hebmorph.lemmafilters.LemmaFilterBase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import org.elasticsearch.common.base.Charsets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class HebrewAnalyzer extends Analyzer {
    protected static final Version matchVersion = Version.LUCENE_45;

    protected static final DictRadix<Integer> prefixesTree = LingInfo.buildPrefixTree(false);
    protected static DictRadix<MorphData> dictRadix;
    protected static DictRadix<MorphData> customWords;
    protected final StreamLemmatizer lemmatizer;
    protected final LemmaFilterBase lemmaFilter;
    protected final char originalTermSuffix = '$';

    private final static Integer[] descFlags_noun;
    private final static Integer[] descFlags_person_name;
    private final static Integer[] descFlags_place_name;
    private final static Integer[] descFlags_empty;
    private static final Byte dummyData = new Byte((byte)0);
    protected final static DictRadix<Byte> SPECIAL_TOKENIZATION_CASES;


    /** An unmodifiable set containing some common Hebrew words that are usually not
     useful for searching.
     */
    protected final CharArraySet commonWords = null; // TODO


    static {
        descFlags_noun = new Integer[] { 69 };
        descFlags_person_name = new Integer[] { 262145 };
        descFlags_place_name = new Integer[] { 262153 };
        descFlags_empty = new Integer[] { 0 };

        try {
            final CharArraySet wordsList = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(
                    new File(/*resourcesPath +*/ "special_tokenization_cases.txt"), IOUtils.CHARSET_UTF_8),
                    matchVersion);

            final DictRadix<Byte> radix = new DictRadix<>(false);
            final Iterator<Object> it = wordsList.iterator();
            while (it.hasNext()) {
                radix.addNode((char[]) it.next(), dummyData);
            }
            SPECIAL_TOKENIZATION_CASES = radix;
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load special tokenization cases list", ex);
        }

        try {
            dictRadix = Loader.loadDictionaryFromHSpellData(new File(/* resourcesPath +*/ "hspell-data-files"), true);
        } catch (IOException e) {
            // TODO log
        }

        try {
            customWords = loadCustomWords(dictRadix);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static DictRadix<MorphData> loadCustomWords(final DictRadix<MorphData> dictRadix) throws IOException {
        final List<String> lines = Files.readAllLines(new File(/* resourcesPath, */ "hebrew_custom.txt").toPath(), Charsets.UTF_8);

        final Hashtable<String, String> secondPass = new Hashtable<>();
        final DictRadix<MorphData> custom = new DictRadix<>();
        for (final String line : lines) {
            String[] cells = line.split(" ");
            if (cells.length < 2)
                continue;

            MorphData md = null;
            if ("שםעצם".equals(cells[1])) {
                md = new MorphData();
                md.setPrefixes((short) 63);
                md.setLemmas(new String[] { cells[0] });
                md.setDescFlags(descFlags_noun);
            } else if ("שםחברה".equals(cells[1]) || "שםפרטי".equals(cells[1])) {
                md = new MorphData();
                md.setPrefixes((short) 8);
                md.setLemmas(new String[] { cells[0] });
                md.setDescFlags(descFlags_person_name);
            } else if ("שםמקום".equals(cells[1])) {
                md = new MorphData();
                md.setPrefixes((short) 8);
                md.setLemmas(new String[] { cells[0] });
                md.setDescFlags(descFlags_place_name);
            } else if ("שםמדויק".equals(cells[1])) {
                md = new MorphData();
                md.setPrefixes((short) 0);
                md.setLemmas(new String[] { cells[0] });
                md.setDescFlags(descFlags_empty);
            }

            if (md == null) { // allow to associate new entries with other custom entries
                try {
                    md = custom.lookup(cells[1], false);
                } catch (IllegalArgumentException ignored_ex) {
                }
            }

            if (md == null) {
                try {
                    md = dictRadix.lookup(cells[1], false);
                } catch (IllegalArgumentException ignored_ex) {
                }
            }

            if (md != null) {
                custom.addNode(cells[0], md);
            } else {
                secondPass.put(cells[0], cells[1]);
            }
        }

        for (final Map.Entry<String, String> entry : secondPass.entrySet()) {
            try {
                custom.lookup(entry.getKey(), false);
                continue; // we already stored this word somehow
            } catch (IllegalArgumentException expected_ex) {
            }

            try {
                final MorphData md = custom.lookup(entry.getValue(), false);
                if (md != null) custom.addNode(entry.getKey(), md);
            } catch (IllegalArgumentException ignored_ex) {
            }
        }

        return custom;
    }

    protected HebrewAnalyzer() throws IOException {
        lemmatizer = new StreamLemmatizer(null, dictRadix, prefixesTree, SPECIAL_TOKENIZATION_CASES);
        lemmatizer.setCustomWords(customWords);
        lemmaFilter = new BasicLemmaFilter();
    }

    public static boolean isHebrewWord(final CharSequence word) {
        for (int i = 0; i < word.length(); i++) {
             if (Tokenizer.isHebrewLetter(word.charAt(i)))
                 return true;
        }
        return false;
    }

    public enum WordType {
        HEBREW,
        HEBREW_WITH_PREFIX,
        HEBREW_TOLERATED,
        HEBREW_TOLERATED_WITH_PREFIX,
        NON_HEBREW,
        UNRECOGNIZED,
        CUSTOM,
        CUSTOM_WITH_PREFIX,
    }

    public static WordType isRecognizedWord(final String word, final boolean tolerate) {
        byte prefLen = 0;
        Integer prefixMask;
        MorphData md;

        try {
            if (customWords.lookup(word) != null) return WordType.CUSTOM;
        } catch (IllegalArgumentException e) {
        }

        while (true) {
            // Make sure there are at least 2 letters left after the prefix (the words של, שלא for example)
            if (word.length() - prefLen < 2)
                break;

            try {
                prefixMask = prefixesTree.lookup(word.substring(0, ++prefLen));
            } catch (IllegalArgumentException e) {
                break;
            }

            try {
                md = customWords.lookup(word.substring(prefLen));
            } catch (IllegalArgumentException e) {
                md = null;
            }
            if ((md != null) && ((md.getPrefixes() & prefixMask) > 0)) {
                for (int result = 0; result < md.getLemmas().length; result++) {
                    if ((LingInfo.DMask2ps(md.getDescFlags()[result]) & prefixMask) > 0) {
                        return WordType.CUSTOM_WITH_PREFIX;
                    }
                }
            }
        }

        try {
            if (dictRadix.lookup(word) != null) return WordType.HEBREW;
        } catch (IllegalArgumentException e) {
        }

        if (word.endsWith("'")) { // Try ommitting closing Geresh
            try {
                if (dictRadix.lookup(word.substring(0, word.length() - 1)) != null) return WordType.HEBREW;
            } catch (IllegalArgumentException e) {
            }
        }

        prefLen = 0;
        while (true) {
            // Make sure there are at least 2 letters left after the prefix (the words של, שלא for example)
            if (word.length() - prefLen < 2)
                break;

            try {
                prefixMask = prefixesTree.lookup(word.substring(0, ++prefLen));
            } catch (IllegalArgumentException e) {
                break;
            }

            try {
                md = dictRadix.lookup(word.substring(prefLen));
            } catch (IllegalArgumentException e) {
                md = null;
            }
            if ((md != null) && ((md.getPrefixes() & prefixMask) > 0)) {
                for (int result = 0; result < md.getLemmas().length; result++) {
                    if ((LingInfo.DMask2ps(md.getDescFlags()[result]) & prefixMask) > 0) {
                        return WordType.HEBREW_WITH_PREFIX;
                    }
                }
            }
        }

        if (tolerate) {
            // Don't try tolerating long words. Longest Hebrew word is 19 chars long
            // http://en.wikipedia.org/wiki/Longest_words#Hebrew
            if (word.length() > 20) {
                return WordType.UNRECOGNIZED;
            }

            List<DictRadix<MorphData>.LookupResult> tolerated = dictRadix.lookupTolerant(word, LookupTolerators.TolerateEmKryiaAll);
            if (tolerated != null && tolerated.size() > 0)
            {
                return WordType.HEBREW_TOLERATED;
            }

            prefLen = 0;
            while (true)
            {
                // Make sure there are at least 2 letters left after the prefix (the words של, שלא for example)
                if (word.length() - prefLen < 2)
                    break;

                try {
                    prefixMask = prefixesTree.lookup(word.substring(0, ++prefLen));
                } catch (IllegalArgumentException e) {
                    break;
                }

                tolerated = dictRadix.lookupTolerant(word.substring(prefLen), LookupTolerators.TolerateEmKryiaAll);
                if (tolerated != null)
                {
                    for (DictRadix<MorphData>.LookupResult lr : tolerated)
                    {
                        for (int result = 0; result < lr.getData().getLemmas().length; result++)
                        {
                            if ((LingInfo.DMask2ps(lr.getData().getDescFlags()[result]) & prefixMask) > 0)
                            {
                                return WordType.HEBREW_TOLERATED_WITH_PREFIX;
                            }
                        }
                    }
                }
            }
        }

        return WordType.UNRECOGNIZED;
    }
}
