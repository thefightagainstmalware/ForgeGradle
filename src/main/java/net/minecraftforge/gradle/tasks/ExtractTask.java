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
import net.minecraftforge.gradle.util.ExtractionVisitor;
import net.minecraftforge.gradle.util.caching.Cached;
import net.minecraftforge.gradle.util.caching.CachedTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class ExtractTask extends CachedTask implements PatternFilterable
{

    @InputFiles
    private LinkedHashSet<Object> sourcePaths      = new LinkedHashSet<Object>();

    @Input
    private PatternSet            patternSet       = new PatternSet();

    @Input
    private boolean               includeEmptyDirs = true;

    @Input
    @Optional
    private boolean               clean            = false;

    @Cached
    @OutputDirectory
    private Object                destinationDir   = null;

    @TaskAction
    public void doTask() throws IOException
    {
        File dest = getDestinationDir();

        if (shouldClean())
        {
            delete(dest);
        }

        dest.mkdirs();
        
        ExtractionVisitor visitor = new ExtractionVisitor(dest, isIncludeEmptyDirs(), patternSet.getAsSpec());

        for (File source : getSourcePaths())
        {
            getLogger().debug("Extracting: " + source);
            getProject().zipTree(source).visit(visitor);
        }
    }

    private void delete(File f) throws IOException
    {
        if (f.isDirectory())
        {
            for (File c : f.listFiles())
                delete(c);
        }
        f.delete();
    }

    public ExtractTask from(Object... paths)
    {
        for (Object path : paths)
        {
            sourcePaths.add(path);
        }
        return this;
    }

    public ExtractTask into(Object target)
    {
        destinationDir = target;
        return this;
    }

    public ExtractTask setDestinationDir(Object target)
    {
        destinationDir = target;
        return this;
    }

    public File getDestinationDir()
    {
        return getProject().file(destinationDir);
    }

    public FileCollection getSourcePaths()
    {
        return getProject().files(sourcePaths);
    }

    public boolean isIncludeEmptyDirs()
    {
        return includeEmptyDirs;
    }

    public void setIncludeEmptyDirs(boolean includeEmptyDirs)
    {
        this.includeEmptyDirs = includeEmptyDirs;
    }

    @Override
    protected boolean defaultCache()
    {
        return false;
    }

    public boolean shouldClean()
    {
        return clean;
    }

    public void setClean(boolean clean)
    {
        this.clean = clean;
    }

    @Override
    public PatternFilterable exclude(String... arg0)
    {
        return patternSet.exclude(arg0);
    }

    @Override
    public PatternFilterable exclude(Iterable<String> arg0)
    {
        return patternSet.exclude(arg0);
    }

    @Override
    public PatternFilterable exclude(Spec<FileTreeElement> arg0)
    {
        return patternSet.exclude(arg0);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public PatternFilterable exclude(Closure arg0)
    {
        return patternSet.exclude(arg0);
    }

    @Override
    public Set<String> getExcludes()
    {
        return patternSet.getExcludes();
    }

    @Override
    public Set<String> getIncludes()
    {
        return patternSet.getIncludes();
    }

    @Override
    public PatternFilterable include(String... arg0)
    {
        return patternSet.include(arg0);
    }

    @Override
    public PatternFilterable include(Iterable<String> arg0)
    {
        return patternSet.include(arg0);
    }

    @Override
    public PatternFilterable include(Spec<FileTreeElement> arg0)
    {
        return patternSet.include(arg0);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public PatternFilterable include(Closure arg0)
    {
        return patternSet.include(arg0);
    }

    @Override
    public PatternFilterable setExcludes(Iterable<String> arg0)
    {
        return patternSet.setExcludes(arg0);
    }

    @Override
    public PatternFilterable setIncludes(Iterable<String> arg0)
    {
        return patternSet.setIncludes(arg0);
    }
}
