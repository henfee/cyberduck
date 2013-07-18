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

import ch.cyberduck.core.Acl;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.AclPermission;
import ch.cyberduck.core.i18n.Locale;

import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.List;

/**
 * @version $Id$
 */
public abstract class WriteAclWorker extends Worker<Acl> {
    private static Logger log = Logger.getLogger(WriteAclWorker.class);

    private Session<?> session;

    private AclPermission feature;

    /**
     * Selected files.
     */
    private List<Path> files;

    /**
     * Permissions to apply to files.
     */
    private Acl acl;

    /**
     * Descend into directories
     */
    private boolean recursive;

    public WriteAclWorker(final Session session, final AclPermission feature, final List<Path> files,
                          final Acl acl, final boolean recursive) {
        this.session = session;
        this.feature = feature;
        this.files = files;
        this.acl = acl;
        this.recursive = recursive;
    }

    @Override
    public Acl run() throws BackgroundException {
        for(Path file : files) {
            if(acl.isModified()) {
                session.message(MessageFormat.format(Locale.localizedString("Changing permission of {0} to {1}", "Status"),
                        file.getName(), acl));
                // Existing entry has been modified
                feature.write(file, acl, recursive);
            }
            else {
                if(acl.equals(file.attributes().getAcl())) {
                    if(log.isInfoEnabled()) {
                        log.info("Skip writing equal ACL for " + file);
                    }
                    return acl;
                }
                else {
                    // Additional entry added
                    feature.write(file, acl, recursive);
                }
            }
            if(recursive) {
                if(file.attributes().isDirectory()) {

                }
            }
        }
        return acl;
    }

    @Override
    public String getActivity() {
        return MessageFormat.format(Locale.localizedString("Changing permission of {0} to {1}", "Status"),
                this.toString(files), acl);
    }
}
