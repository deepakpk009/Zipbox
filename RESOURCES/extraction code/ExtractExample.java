package example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class ExtractExample {
    static class ExtractionException extends Exception {
        private static final long serialVersionUID = -5108931481040742838L;

        ExtractionException(String msg) {
            super(msg);
        }

        public ExtractionException(String msg, Exception e) {
            super(msg, e);
        }
    }

    class ExtractCallback implements IArchiveExtractCallback {
        private ISevenZipInArchive inArchive;
        private int index;
        private Boolean skipExtraction;
        private OutputStream outputStream;
        private File file;

        ExtractCallback(ISevenZipInArchive inArchive) {
            this.inArchive = inArchive;
        }

        @Override
        public void setTotal(long total) throws SevenZipException {

        }

        @Override
        public void setCompleted(long completeValue) throws SevenZipException {

        }

        @Override
        public ISequentialOutStream getStream(int index,
                ExtractAskMode extractAskMode) throws SevenZipException {

            closeOutputStream();

            this.index = index;
            skipExtraction = (Boolean) inArchive.getProperty(index, PropID.IS_FOLDER);
            String path = (String) inArchive.getProperty(index, PropID.PATH);
            file = new File(outputDirectoryFile, path);
            if (skipExtraction || extractAskMode != ExtractAskMode.EXTRACT) {
                createDirectory(file);
                return null;
            }

            createDirectory(file.getParentFile());

            System.out.println(path);

            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                throw new SevenZipException("Error opening file: "
                    + file.getAbsolutePath(), e);
            }

            return new ISequentialOutStream() {
                public int write(byte[] data) throws SevenZipException {
                    try {
                        outputStream.write(data);
                    } catch (IOException e) {
                        throw new SevenZipException("Error writing to file: "
                            + file.getAbsolutePath());
                    }
                    return data.length; // Return amount of proceed data
                }
            };
        }

        private void createDirectory(File parentFile) throws SevenZipException {
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    throw new SevenZipException("Error creating directory: "
                        + parentFile.getAbsolutePath());
                }
            }
        }

        private void closeOutputStream() throws SevenZipException {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new SevenZipException("Error closing file: "
                        + file.getAbsolutePath());
                }
            }
        }

        @Override
        public void prepareOperation(ExtractAskMode extractAskMode)
                throws SevenZipException {

        }

        @Override
        public void setOperationResult(
                ExtractOperationResult extractOperationResult)
                throws SevenZipException {
            if (skipExtraction) {
                return;
            }
            if (extractOperationResult != ExtractOperationResult.OK) {
                String path = (String) inArchive.getProperty(index, PropID.PATH);
                throw new SevenZipException("Extraction error: " + path);
            } else {
            }
        }

    }

    private String archive;
    private String outputDirectory;
    private File outputDirectoryFile;

    ExtractExample(String archive, String outputDirectory) {
        this.archive = archive;
        this.outputDirectory = outputDirectory;
    }

    void extract() throws ExtractionException {
        checkArchiveFile();
        prepareOutputDirectory();
        extractArchive();
    }

    private void prepareOutputDirectory() throws ExtractionException {
        outputDirectoryFile = new File(outputDirectory);
        if (!outputDirectoryFile.exists()) {
            outputDirectoryFile.mkdirs();
        } else {
            if (outputDirectoryFile.list().length != 0) {
                throw new ExtractionException("Output directory not empty: "
                    + outputDirectory);
            }
        }
    }

    private void checkArchiveFile() throws ExtractionException {
        if (!new File(archive).exists()) {
            throw new ExtractionException("Archive file not found: " + archive);
        }
        if (!new File(archive).canRead()) {
            System.out.println("Can't read archive file: " + archive);
        }
    }

    public void extractArchive() throws ExtractionException {
        RandomAccessFile randomAccessFile;
        boolean ok = false;
        try {
            randomAccessFile = new RandomAccessFile(archive, "r");
        } catch (FileNotFoundException e) {
            throw new ExtractionException("File not found", e);
        }
        try {
            extractArchive(randomAccessFile);
            ok = true;
        } finally {
            try {
                randomAccessFile.close();
            } catch (Exception e) {
                if (ok) {
                    throw new ExtractionException("Error closing archive file", e);
                }
            }
        }
    }

    private void extractArchive(RandomAccessFile file) throws ExtractionException {
        ISevenZipInArchive inArchive;
        boolean ok = false;
        try {
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(file));
        } catch (SevenZipException e) {
            throw new ExtractionException("Error opening archive", e);
        }
        try {
            inArchive.extract(null, false, new ExtractCallback(inArchive));
            ok = true;
        } catch (SevenZipException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error extracting archive '");
            stringBuilder.append(archive);
            stringBuilder.append("': ");
            stringBuilder.append(e.getMessage());
            if (e.getCause() != null) {
                stringBuilder.append(" (");
                stringBuilder.append(e.getCause().getMessage());
                stringBuilder.append(')');
            }
            String message = stringBuilder.toString();

            throw new ExtractionException(message, e);
        } finally {
            try {
                inArchive.close();
            } catch (SevenZipException e) {
                if (ok) {
                    throw new ExtractionException("Error closing archive", e);
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage:");
            System.out.println("java -cp ... example.ExtractExample <archive> <output-dir>");
            System.exit(1);
        }
        try {
            new ExtractExample(args[0], args[1]).extract();
            System.out.println("Extraction successfull");
        } catch (ExtractionException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}