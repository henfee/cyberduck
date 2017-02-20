package ch.cyberduck.core.vault.registry;

/*
 * Copyright (c) 2002-2017 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
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
 */

import ch.cyberduck.core.ConnectionCallback;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.TransferAcceleration;
import ch.cyberduck.core.http.HttpSession;
import ch.cyberduck.core.ssl.X509KeyManager;
import ch.cyberduck.core.ssl.X509TrustManager;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.core.vault.DefaultVaultRegistry;

public class VaultRegistryTransferAccelerationFeature<C extends HttpSession<?>> implements TransferAcceleration<C> {
    private final Session<?> session;
    private final TransferAcceleration<C> proxy;
    private final DefaultVaultRegistry registry;

    public VaultRegistryTransferAccelerationFeature(final Session<?> session, final TransferAcceleration<C> proxy, final DefaultVaultRegistry registry) {
        this.session = session;
        this.proxy = proxy;
        this.registry = registry;
    }

    @Override
    public boolean getStatus(final Path file) throws BackgroundException {
        return registry.find(session, file).getFeature(session, TransferAcceleration.class, proxy).getStatus(file);
    }

    @Override
    public void setStatus(final Path file, final boolean enabled) throws BackgroundException {
        registry.find(session, file).getFeature(session, TransferAcceleration.class, proxy).setStatus(file, enabled);
    }

    @Override
    public boolean prompt(final Host bookmark, final Path file, final TransferStatus status, final ConnectionCallback prompt) throws BackgroundException {
        return registry.find(session, file).getFeature(session, TransferAcceleration.class, proxy).prompt(bookmark, file, status, prompt);
    }

    @Override
    @SuppressWarnings("unchecked")
    public C open(final Host bookmark, final Path file, final X509TrustManager trust, final X509KeyManager key) throws BackgroundException {
        return (C) registry.find(session, file).getFeature(session, TransferAcceleration.class, proxy).open(bookmark, file, trust, key);
    }
}