package com.hpe.caf.worker.document.scripting.specs;

import com.hpe.caf.worker.document.DocumentWorkerScript;
import com.hpe.caf.worker.document.model.ScriptEngineType;
import jdk.nashorn.api.scripting.URLReader;

import javax.annotation.Nonnull;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class NashornUrlScriptSpec extends RemoteScriptSpec
{
    private final URL url;
    private final URI uri;

    public NashornUrlScriptSpec(final URL url) throws URISyntaxException
    {
        this.url = Objects.requireNonNull(url);
        this.uri = url.toURI();
    }

    @Nonnull
    @Override
    public CompiledScript compile(final Compilable compiler) throws ScriptException
    {
        // This method has been overridden in order to avoid calling close() on the URLReader after the compile() call.
        // Doing so actually causes it to read from the URL a further time!
        final Reader reader = new URLReader(url);
        return compiler.compile(reader);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof NashornUrlScriptSpec)) {
            return false;
        }

        final NashornUrlScriptSpec other = (NashornUrlScriptSpec) obj;

        return uri.equals(other.uri);
    }

    @Override
    public int hashCode()
    {
        return uri.hashCode();
    }

    @Override
    public boolean isStatic()
    {
        return false;
    }

    @Nonnull
    @Override
    public ScriptEngineType getEngineType()
    {
        return ScriptEngineType.NASHORN;
    }

    @Nonnull
    @Override
    protected Reader openReader() throws IOException
    {
        return new URLReader(url);
    }

    @Override
    protected void setScriptSpecField(final DocumentWorkerScript script)
    {
        script.url = url.toString();
    }
}
