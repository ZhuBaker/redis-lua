package com.qf58.exec;

import com.qf58.exec.redpackage.DrawExec;
import com.qf58.exec.redpackage.RedDraw;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        InputStream inputStream = RedDraw.class.getClassLoader().getResourceAsStream("red/red.lua");
        List<String> strings = IOUtils.readLines(inputStream, Charset.forName("utf-8"));
        StringBuilder sb = new StringBuilder();
        for (String s: strings ) {
            sb.append(s).append("\n");
        }
        System.out.println(sb.toString());
    }
}
