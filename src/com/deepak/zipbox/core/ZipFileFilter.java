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

import java.io.File;
import java.util.regex.Pattern;
import javax.swing.filechooser.FileFilter;
/*
 
 */

/**
 * file filter class to filter out the zip files supported by the app
 *
 * the supported file formats are : 7z, Zip, Tar, Rar, Lzma, Iso, GZip, Cpio,
 * BZIP2, Z, Arj, Lzh, Cab, Chm, Nsis, DEB, RPM, UDF, WIM
 *
 * @author deepak
 */
public class ZipFileFilter extends FileFilter {

    // supported file types
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(7z|zip|tar|rar|lzma"
            + "|iso|gzip|cpio|bzip2|z|arj|lzh|cab|chm|nsis|deb|rpm|udf|wim))$");

    @Override
    public boolean accept(File f) {
        // get the file name
        String name = f.getName().toLowerCase();
        // return whether the file extension is present or not
        return FILTERS.matcher(name).matches();
    }

    @Override
    public String getDescription() {
        return "Zip Files";
    }

}
