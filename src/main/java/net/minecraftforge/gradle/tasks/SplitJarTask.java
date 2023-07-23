/*
 * A Gradle plugin for the creation of Minecraft mods and MinecraftForge plugins.
 * Copyright (C) 2013 Minecraft Forge
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package net.minecraftforge.gradle.tasks;

import groovy.lang.Closure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.nio.file.attribute.FileTime;

import net.minecraftforge.gradle.util.caching.Cached;
import net.minecraftforge.gradle.util.caching.CachedTask;

import org.gradle.api.file.FileTreeElement;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;

import com.google.common.base.Throwables;

public class SplitJarTask extends CachedTask implements PatternFilterable
{
    @InputFile
    private Object     inJar;

    @Internal
    private PatternSet pattern = new PatternSet();

    @Cached
    @OutputFile
    private Object     outFirst;

    @Cached
    @OutputFile
    private Object     outSecond;

    @Input
    private Set<String> excludes;


    @Input
    private Set<String> includes;
    @TaskAction
    public void doTask() throws IOException
    {
        // get the spec
        final Spec<FileTreeElement> spec = pattern.getAsSpec();

        File input = getInJar();

        File out1 = getOutFirst();
        File out2 = getOutSecond();

        out1.getParentFile().mkdirs();
        out2.getParentFile().mkdirs();

        // begin reading jar
        final JarOutputStream zout1 = new JarOutputStream(new FileOutputStream(out1));
        final JarOutputStream zout2 = new JarOutputStream(new FileOutputStream(out2));

        getProject().zipTree(input).visit(new FileVisitor() {

            @Override
            public void visitDir(FileVisitDetails details)
            {
                // ignore directories
            }

            @Override
            public void visitFile(FileVisitDetails details)
            {
                JarEntry entry = new JarEntry(details.getPath());
                entry.setSize(details.getSize());
                entry.setCreationTime(FileTime.fromMillis(0L));
                entry.setLastAccessTime(FileTime.fromMillis(0L));
                entry.setLastModifiedTime(FileTime.fromMillis(0L));
                try
                {
                    if (spec.isSatisfiedBy(details))
                    {
                        zout1.putNextEntry(entry);
                        details.copyTo(zout1);
                        zout1.closeEntry();
                    }
                    else
                    {
                        zout2.putNextEntry(entry);
                        details.copyTo(zout2);
                        zout2.closeEntry();
                    }
                }
                catch (IOException e)
                {
                    Throwables.propagate(e);
                }
            }
        });

        zout1.close();
        zout2.close();
    }

    public PatternSet getPattern() {
        return pattern;
    }

    public File getInJar()
    {
        return getProject().file(inJar);
    }

    public void setInJar(Object inJar)
    {
        this.inJar = inJar;
    }

    public File getOutFirst()
    {
        return getProject().file(outFirst);
    }

    public void setOutFirst(Object outFirst)
    {
        this.outFirst = outFirst;
    }

    public File getOutSecond()
    {
        return getProject().file(outSecond);
    }

    public void setOutSecond(Object outSecond)
    {
        this.outSecond = outSecond;
    }

    @Override
    public PatternFilterable exclude(String... arg0)
    {
        return pattern.exclude(arg0);
    }

    @Override
    public PatternFilterable exclude(Iterable<String> arg0)
    {
        return pattern.exclude(arg0);
    }

    @Override
    public PatternFilterable exclude(Spec<FileTreeElement> arg0)
    {
        return pattern.exclude(arg0);
    }

    @Override
    public PatternFilterable exclude(Closure arg0)
    {
        return pattern.exclude(arg0);
    }

    @Override
    public Set<String> getExcludes()
    {
        excludes = pattern.getExcludes();
        return excludes;
    }

    @Override
    public Set<String> getIncludes()
    {
        includes = pattern.getIncludes();
        return includes;
    }

    @Override
    public PatternFilterable include(String... arg0)
    {
        return pattern.include(arg0);
    }

    @Override
    public PatternFilterable include(Iterable<String> arg0)
    {
        return pattern.include(arg0);
    }

    @Override
    public PatternFilterable include(Spec<FileTreeElement> arg0)
    {
        return pattern.include(arg0);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public PatternFilterable include(Closure arg0)
    {
        return pattern.include(arg0);
    }

    @Override
    public PatternFilterable setExcludes(Iterable<String> arg0)
    {
        return pattern.setExcludes(arg0);
    }

    @Override
    public PatternFilterable setIncludes(Iterable<String> arg0)
    {
        return pattern.setIncludes(arg0);
    }
}
