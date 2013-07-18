package ch.cyberduck.ui.action;

/*
 * Copyright (c) 2002-2010 David Kocher. All rights reserved.
 *
 * http://cyberduck.ch/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Bug fixes, suggestions and comments should be sent to:
 * dkocher@cyberduck.ch
 */

import ch.cyberduck.core.DisabledListProgressListener;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.i18n.Locale;

import java.text.MessageFormat;
import java.util.List;

/**
 * @version $Id$
 */
public abstract class CalculateSizeWorker extends Worker<Long> {

    private Session<?> session;

    /**
     * Selected files.
     */
    private List<Path> files;

    public CalculateSizeWorker(final Session session, final List<Path> files) {
        this.session = session;
        this.files = files;
    }

    private long total;

    @Override
    public Long run() throws BackgroundException {
        for(Path next : files) {
            next.attributes().setSize(this.calculateSize(next));
        }
        return total;
    }

    /**
     * Calculates recursively the size of this path if a directory
     * Potentially lengthy operation
     *
     * @param p Directory or file
     * @return The size of the file or the sum of all containing files if a directory
     */
    private long calculateSize(final Path p) throws BackgroundException {
        long size = 0;
        session.message(MessageFormat.format(Locale.localizedString("Getting size of {0}", "Status"),
                p.getName()));
        if(p.attributes().isDirectory()) {
            for(Path next : session.list(p, new DisabledListProgressListener())) {
                size += this.calculateSize(next);
            }
        }
        else if(p.attributes().isFile()) {
            size += p.attributes().getSize();
            total += size;
            this.update(total);
        }
        return size;
    }

    /**
     * Incremental update with latest size value.
     *
     * @param size Current known size
     */
    protected abstract void update(long size);

    @Override
    public String getActivity() {
        return MessageFormat.format(Locale.localizedString("Getting size of {0}", "Status"),
                this.toString(files));
    }
}
