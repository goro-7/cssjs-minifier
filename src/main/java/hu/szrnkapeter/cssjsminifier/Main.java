package hu.szrnkapeter.cssjsminifier;

import java.io.*;
import java.util.Objects;
import java.util.function.UnaryOperator;

import hu.szrnkapeter.cssjsminifier.compressor.CSSCompressorFactory;
import hu.szrnkapeter.cssjsminifier.compressor.JSCompressorFactory;
import hu.szrnkapeter.cssjsminifier.filter.CustomFileNameFilter;
import hu.szrnkapeter.cssjsminifier.util.Config;
import hu.szrnkapeter.cssjsminifier.util.FileUtils;
import hu.szrnkapeter.cssjsminifier.util.PropertyUtil;

public class Main {

    public static final Config CONFIG = PropertyUtil.loadProperties();
    public static final String MIN = "min";
    public static final String CSS = "css";
    public static final String JS = "js";

    /**
     * 1. argument: Directory of js files
     * 2. argument: optimization mode [whitespace, simple, advanced]
     *
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        compressFiles(CONFIG.getCssFolder(), CONFIG.getCssOut(), getCssCompressor(), new CustomFileNameFilter(CSS));
        compressFiles(CONFIG.getJsFolder(), CONFIG.getJsOut(), getJsCompressor(), new CustomFileNameFilter(JS));
    }

    private static UnaryOperator<String> getJsCompressor() {
        return inputFile -> {
            try {
                return new JSCompressorFactory(CONFIG.getJsCompressor()).getJsCompressor().compress(inputFile, CONFIG.getJsCompileType());
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        };
    }

    private static UnaryOperator<String> getCssCompressor() {
        return inputFile -> {
            try {
                return new CSSCompressorFactory(CONFIG.getCssCompressor()).getCssCompressor().compress(inputFile);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        };
    }

    private static void compressFiles(String inputFolder, String outputFolder, UnaryOperator<String> compressor, FilenameFilter filter) throws Exception {
        Objects.requireNonNull(inputFolder, "Input folder must not be empty " + inputFolder);
        // Currently it's just merge the css files
        final var folder = new File(inputFolder);
        if (folder.isDirectory() && Objects.requireNonNull(folder.listFiles(filter)).length > 0) {
            long i = 0;
            String outputFile = null;
            for (final File item : Objects.requireNonNull(folder.listFiles(filter))) {
                System.out.print("File: " + item.getName() + "; Filesize: " + item.length() / 1024 + "KB");
                outputFile = String.format("%s/%s", outputFolder, item.getName());
                FileUtils.createFile(outputFile);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                    if (!item.getName().toLowerCase().contains(MIN)) {
                        writer.append(compressor.apply(item.getAbsolutePath()));
                        writer.newLine();
                        System.out.println(" - compressed");
                    } else {
                        try (BufferedReader reader = new BufferedReader(new FileReader(item))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                writer.append(line);
                                writer.newLine();
                            }
                        }
                        System.out.println(" - compress skipped");
                    }
                    i++;
                }
            }
            System.out.println("--------------------------------------");
            System.out.println(i + " file added.  Output: " + outputFile);
        } else {
            System.out.println("No  files found in inputFolder " + inputFolder);
        }
    }
}
