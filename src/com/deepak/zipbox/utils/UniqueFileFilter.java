/*
 This file is part of Zipbox v0.1.1

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
package com.deepak.zipbox.utils;

import java.io.File;
/*
 if the file is present then a counter is added to the file
 abc.zip        abc(1).zip
 abc(1).zip     abc(2).zip
 abcd           abcd(1)
 abcd(1)        abcd(2)
 */

/**
 * this class provides the methods to generate a unique file based on the input
 * file.
 *
 * @author deepak
 */
public class UniqueFileFilter {

    // the file path
    private String filePath = null;
    // the file name
    private String fileName = null;
    // the file extension
    private String fileExtension = null;
    // the counter
    private int count = 0;
    // the file reference
    private File file = null;
    private String fileString = null;

    /**
     * method to generate the unique file
     *
     * @param f the input file
     * @return a unique file object
     */
    public File filter(File f) {
        // get the refernce
        file = f;
        // if the file/folder doesn't exists 
        // then return the same file
        if (!file.exists()) {
            return file;
        }

        // get the file path
        filePath = file.getParent();
        // get the file name
        fileName = file.getName();
        // initilize the extension
        fileExtension = null;

        // if the file refernce is to a  file object and contains "." (ie. contains extension)
        if (file.isFile() && fileName.contains(".")) {
            // get the file extension
            fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            // remove the extension from the file name
            fileName = fileName.substring(0, fileName.lastIndexOf(fileExtension));
        }

        // now if the file name already contains the counter "(x)" then
        if (fileName.contains("(") && fileName.contains(")")) {
            // initlize the counter
            count = 0;
            try {
                // get the counter value
                count = Integer.parseInt(
                        fileName.substring(
                                fileName.lastIndexOf("(") + 1,
                                fileName.lastIndexOf(")")));
                // increment it
                count++;
                // remove the counter from the file name
                fileName = fileName.substring(0, fileName.lastIndexOf("("));
            } catch (Exception e) {
                // if any exception occures then 
                count = 1;
            }
        } else {
            // if no counter is found then
            // set the counter to 1
            count = 1;
        }

        // generate a file using the file path, file name and the counter
        // repeat the below process until a unique file is got
        do {
            // generate the full file path
            fileString = filePath + File.separator + fileName + "(" + count + ")";
            // if is a file and has a extension then add it to the path
            if (file.isFile() && fileExtension != null) {
                fileString += fileExtension;
            }
            // create the file
            file = new File(fileString);
            // update the counter
            count++;
            // check whether the file exists. if exists then generate again
        } while (file.exists());

        // return the unique file refernce
        return file;
    }

}
