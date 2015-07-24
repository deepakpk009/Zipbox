/*
 ORIGINALCODE DETAILS
 ------------------------
 Advance ZIP example – Recursively
 
 Read all files from folder “C:\\testzip” and compress it into a zip file – “C:\\MyFile.zip“.
 It will recursively zip a directory as well.
 
 Author: mkyong (http://www.mkyong.com)
 Code Download Link: http://www.mkyong.com/java/how-to-compress-files-in-zip-format/
 -------------------------------------------------------------------------------
 THE CODE BELOW HAS BEEN MODIFIED BY DEEPAK.
 ALL CREDITS TO THE CODE GOES TO MKYONG
 -------------------------------------------------------------------------------
 */
package com.deepak.zipbox.core.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AppZip {

    List<String> fileList;
    private String sourceFolder = null;

    public AppZip() {
        fileList = new ArrayList<String>();
    }

    /**
     * Zip it
     *
     * @param zipFile output ZIP file location
     */
    public void zipIt(String zipFile) {

        byte[] buffer = new byte[1024];

        try {

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);

            for (String file : this.fileList) {

                System.out.println("File Added : " + file);
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in
                        = new FileInputStream(sourceFolder + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            //remember close it
            zos.close();

            System.out.println("Done");
            // added by deepak
            sourceFolder = null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Traverse a directory and get all files, and add the file into fileList
     *
     * @param node file or directory
     */
    public void generateFileList(File node) {

        // added by deepak
        if (sourceFolder == null) {
            sourceFolder = node.getParent();
        }

        //add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename));
            }
        }

    }

    /**
     * Format the file path for zip
     *
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file) {
        return file.substring(sourceFolder.length() + 1, file.length());
    }
}
