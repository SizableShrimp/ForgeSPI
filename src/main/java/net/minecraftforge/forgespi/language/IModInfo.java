/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.forgespi.language;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.forgespi.Environment;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.net.URL;
import java.util.List;
import java.util.Map;

public interface IModInfo
{
    VersionRange UNBOUNDED = MavenVersionAdapter.createFromVersionSpec("");

    IModFileInfo getOwningFile();

    String getModId();

    String getDisplayName();

    String getDescription();

    ArtifactVersion getVersion();

    List<ModVersion> getDependencies();

    UnmodifiableConfig getModConfig();

    String getNamespace();

    Map<String,Object> getModProperties();

    URL getUpdateURL();


    enum Ordering {
        BEFORE, AFTER, NONE
    }

    enum DependencySide {
        CLIENT(Dist.CLIENT), SERVER(Dist.DEDICATED_SERVER), BOTH(Dist.values());

        private final Dist[] dist;

        DependencySide(final Dist... dist) {
            this.dist = dist;
        }

        public boolean isCorrectSide()
        {
            return this == BOTH || Environment.get().getDist().equals(this.dist[0]);
        }
    }

    class ModVersion {
        private IModInfo owner;
        private final String modId;
        private final VersionRange versionRange;
        private final boolean mandatory;
        private final Ordering ordering;
        private final DependencySide side;

        public ModVersion(final IModInfo owner, final UnmodifiableConfig config) {
            this.owner = owner;
            this.modId = config.get("modId");
            this.versionRange = config.getOptional("versionRange").map(String.class::cast).
                    map(MavenVersionAdapter::createFromVersionSpec).orElse(UNBOUNDED);
            this.mandatory = config.get("mandatory");
            this.ordering = config.getOptional("ordering").map(String.class::cast).
                    map(Ordering::valueOf).orElse(Ordering.NONE);
            this.side = config.getOptional("side").map(String.class::cast).
                    map(DependencySide::valueOf).orElse(DependencySide.BOTH);
        }


        public String getModId()
        {
            return modId;
        }

        public VersionRange getVersionRange()
        {
            return versionRange;
        }

        public boolean isMandatory()
        {
            return mandatory;
        }

        public Ordering getOrdering()
        {
            return ordering;
        }

        public DependencySide getSide()
        {
            return side;
        }

        public void setOwner(final IModInfo owner)
        {
            this.owner = owner;
        }

        public IModInfo getOwner()
        {
            return owner;
        }
    }
}
