/*
 This file is part of Zipbox v0.1

 Zipbox is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Zipbox is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with Zipbox.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.deepak.zipbox.core;

import com.deepak.zipbox.core.zip.AppZip;
import com.deepak.zipbox.core.zip.ExtractExample;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class provides methods for zipping files and folders
 *
 * @author deepak
 */
public class Zipper {

    // flag to check whether the zipper is processing or not
    private boolean running = false;

    /**
     * method to zip a file
     *
     * @param file the file to be zipped
     * @param outputZipFile the out put zip file
     */
    public void zip(final File file, final File outputZipFile) {
        // use a thread to precess
        // so the main UI thread won't get exhausted 
        new Thread() {

            @Override
            public void run() {
                // set the running flag to true
                running = true;
                // create an instance of the AppZip class
                AppZip appZip = new AppZip();
                // add the file to generate file list
                appZip.generateFileList(file);
                // zip the file to the output zip file
                appZip.zipIt(outputZipFile.getAbsolutePath());
                // on processing complete reset the running flag
                running = false;
            }
            // run the thread
        }.start();
    }

    /**
     * method to zip a list of files
     *
     * @param files the files list array
     * @param outputZipFile the output zip file
     */
    public void zip(final File[] files, final File outputZipFile) {
        // run using a thread
        new Thread() {

            @Override
            public void run() {
                // set running flag
                running = true;
                // create object of appzip
                AppZip appZip = new AppZip();
                // add files ot the file list for zipping
                for (File f : files) {
                    appZip.generateFileList(f);
                }
                // zip it
                appZip.zipIt(outputZipFile.getAbsolutePath());
                // rest the running flag
                running = false;
            }
            // start the thread
        }.start();
    }

    /**
     * Unzip a file
     *
     * @param zipFile input zip file
     * @param outputFolder folder to save the extracted files
     */
    public void unZip(final File zipFile, final File outputFolder) {
        // use thread for runnign
        new Thread() {

            @Override
            public void run() {
                try {
                    // set running flag
                    running = true;
                    // create a anonymous object and call the unzip function
                    new ExtractExample(zipFile.getAbsolutePath(), outputFolder.getAbsolutePath()).extract();
                    // on complete rest the running flag
                    running = false;
                } catch (ExtractExample.ExtractionException ex) {
                    Logger.getLogger(Zipper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // start the thread
        }.start();

    }

    /**
     * method to check whether the zipper is running or not
     *
     * @return true if running else false
     */
    public boolean isRunning() {
        return running;
    }

}
