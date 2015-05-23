package app.android.kopper.ioutil;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kopper on 2015-05-21.
 * (C) Copyright 2015 kopperek@gmail.com
 * <p/>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
public class IOUtil {

    public static File createFile(String fileName,File parentDir,String importDirName) throws IOException {
        File importDir=createDirectory(parentDir,importDirName);
        File rawFile=new File(importDir,fileName);
        if(!rawFile.createNewFile())
            return(null);
        return (rawFile);
    }

    public static File createDirectory(File parent,String name) throws IOException {
        File file=new File(parent,name);
        if(!file.exists()) {
            if(!file.mkdirs())
                return(null);
        } else {
            if(!file.isDirectory())
                return(null);
        }
        return(file);
    }

    public static File createExternalStorageDirectory(String name) throws IOException {
        return createDirectory(Environment.getExternalStorageDirectory(),name);
    }

    public static void clearDir(File dir) {
        for(File ff:dir.listFiles()) {
            if(ff.isDirectory())
                clearDir(ff);
            ff.delete();
        }
    }

    public static void write(String fileName,File dir,byte[] content) throws IOException {
        File newFile=new File(dir,fileName);
        if(newFile.exists())
            newFile.delete();
        else
            newFile.createNewFile();
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(newFile);
            fos.write(content);
        } finally {
            if(fos!=null)
                fos.close();
        }
    }

    public static byte[] read(InputStream is) throws IOException {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        copy(is,baos);
        baos.close();
        return(baos.toByteArray());
    }

    public static void copy(InputStream is,OutputStream os) throws IOException {
        byte[] BUFFER=new byte[4096];
        for(;;) {
            int read=is.read(BUFFER);
            if(read==-1)
                break;
            os.write(BUFFER,0,read);
        }
    }

    public static byte[] readFile(File file) throws IOException {
        FileInputStream fis=new FileInputStream(file);
        byte[] bytes=read(fis);
        fis.close();
        return bytes;
    }
}
