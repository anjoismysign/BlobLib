package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final ComLibraryAccessors laccForComLibraryAccessors = new ComLibraryAccessors(owner);
    private final CommonsLibraryAccessors laccForCommonsLibraryAccessors = new CommonsLibraryAccessors(owner);
    private final DeLibraryAccessors laccForDeLibraryAccessors = new DeLibraryAccessors(owner);
    private final IoLibraryAccessors laccForIoLibraryAccessors = new IoLibraryAccessors(owner);
    private final LibsdisguisesLibraryAccessors laccForLibsdisguisesLibraryAccessors = new LibsdisguisesLibraryAccessors(owner);
    private final MeLibraryAccessors laccForMeLibraryAccessors = new MeLibraryAccessors(owner);
    private final NetLibraryAccessors laccForNetLibraryAccessors = new NetLibraryAccessors(owner);
    private final OrgLibraryAccessors laccForOrgLibraryAccessors = new OrgLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Group of libraries at <b>com</b>
     */
    public ComLibraryAccessors getCom() {
        return laccForComLibraryAccessors;
    }

    /**
     * Group of libraries at <b>commons</b>
     */
    public CommonsLibraryAccessors getCommons() {
        return laccForCommonsLibraryAccessors;
    }

    /**
     * Group of libraries at <b>de</b>
     */
    public DeLibraryAccessors getDe() {
        return laccForDeLibraryAccessors;
    }

    /**
     * Group of libraries at <b>io</b>
     */
    public IoLibraryAccessors getIo() {
        return laccForIoLibraryAccessors;
    }

    /**
     * Group of libraries at <b>libsdisguises</b>
     */
    public LibsdisguisesLibraryAccessors getLibsdisguises() {
        return laccForLibsdisguisesLibraryAccessors;
    }

    /**
     * Group of libraries at <b>me</b>
     */
    public MeLibraryAccessors getMe() {
        return laccForMeLibraryAccessors;
    }

    /**
     * Group of libraries at <b>net</b>
     */
    public NetLibraryAccessors getNet() {
        return laccForNetLibraryAccessors;
    }

    /**
     * Group of libraries at <b>org</b>
     */
    public OrgLibraryAccessors getOrg() {
        return laccForOrgLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class ComLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlLibraryAccessors laccForComFasterxmlLibraryAccessors = new ComFasterxmlLibraryAccessors(owner);
        private final ComGithubLibraryAccessors laccForComGithubLibraryAccessors = new ComGithubLibraryAccessors(owner);
        private final ComMojangLibraryAccessors laccForComMojangLibraryAccessors = new ComMojangLibraryAccessors(owner);
        private final ComSk89qLibraryAccessors laccForComSk89qLibraryAccessors = new ComSk89qLibraryAccessors(owner);

        public ComLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml</b>
         */
        public ComFasterxmlLibraryAccessors getFasterxml() {
            return laccForComFasterxmlLibraryAccessors;
        }

        /**
         * Group of libraries at <b>com.github</b>
         */
        public ComGithubLibraryAccessors getGithub() {
            return laccForComGithubLibraryAccessors;
        }

        /**
         * Group of libraries at <b>com.mojang</b>
         */
        public ComMojangLibraryAccessors getMojang() {
            return laccForComMojangLibraryAccessors;
        }

        /**
         * Group of libraries at <b>com.sk89q</b>
         */
        public ComSk89qLibraryAccessors getSk89q() {
            return laccForComSk89qLibraryAccessors;
        }

    }

    public static class ComFasterxmlLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonLibraryAccessors laccForComFasterxmlJacksonLibraryAccessors = new ComFasterxmlJacksonLibraryAccessors(owner);

        public ComFasterxmlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml.jackson</b>
         */
        public ComFasterxmlJacksonLibraryAccessors getJackson() {
            return laccForComFasterxmlJacksonLibraryAccessors;
        }

    }

    public static class ComFasterxmlJacksonLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonCoreLibraryAccessors laccForComFasterxmlJacksonCoreLibraryAccessors = new ComFasterxmlJacksonCoreLibraryAccessors(owner);
        private final ComFasterxmlJacksonDataformatLibraryAccessors laccForComFasterxmlJacksonDataformatLibraryAccessors = new ComFasterxmlJacksonDataformatLibraryAccessors(owner);

        public ComFasterxmlJacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml.jackson.core</b>
         */
        public ComFasterxmlJacksonCoreLibraryAccessors getCore() {
            return laccForComFasterxmlJacksonCoreLibraryAccessors;
        }

        /**
         * Group of libraries at <b>com.fasterxml.jackson.dataformat</b>
         */
        public ComFasterxmlJacksonDataformatLibraryAccessors getDataformat() {
            return laccForComFasterxmlJacksonDataformatLibraryAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonCoreJacksonLibraryAccessors laccForComFasterxmlJacksonCoreJacksonLibraryAccessors = new ComFasterxmlJacksonCoreJacksonLibraryAccessors(owner);

        public ComFasterxmlJacksonCoreLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml.jackson.core.jackson</b>
         */
        public ComFasterxmlJacksonCoreJacksonLibraryAccessors getJackson() {
            return laccForComFasterxmlJacksonCoreJacksonLibraryAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreJacksonLibraryAccessors extends SubDependencyFactory {

        public ComFasterxmlJacksonCoreJacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>databind</b> with <b>com.fasterxml.jackson.core:jackson-databind</b> coordinates and
         * with version reference <b>com.fasterxml.jackson.core.jackson.databind</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getDatabind() {
            return create("com.fasterxml.jackson.core.jackson.databind");
        }

    }

    public static class ComFasterxmlJacksonDataformatLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonDataformatJacksonLibraryAccessors laccForComFasterxmlJacksonDataformatJacksonLibraryAccessors = new ComFasterxmlJacksonDataformatJacksonLibraryAccessors(owner);

        public ComFasterxmlJacksonDataformatLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml.jackson.dataformat.jackson</b>
         */
        public ComFasterxmlJacksonDataformatJacksonLibraryAccessors getJackson() {
            return laccForComFasterxmlJacksonDataformatJacksonLibraryAccessors;
        }

    }

    public static class ComFasterxmlJacksonDataformatJacksonLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonDataformatJacksonDataformatLibraryAccessors laccForComFasterxmlJacksonDataformatJacksonDataformatLibraryAccessors = new ComFasterxmlJacksonDataformatJacksonDataformatLibraryAccessors(owner);

        public ComFasterxmlJacksonDataformatJacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml.jackson.dataformat.jackson.dataformat</b>
         */
        public ComFasterxmlJacksonDataformatJacksonDataformatLibraryAccessors getDataformat() {
            return laccForComFasterxmlJacksonDataformatJacksonDataformatLibraryAccessors;
        }

    }

    public static class ComFasterxmlJacksonDataformatJacksonDataformatLibraryAccessors extends SubDependencyFactory {

        public ComFasterxmlJacksonDataformatJacksonDataformatLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>yaml</b> with <b>com.fasterxml.jackson.dataformat:jackson-dataformat-yaml</b> coordinates and
         * with version reference <b>com.fasterxml.jackson.dataformat.jackson.dataformat.yaml</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getYaml() {
            return create("com.fasterxml.jackson.dataformat.jackson.dataformat.yaml");
        }

    }

    public static class ComGithubLibraryAccessors extends SubDependencyFactory {
        private final ComGithubDecentsoftwareLibraryAccessors laccForComGithubDecentsoftwareLibraryAccessors = new ComGithubDecentsoftwareLibraryAccessors(owner);

        public ComGithubLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.github.decentsoftware</b>
         */
        public ComGithubDecentsoftwareLibraryAccessors getDecentsoftware() {
            return laccForComGithubDecentsoftwareLibraryAccessors;
        }

    }

    public static class ComGithubDecentsoftwareLibraryAccessors extends SubDependencyFactory {
        private final ComGithubDecentsoftwareEuLibraryAccessors laccForComGithubDecentsoftwareEuLibraryAccessors = new ComGithubDecentsoftwareEuLibraryAccessors(owner);

        public ComGithubDecentsoftwareLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.github.decentsoftware.eu</b>
         */
        public ComGithubDecentsoftwareEuLibraryAccessors getEu() {
            return laccForComGithubDecentsoftwareEuLibraryAccessors;
        }

    }

    public static class ComGithubDecentsoftwareEuLibraryAccessors extends SubDependencyFactory {

        public ComGithubDecentsoftwareEuLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>decentholograms</b> with <b>com.github.decentsoftware-eu:decentholograms</b> coordinates and
         * with version reference <b>com.github.decentsoftware.eu.decentholograms</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getDecentholograms() {
            return create("com.github.decentsoftware.eu.decentholograms");
        }

    }

    public static class ComMojangLibraryAccessors extends SubDependencyFactory {

        public ComMojangLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>authlib</b> with <b>com.mojang:authlib</b> coordinates and
         * with version reference <b>com.mojang.authlib</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAuthlib() {
            return create("com.mojang.authlib");
        }

    }

    public static class ComSk89qLibraryAccessors extends SubDependencyFactory {
        private final ComSk89qWorldguardLibraryAccessors laccForComSk89qWorldguardLibraryAccessors = new ComSk89qWorldguardLibraryAccessors(owner);

        public ComSk89qLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.sk89q.worldguard</b>
         */
        public ComSk89qWorldguardLibraryAccessors getWorldguard() {
            return laccForComSk89qWorldguardLibraryAccessors;
        }

    }

    public static class ComSk89qWorldguardLibraryAccessors extends SubDependencyFactory {
        private final ComSk89qWorldguardWorldguardLibraryAccessors laccForComSk89qWorldguardWorldguardLibraryAccessors = new ComSk89qWorldguardWorldguardLibraryAccessors(owner);

        public ComSk89qWorldguardLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.sk89q.worldguard.worldguard</b>
         */
        public ComSk89qWorldguardWorldguardLibraryAccessors getWorldguard() {
            return laccForComSk89qWorldguardWorldguardLibraryAccessors;
        }

    }

    public static class ComSk89qWorldguardWorldguardLibraryAccessors extends SubDependencyFactory {

        public ComSk89qWorldguardWorldguardLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>bukkit</b> with <b>com.sk89q.worldguard:worldguard-bukkit</b> coordinates and
         * with version reference <b>com.sk89q.worldguard.worldguard.bukkit</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBukkit() {
            return create("com.sk89q.worldguard.worldguard.bukkit");
        }

    }

    public static class CommonsLibraryAccessors extends SubDependencyFactory {
        private final CommonsIoLibraryAccessors laccForCommonsIoLibraryAccessors = new CommonsIoLibraryAccessors(owner);

        public CommonsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>commons.io</b>
         */
        public CommonsIoLibraryAccessors getIo() {
            return laccForCommonsIoLibraryAccessors;
        }

    }

    public static class CommonsIoLibraryAccessors extends SubDependencyFactory {
        private final CommonsIoCommonsLibraryAccessors laccForCommonsIoCommonsLibraryAccessors = new CommonsIoCommonsLibraryAccessors(owner);

        public CommonsIoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>commons.io.commons</b>
         */
        public CommonsIoCommonsLibraryAccessors getCommons() {
            return laccForCommonsIoCommonsLibraryAccessors;
        }

    }

    public static class CommonsIoCommonsLibraryAccessors extends SubDependencyFactory {

        public CommonsIoCommonsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>io</b> with <b>commons-io:commons-io</b> coordinates and
         * with version reference <b>commons.io.commons.io</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getIo() {
            return create("commons.io.commons.io");
        }

    }

    public static class DeLibraryAccessors extends SubDependencyFactory {
        private final DeOliverLibraryAccessors laccForDeOliverLibraryAccessors = new DeOliverLibraryAccessors(owner);

        public DeLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>de.oliver</b>
         */
        public DeOliverLibraryAccessors getOliver() {
            return laccForDeOliverLibraryAccessors;
        }

    }

    public static class DeOliverLibraryAccessors extends SubDependencyFactory {

        public DeOliverLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>fancyholograms</b> with <b>de.oliver:FancyHolograms</b> coordinates and
         * with version reference <b>de.oliver.fancyholograms</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getFancyholograms() {
            return create("de.oliver.fancyholograms");
        }

    }

    public static class IoLibraryAccessors extends SubDependencyFactory {
        private final IoPapermcLibraryAccessors laccForIoPapermcLibraryAccessors = new IoPapermcLibraryAccessors(owner);

        public IoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.papermc</b>
         */
        public IoPapermcLibraryAccessors getPapermc() {
            return laccForIoPapermcLibraryAccessors;
        }

    }

    public static class IoPapermcLibraryAccessors extends SubDependencyFactory {
        private final IoPapermcPaperLibraryAccessors laccForIoPapermcPaperLibraryAccessors = new IoPapermcPaperLibraryAccessors(owner);

        public IoPapermcLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.papermc.paper</b>
         */
        public IoPapermcPaperLibraryAccessors getPaper() {
            return laccForIoPapermcPaperLibraryAccessors;
        }

    }

    public static class IoPapermcPaperLibraryAccessors extends SubDependencyFactory {
        private final IoPapermcPaperPaperLibraryAccessors laccForIoPapermcPaperPaperLibraryAccessors = new IoPapermcPaperPaperLibraryAccessors(owner);

        public IoPapermcPaperLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.papermc.paper.paper</b>
         */
        public IoPapermcPaperPaperLibraryAccessors getPaper() {
            return laccForIoPapermcPaperPaperLibraryAccessors;
        }

    }

    public static class IoPapermcPaperPaperLibraryAccessors extends SubDependencyFactory {

        public IoPapermcPaperPaperLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>api</b> with <b>io.papermc.paper:paper-api</b> coordinates and
         * with version reference <b>io.papermc.paper.paper.api</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getApi() {
            return create("io.papermc.paper.paper.api");
        }

    }

    public static class LibsdisguisesLibraryAccessors extends SubDependencyFactory {

        public LibsdisguisesLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>libsdisguises</b> with <b>LibsDisguises:LibsDisguises</b> coordinates and
         * with version reference <b>libsdisguises.libsdisguises</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLibsdisguises() {
            return create("libsdisguises.libsdisguises");
        }

    }

    public static class MeLibraryAccessors extends SubDependencyFactory {
        private final MeAnjoismysignLibraryAccessors laccForMeAnjoismysignLibraryAccessors = new MeAnjoismysignLibraryAccessors(owner);
        private final MeClipLibraryAccessors laccForMeClipLibraryAccessors = new MeClipLibraryAccessors(owner);

        public MeLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>me.anjoismysign</b>
         */
        public MeAnjoismysignLibraryAccessors getAnjoismysign() {
            return laccForMeAnjoismysignLibraryAccessors;
        }

        /**
         * Group of libraries at <b>me.clip</b>
         */
        public MeClipLibraryAccessors getClip() {
            return laccForMeClipLibraryAccessors;
        }

    }

    public static class MeAnjoismysignLibraryAccessors extends SubDependencyFactory {
        private final MeAnjoismysignAnjoLibraryAccessors laccForMeAnjoismysignAnjoLibraryAccessors = new MeAnjoismysignAnjoLibraryAccessors(owner);
        private final MeAnjoismysignPsaLibraryAccessors laccForMeAnjoismysignPsaLibraryAccessors = new MeAnjoismysignPsaLibraryAccessors(owner);

        public MeAnjoismysignLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>aesthetic</b> with <b>me.anjoismysign:aesthetic</b> coordinates and
         * with version reference <b>me.anjoismysign.aesthetic</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAesthetic() {
            return create("me.anjoismysign.aesthetic");
        }

        /**
         * Dependency provider for <b>holoworld</b> with <b>me.anjoismysign:holoworld</b> coordinates and
         * with version reference <b>me.anjoismysign.holoworld</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getHoloworld() {
            return create("me.anjoismysign.holoworld");
        }

        /**
         * Dependency provider for <b>manobukkit</b> with <b>me.anjoismysign:manobukkit</b> coordinates and
         * with version reference <b>me.anjoismysign.manobukkit</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getManobukkit() {
            return create("me.anjoismysign.manobukkit");
        }

        /**
         * Dependency provider for <b>skeramidcommands</b> with <b>me.anjoismysign:skeramidcommands</b> coordinates and
         * with version reference <b>me.anjoismysign.skeramidcommands</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSkeramidcommands() {
            return create("me.anjoismysign.skeramidcommands");
        }

        /**
         * Dependency provider for <b>winona</b> with <b>me.anjoismysign:winona</b> coordinates and
         * with version reference <b>me.anjoismysign.winona</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getWinona() {
            return create("me.anjoismysign.winona");
        }

        /**
         * Group of libraries at <b>me.anjoismysign.anjo</b>
         */
        public MeAnjoismysignAnjoLibraryAccessors getAnjo() {
            return laccForMeAnjoismysignAnjoLibraryAccessors;
        }

        /**
         * Group of libraries at <b>me.anjoismysign.psa</b>
         */
        public MeAnjoismysignPsaLibraryAccessors getPsa() {
            return laccForMeAnjoismysignPsaLibraryAccessors;
        }

    }

    public static class MeAnjoismysignAnjoLibraryAccessors extends SubDependencyFactory {

        public MeAnjoismysignAnjoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>anjo</b> with <b>me.anjoismysign.anjo:anjo</b> coordinates and
         * with version reference <b>me.anjoismysign.anjo.anjo</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAnjo() {
            return create("me.anjoismysign.anjo.anjo");
        }

    }

    public static class MeAnjoismysignPsaLibraryAccessors extends SubDependencyFactory {

        public MeAnjoismysignPsaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>api</b> with <b>me.anjoismysign:psa-api</b> coordinates and
         * with version reference <b>me.anjoismysign.psa.api</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getApi() {
            return create("me.anjoismysign.psa.api");
        }

    }

    public static class MeClipLibraryAccessors extends SubDependencyFactory {

        public MeClipLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>placeholderapi</b> with <b>me.clip:placeholderapi</b> coordinates and
         * with version reference <b>me.clip.placeholderapi</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPlaceholderapi() {
            return create("me.clip.placeholderapi");
        }

    }

    public static class NetLibraryAccessors extends SubDependencyFactory {
        private final NetLingalaLibraryAccessors laccForNetLingalaLibraryAccessors = new NetLingalaLibraryAccessors(owner);
        private final NetMdLibraryAccessors laccForNetMdLibraryAccessors = new NetMdLibraryAccessors(owner);
        private final NetMilkbowlLibraryAccessors laccForNetMilkbowlLibraryAccessors = new NetMilkbowlLibraryAccessors(owner);

        public NetLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>net.lingala</b>
         */
        public NetLingalaLibraryAccessors getLingala() {
            return laccForNetLingalaLibraryAccessors;
        }

        /**
         * Group of libraries at <b>net.md</b>
         */
        public NetMdLibraryAccessors getMd() {
            return laccForNetMdLibraryAccessors;
        }

        /**
         * Group of libraries at <b>net.milkbowl</b>
         */
        public NetMilkbowlLibraryAccessors getMilkbowl() {
            return laccForNetMilkbowlLibraryAccessors;
        }

    }

    public static class NetLingalaLibraryAccessors extends SubDependencyFactory {
        private final NetLingalaZip4jLibraryAccessors laccForNetLingalaZip4jLibraryAccessors = new NetLingalaZip4jLibraryAccessors(owner);

        public NetLingalaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>net.lingala.zip4j</b>
         */
        public NetLingalaZip4jLibraryAccessors getZip4j() {
            return laccForNetLingalaZip4jLibraryAccessors;
        }

    }

    public static class NetLingalaZip4jLibraryAccessors extends SubDependencyFactory {

        public NetLingalaZip4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>zip4j</b> with <b>net.lingala.zip4j:zip4j</b> coordinates and
         * with version reference <b>net.lingala.zip4j.zip4j</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getZip4j() {
            return create("net.lingala.zip4j.zip4j");
        }

    }

    public static class NetMdLibraryAccessors extends SubDependencyFactory {
        private final NetMdV5LibraryAccessors laccForNetMdV5LibraryAccessors = new NetMdV5LibraryAccessors(owner);

        public NetMdLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>net.md.v5</b>
         */
        public NetMdV5LibraryAccessors getV5() {
            return laccForNetMdV5LibraryAccessors;
        }

    }

    public static class NetMdV5LibraryAccessors extends SubDependencyFactory {
        private final NetMdV5BungeecordLibraryAccessors laccForNetMdV5BungeecordLibraryAccessors = new NetMdV5BungeecordLibraryAccessors(owner);

        public NetMdV5LibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>net.md.v5.bungeecord</b>
         */
        public NetMdV5BungeecordLibraryAccessors getBungeecord() {
            return laccForNetMdV5BungeecordLibraryAccessors;
        }

    }

    public static class NetMdV5BungeecordLibraryAccessors extends SubDependencyFactory {

        public NetMdV5BungeecordLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>api</b> with <b>net.md-5:bungeecord-api</b> coordinates and
         * with version reference <b>net.md.v5.bungeecord.api</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getApi() {
            return create("net.md.v5.bungeecord.api");
        }

    }

    public static class NetMilkbowlLibraryAccessors extends SubDependencyFactory {
        private final NetMilkbowlVaultLibraryAccessors laccForNetMilkbowlVaultLibraryAccessors = new NetMilkbowlVaultLibraryAccessors(owner);

        public NetMilkbowlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>net.milkbowl.vault</b>
         */
        public NetMilkbowlVaultLibraryAccessors getVault() {
            return laccForNetMilkbowlVaultLibraryAccessors;
        }

    }

    public static class NetMilkbowlVaultLibraryAccessors extends SubDependencyFactory {

        public NetMilkbowlVaultLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>vaultapi</b> with <b>net.milkbowl.vault:vaultapi</b> coordinates and
         * with version reference <b>net.milkbowl.vault.vaultapi</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getVaultapi() {
            return create("net.milkbowl.vault.vaultapi");
        }

    }

    public static class OrgLibraryAccessors extends SubDependencyFactory {
        private final OrgApacheLibraryAccessors laccForOrgApacheLibraryAccessors = new OrgApacheLibraryAccessors(owner);
        private final OrgMongodbLibraryAccessors laccForOrgMongodbLibraryAccessors = new OrgMongodbLibraryAccessors(owner);

        public OrgLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.apache</b>
         */
        public OrgApacheLibraryAccessors getApache() {
            return laccForOrgApacheLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.mongodb</b>
         */
        public OrgMongodbLibraryAccessors getMongodb() {
            return laccForOrgMongodbLibraryAccessors;
        }

    }

    public static class OrgApacheLibraryAccessors extends SubDependencyFactory {
        private final OrgApacheCommonsLibraryAccessors laccForOrgApacheCommonsLibraryAccessors = new OrgApacheCommonsLibraryAccessors(owner);

        public OrgApacheLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.apache.commons</b>
         */
        public OrgApacheCommonsLibraryAccessors getCommons() {
            return laccForOrgApacheCommonsLibraryAccessors;
        }

    }

    public static class OrgApacheCommonsLibraryAccessors extends SubDependencyFactory {
        private final OrgApacheCommonsCommonsLibraryAccessors laccForOrgApacheCommonsCommonsLibraryAccessors = new OrgApacheCommonsCommonsLibraryAccessors(owner);

        public OrgApacheCommonsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.apache.commons.commons</b>
         */
        public OrgApacheCommonsCommonsLibraryAccessors getCommons() {
            return laccForOrgApacheCommonsCommonsLibraryAccessors;
        }

    }

    public static class OrgApacheCommonsCommonsLibraryAccessors extends SubDependencyFactory {

        public OrgApacheCommonsCommonsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>lang3</b> with <b>org.apache.commons:commons-lang3</b> coordinates and
         * with version reference <b>org.apache.commons.commons.lang3</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLang3() {
            return create("org.apache.commons.commons.lang3");
        }

    }

    public static class OrgMongodbLibraryAccessors extends SubDependencyFactory {
        private final OrgMongodbMongodbLibraryAccessors laccForOrgMongodbMongodbLibraryAccessors = new OrgMongodbMongodbLibraryAccessors(owner);

        public OrgMongodbLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>bson</b> with <b>org.mongodb:bson</b> coordinates and
         * with version reference <b>org.mongodb.bson</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBson() {
            return create("org.mongodb.bson");
        }

        /**
         * Group of libraries at <b>org.mongodb.mongodb</b>
         */
        public OrgMongodbMongodbLibraryAccessors getMongodb() {
            return laccForOrgMongodbMongodbLibraryAccessors;
        }

    }

    public static class OrgMongodbMongodbLibraryAccessors extends SubDependencyFactory {
        private final OrgMongodbMongodbDriverLibraryAccessors laccForOrgMongodbMongodbDriverLibraryAccessors = new OrgMongodbMongodbDriverLibraryAccessors(owner);

        public OrgMongodbMongodbLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.mongodb.mongodb.driver</b>
         */
        public OrgMongodbMongodbDriverLibraryAccessors getDriver() {
            return laccForOrgMongodbMongodbDriverLibraryAccessors;
        }

    }

    public static class OrgMongodbMongodbDriverLibraryAccessors extends SubDependencyFactory {

        public OrgMongodbMongodbDriverLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>sync</b> with <b>org.mongodb:mongodb-driver-sync</b> coordinates and
         * with version reference <b>org.mongodb.mongodb.driver.sync</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSync() {
            return create("org.mongodb.mongodb.driver.sync");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final ComVersionAccessors vaccForComVersionAccessors = new ComVersionAccessors(providers, config);
        private final CommonsVersionAccessors vaccForCommonsVersionAccessors = new CommonsVersionAccessors(providers, config);
        private final DeVersionAccessors vaccForDeVersionAccessors = new DeVersionAccessors(providers, config);
        private final IoVersionAccessors vaccForIoVersionAccessors = new IoVersionAccessors(providers, config);
        private final LibsdisguisesVersionAccessors vaccForLibsdisguisesVersionAccessors = new LibsdisguisesVersionAccessors(providers, config);
        private final MeVersionAccessors vaccForMeVersionAccessors = new MeVersionAccessors(providers, config);
        private final NetVersionAccessors vaccForNetVersionAccessors = new NetVersionAccessors(providers, config);
        private final OrgVersionAccessors vaccForOrgVersionAccessors = new OrgVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com</b>
         */
        public ComVersionAccessors getCom() {
            return vaccForComVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.commons</b>
         */
        public CommonsVersionAccessors getCommons() {
            return vaccForCommonsVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.de</b>
         */
        public DeVersionAccessors getDe() {
            return vaccForDeVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.io</b>
         */
        public IoVersionAccessors getIo() {
            return vaccForIoVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.libsdisguises</b>
         */
        public LibsdisguisesVersionAccessors getLibsdisguises() {
            return vaccForLibsdisguisesVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.me</b>
         */
        public MeVersionAccessors getMe() {
            return vaccForMeVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.net</b>
         */
        public NetVersionAccessors getNet() {
            return vaccForNetVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org</b>
         */
        public OrgVersionAccessors getOrg() {
            return vaccForOrgVersionAccessors;
        }

    }

    public static class ComVersionAccessors extends VersionFactory  {

        private final ComFasterxmlVersionAccessors vaccForComFasterxmlVersionAccessors = new ComFasterxmlVersionAccessors(providers, config);
        private final ComGithubVersionAccessors vaccForComGithubVersionAccessors = new ComGithubVersionAccessors(providers, config);
        private final ComMojangVersionAccessors vaccForComMojangVersionAccessors = new ComMojangVersionAccessors(providers, config);
        private final ComSk89qVersionAccessors vaccForComSk89qVersionAccessors = new ComSk89qVersionAccessors(providers, config);
        public ComVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml</b>
         */
        public ComFasterxmlVersionAccessors getFasterxml() {
            return vaccForComFasterxmlVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com.github</b>
         */
        public ComGithubVersionAccessors getGithub() {
            return vaccForComGithubVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com.mojang</b>
         */
        public ComMojangVersionAccessors getMojang() {
            return vaccForComMojangVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com.sk89q</b>
         */
        public ComSk89qVersionAccessors getSk89q() {
            return vaccForComSk89qVersionAccessors;
        }

    }

    public static class ComFasterxmlVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonVersionAccessors vaccForComFasterxmlJacksonVersionAccessors = new ComFasterxmlJacksonVersionAccessors(providers, config);
        public ComFasterxmlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson</b>
         */
        public ComFasterxmlJacksonVersionAccessors getJackson() {
            return vaccForComFasterxmlJacksonVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonCoreVersionAccessors vaccForComFasterxmlJacksonCoreVersionAccessors = new ComFasterxmlJacksonCoreVersionAccessors(providers, config);
        private final ComFasterxmlJacksonDataformatVersionAccessors vaccForComFasterxmlJacksonDataformatVersionAccessors = new ComFasterxmlJacksonDataformatVersionAccessors(providers, config);
        public ComFasterxmlJacksonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson.core</b>
         */
        public ComFasterxmlJacksonCoreVersionAccessors getCore() {
            return vaccForComFasterxmlJacksonCoreVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson.dataformat</b>
         */
        public ComFasterxmlJacksonDataformatVersionAccessors getDataformat() {
            return vaccForComFasterxmlJacksonDataformatVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonCoreJacksonVersionAccessors vaccForComFasterxmlJacksonCoreJacksonVersionAccessors = new ComFasterxmlJacksonCoreJacksonVersionAccessors(providers, config);
        public ComFasterxmlJacksonCoreVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson.core.jackson</b>
         */
        public ComFasterxmlJacksonCoreJacksonVersionAccessors getJackson() {
            return vaccForComFasterxmlJacksonCoreJacksonVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreJacksonVersionAccessors extends VersionFactory  {

        public ComFasterxmlJacksonCoreJacksonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.fasterxml.jackson.core.jackson.databind</b> with value <b>2.15.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getDatabind() { return getVersion("com.fasterxml.jackson.core.jackson.databind"); }

    }

    public static class ComFasterxmlJacksonDataformatVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonDataformatJacksonVersionAccessors vaccForComFasterxmlJacksonDataformatJacksonVersionAccessors = new ComFasterxmlJacksonDataformatJacksonVersionAccessors(providers, config);
        public ComFasterxmlJacksonDataformatVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson.dataformat.jackson</b>
         */
        public ComFasterxmlJacksonDataformatJacksonVersionAccessors getJackson() {
            return vaccForComFasterxmlJacksonDataformatJacksonVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonDataformatJacksonVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonDataformatJacksonDataformatVersionAccessors vaccForComFasterxmlJacksonDataformatJacksonDataformatVersionAccessors = new ComFasterxmlJacksonDataformatJacksonDataformatVersionAccessors(providers, config);
        public ComFasterxmlJacksonDataformatJacksonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson.dataformat.jackson.dataformat</b>
         */
        public ComFasterxmlJacksonDataformatJacksonDataformatVersionAccessors getDataformat() {
            return vaccForComFasterxmlJacksonDataformatJacksonDataformatVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonDataformatJacksonDataformatVersionAccessors extends VersionFactory  {

        public ComFasterxmlJacksonDataformatJacksonDataformatVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.fasterxml.jackson.dataformat.jackson.dataformat.yaml</b> with value <b>2.15.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getYaml() { return getVersion("com.fasterxml.jackson.dataformat.jackson.dataformat.yaml"); }

    }

    public static class ComGithubVersionAccessors extends VersionFactory  {

        private final ComGithubDecentsoftwareVersionAccessors vaccForComGithubDecentsoftwareVersionAccessors = new ComGithubDecentsoftwareVersionAccessors(providers, config);
        public ComGithubVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.github.decentsoftware</b>
         */
        public ComGithubDecentsoftwareVersionAccessors getDecentsoftware() {
            return vaccForComGithubDecentsoftwareVersionAccessors;
        }

    }

    public static class ComGithubDecentsoftwareVersionAccessors extends VersionFactory  {

        private final ComGithubDecentsoftwareEuVersionAccessors vaccForComGithubDecentsoftwareEuVersionAccessors = new ComGithubDecentsoftwareEuVersionAccessors(providers, config);
        public ComGithubDecentsoftwareVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.github.decentsoftware.eu</b>
         */
        public ComGithubDecentsoftwareEuVersionAccessors getEu() {
            return vaccForComGithubDecentsoftwareEuVersionAccessors;
        }

    }

    public static class ComGithubDecentsoftwareEuVersionAccessors extends VersionFactory  {

        public ComGithubDecentsoftwareEuVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.github.decentsoftware.eu.decentholograms</b> with value <b>2.8.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getDecentholograms() { return getVersion("com.github.decentsoftware.eu.decentholograms"); }

    }

    public static class ComMojangVersionAccessors extends VersionFactory  {

        public ComMojangVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.mojang.authlib</b> with value <b>1.5.21</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAuthlib() { return getVersion("com.mojang.authlib"); }

    }

    public static class ComSk89qVersionAccessors extends VersionFactory  {

        private final ComSk89qWorldguardVersionAccessors vaccForComSk89qWorldguardVersionAccessors = new ComSk89qWorldguardVersionAccessors(providers, config);
        public ComSk89qVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.sk89q.worldguard</b>
         */
        public ComSk89qWorldguardVersionAccessors getWorldguard() {
            return vaccForComSk89qWorldguardVersionAccessors;
        }

    }

    public static class ComSk89qWorldguardVersionAccessors extends VersionFactory  {

        private final ComSk89qWorldguardWorldguardVersionAccessors vaccForComSk89qWorldguardWorldguardVersionAccessors = new ComSk89qWorldguardWorldguardVersionAccessors(providers, config);
        public ComSk89qWorldguardVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.sk89q.worldguard.worldguard</b>
         */
        public ComSk89qWorldguardWorldguardVersionAccessors getWorldguard() {
            return vaccForComSk89qWorldguardWorldguardVersionAccessors;
        }

    }

    public static class ComSk89qWorldguardWorldguardVersionAccessors extends VersionFactory  {

        public ComSk89qWorldguardWorldguardVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.sk89q.worldguard.worldguard.bukkit</b> with value <b>7.0.8-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBukkit() { return getVersion("com.sk89q.worldguard.worldguard.bukkit"); }

    }

    public static class CommonsVersionAccessors extends VersionFactory  {

        private final CommonsIoVersionAccessors vaccForCommonsIoVersionAccessors = new CommonsIoVersionAccessors(providers, config);
        public CommonsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.commons.io</b>
         */
        public CommonsIoVersionAccessors getIo() {
            return vaccForCommonsIoVersionAccessors;
        }

    }

    public static class CommonsIoVersionAccessors extends VersionFactory  {

        private final CommonsIoCommonsVersionAccessors vaccForCommonsIoCommonsVersionAccessors = new CommonsIoCommonsVersionAccessors(providers, config);
        public CommonsIoVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.commons.io.commons</b>
         */
        public CommonsIoCommonsVersionAccessors getCommons() {
            return vaccForCommonsIoCommonsVersionAccessors;
        }

    }

    public static class CommonsIoCommonsVersionAccessors extends VersionFactory  {

        public CommonsIoCommonsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>commons.io.commons.io</b> with value <b>2.7</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getIo() { return getVersion("commons.io.commons.io"); }

    }

    public static class DeVersionAccessors extends VersionFactory  {

        private final DeOliverVersionAccessors vaccForDeOliverVersionAccessors = new DeOliverVersionAccessors(providers, config);
        public DeVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.de.oliver</b>
         */
        public DeOliverVersionAccessors getOliver() {
            return vaccForDeOliverVersionAccessors;
        }

    }

    public static class DeOliverVersionAccessors extends VersionFactory  {

        public DeOliverVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>de.oliver.fancyholograms</b> with value <b>2.3.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFancyholograms() { return getVersion("de.oliver.fancyholograms"); }

    }

    public static class IoVersionAccessors extends VersionFactory  {

        private final IoPapermcVersionAccessors vaccForIoPapermcVersionAccessors = new IoPapermcVersionAccessors(providers, config);
        public IoVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.papermc</b>
         */
        public IoPapermcVersionAccessors getPapermc() {
            return vaccForIoPapermcVersionAccessors;
        }

    }

    public static class IoPapermcVersionAccessors extends VersionFactory  {

        private final IoPapermcPaperVersionAccessors vaccForIoPapermcPaperVersionAccessors = new IoPapermcPaperVersionAccessors(providers, config);
        public IoPapermcVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.papermc.paper</b>
         */
        public IoPapermcPaperVersionAccessors getPaper() {
            return vaccForIoPapermcPaperVersionAccessors;
        }

    }

    public static class IoPapermcPaperVersionAccessors extends VersionFactory  {

        private final IoPapermcPaperPaperVersionAccessors vaccForIoPapermcPaperPaperVersionAccessors = new IoPapermcPaperPaperVersionAccessors(providers, config);
        public IoPapermcPaperVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.papermc.paper.paper</b>
         */
        public IoPapermcPaperPaperVersionAccessors getPaper() {
            return vaccForIoPapermcPaperPaperVersionAccessors;
        }

    }

    public static class IoPapermcPaperPaperVersionAccessors extends VersionFactory  {

        public IoPapermcPaperPaperVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>io.papermc.paper.paper.api</b> with value <b>1.21.4-R0.1-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getApi() { return getVersion("io.papermc.paper.paper.api"); }

    }

    public static class LibsdisguisesVersionAccessors extends VersionFactory  {

        public LibsdisguisesVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>libsdisguises.libsdisguises</b> with value <b>10.0.29</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLibsdisguises() { return getVersion("libsdisguises.libsdisguises"); }

    }

    public static class MeVersionAccessors extends VersionFactory  {

        private final MeAnjoismysignVersionAccessors vaccForMeAnjoismysignVersionAccessors = new MeAnjoismysignVersionAccessors(providers, config);
        private final MeClipVersionAccessors vaccForMeClipVersionAccessors = new MeClipVersionAccessors(providers, config);
        public MeVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.me.anjoismysign</b>
         */
        public MeAnjoismysignVersionAccessors getAnjoismysign() {
            return vaccForMeAnjoismysignVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.me.clip</b>
         */
        public MeClipVersionAccessors getClip() {
            return vaccForMeClipVersionAccessors;
        }

    }

    public static class MeAnjoismysignVersionAccessors extends VersionFactory  {

        private final MeAnjoismysignAnjoVersionAccessors vaccForMeAnjoismysignAnjoVersionAccessors = new MeAnjoismysignAnjoVersionAccessors(providers, config);
        private final MeAnjoismysignPsaVersionAccessors vaccForMeAnjoismysignPsaVersionAccessors = new MeAnjoismysignPsaVersionAccessors(providers, config);
        public MeAnjoismysignVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>me.anjoismysign.aesthetic</b> with value <b>1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAesthetic() { return getVersion("me.anjoismysign.aesthetic"); }

        /**
         * Version alias <b>me.anjoismysign.holoworld</b> with value <b>1.0-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getHoloworld() { return getVersion("me.anjoismysign.holoworld"); }

        /**
         * Version alias <b>me.anjoismysign.manobukkit</b> with value <b>1.0.5d</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getManobukkit() { return getVersion("me.anjoismysign.manobukkit"); }

        /**
         * Version alias <b>me.anjoismysign.skeramidcommands</b> with value <b>1.0.6</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSkeramidcommands() { return getVersion("me.anjoismysign.skeramidcommands"); }

        /**
         * Version alias <b>me.anjoismysign.winona</b> with value <b>1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getWinona() { return getVersion("me.anjoismysign.winona"); }

        /**
         * Group of versions at <b>versions.me.anjoismysign.anjo</b>
         */
        public MeAnjoismysignAnjoVersionAccessors getAnjo() {
            return vaccForMeAnjoismysignAnjoVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.me.anjoismysign.psa</b>
         */
        public MeAnjoismysignPsaVersionAccessors getPsa() {
            return vaccForMeAnjoismysignPsaVersionAccessors;
        }

    }

    public static class MeAnjoismysignAnjoVersionAccessors extends VersionFactory  {

        public MeAnjoismysignAnjoVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>me.anjoismysign.anjo.anjo</b> with value <b>0.3.14</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAnjo() { return getVersion("me.anjoismysign.anjo.anjo"); }

    }

    public static class MeAnjoismysignPsaVersionAccessors extends VersionFactory  {

        public MeAnjoismysignPsaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>me.anjoismysign.psa.api</b> with value <b>1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getApi() { return getVersion("me.anjoismysign.psa.api"); }

    }

    public static class MeClipVersionAccessors extends VersionFactory  {

        public MeClipVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>me.clip.placeholderapi</b> with value <b>2.11.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPlaceholderapi() { return getVersion("me.clip.placeholderapi"); }

    }

    public static class NetVersionAccessors extends VersionFactory  {

        private final NetLingalaVersionAccessors vaccForNetLingalaVersionAccessors = new NetLingalaVersionAccessors(providers, config);
        private final NetMdVersionAccessors vaccForNetMdVersionAccessors = new NetMdVersionAccessors(providers, config);
        private final NetMilkbowlVersionAccessors vaccForNetMilkbowlVersionAccessors = new NetMilkbowlVersionAccessors(providers, config);
        public NetVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.net.lingala</b>
         */
        public NetLingalaVersionAccessors getLingala() {
            return vaccForNetLingalaVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.net.md</b>
         */
        public NetMdVersionAccessors getMd() {
            return vaccForNetMdVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.net.milkbowl</b>
         */
        public NetMilkbowlVersionAccessors getMilkbowl() {
            return vaccForNetMilkbowlVersionAccessors;
        }

    }

    public static class NetLingalaVersionAccessors extends VersionFactory  {

        private final NetLingalaZip4jVersionAccessors vaccForNetLingalaZip4jVersionAccessors = new NetLingalaZip4jVersionAccessors(providers, config);
        public NetLingalaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.net.lingala.zip4j</b>
         */
        public NetLingalaZip4jVersionAccessors getZip4j() {
            return vaccForNetLingalaZip4jVersionAccessors;
        }

    }

    public static class NetLingalaZip4jVersionAccessors extends VersionFactory  {

        public NetLingalaZip4jVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>net.lingala.zip4j.zip4j</b> with value <b>2.11.5</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getZip4j() { return getVersion("net.lingala.zip4j.zip4j"); }

    }

    public static class NetMdVersionAccessors extends VersionFactory  {

        private final NetMdV5VersionAccessors vaccForNetMdV5VersionAccessors = new NetMdV5VersionAccessors(providers, config);
        public NetMdVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.net.md.v5</b>
         */
        public NetMdV5VersionAccessors getV5() {
            return vaccForNetMdV5VersionAccessors;
        }

    }

    public static class NetMdV5VersionAccessors extends VersionFactory  {

        private final NetMdV5BungeecordVersionAccessors vaccForNetMdV5BungeecordVersionAccessors = new NetMdV5BungeecordVersionAccessors(providers, config);
        public NetMdV5VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.net.md.v5.bungeecord</b>
         */
        public NetMdV5BungeecordVersionAccessors getBungeecord() {
            return vaccForNetMdV5BungeecordVersionAccessors;
        }

    }

    public static class NetMdV5BungeecordVersionAccessors extends VersionFactory  {

        public NetMdV5BungeecordVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>net.md.v5.bungeecord.api</b> with value <b>1.20-R0.2-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getApi() { return getVersion("net.md.v5.bungeecord.api"); }

    }

    public static class NetMilkbowlVersionAccessors extends VersionFactory  {

        private final NetMilkbowlVaultVersionAccessors vaccForNetMilkbowlVaultVersionAccessors = new NetMilkbowlVaultVersionAccessors(providers, config);
        public NetMilkbowlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.net.milkbowl.vault</b>
         */
        public NetMilkbowlVaultVersionAccessors getVault() {
            return vaccForNetMilkbowlVaultVersionAccessors;
        }

    }

    public static class NetMilkbowlVaultVersionAccessors extends VersionFactory  {

        public NetMilkbowlVaultVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>net.milkbowl.vault.vaultapi</b> with value <b>1.7.6</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getVaultapi() { return getVersion("net.milkbowl.vault.vaultapi"); }

    }

    public static class OrgVersionAccessors extends VersionFactory  {

        private final OrgApacheVersionAccessors vaccForOrgApacheVersionAccessors = new OrgApacheVersionAccessors(providers, config);
        private final OrgMongodbVersionAccessors vaccForOrgMongodbVersionAccessors = new OrgMongodbVersionAccessors(providers, config);
        public OrgVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.apache</b>
         */
        public OrgApacheVersionAccessors getApache() {
            return vaccForOrgApacheVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.mongodb</b>
         */
        public OrgMongodbVersionAccessors getMongodb() {
            return vaccForOrgMongodbVersionAccessors;
        }

    }

    public static class OrgApacheVersionAccessors extends VersionFactory  {

        private final OrgApacheCommonsVersionAccessors vaccForOrgApacheCommonsVersionAccessors = new OrgApacheCommonsVersionAccessors(providers, config);
        public OrgApacheVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.apache.commons</b>
         */
        public OrgApacheCommonsVersionAccessors getCommons() {
            return vaccForOrgApacheCommonsVersionAccessors;
        }

    }

    public static class OrgApacheCommonsVersionAccessors extends VersionFactory  {

        private final OrgApacheCommonsCommonsVersionAccessors vaccForOrgApacheCommonsCommonsVersionAccessors = new OrgApacheCommonsCommonsVersionAccessors(providers, config);
        public OrgApacheCommonsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.apache.commons.commons</b>
         */
        public OrgApacheCommonsCommonsVersionAccessors getCommons() {
            return vaccForOrgApacheCommonsCommonsVersionAccessors;
        }

    }

    public static class OrgApacheCommonsCommonsVersionAccessors extends VersionFactory  {

        public OrgApacheCommonsCommonsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.apache.commons.commons.lang3</b> with value <b>3.17.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLang3() { return getVersion("org.apache.commons.commons.lang3"); }

    }

    public static class OrgMongodbVersionAccessors extends VersionFactory  {

        private final OrgMongodbMongodbVersionAccessors vaccForOrgMongodbMongodbVersionAccessors = new OrgMongodbMongodbVersionAccessors(providers, config);
        public OrgMongodbVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.mongodb.bson</b> with value <b>4.10.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBson() { return getVersion("org.mongodb.bson"); }

        /**
         * Group of versions at <b>versions.org.mongodb.mongodb</b>
         */
        public OrgMongodbMongodbVersionAccessors getMongodb() {
            return vaccForOrgMongodbMongodbVersionAccessors;
        }

    }

    public static class OrgMongodbMongodbVersionAccessors extends VersionFactory  {

        private final OrgMongodbMongodbDriverVersionAccessors vaccForOrgMongodbMongodbDriverVersionAccessors = new OrgMongodbMongodbDriverVersionAccessors(providers, config);
        public OrgMongodbMongodbVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.mongodb.mongodb.driver</b>
         */
        public OrgMongodbMongodbDriverVersionAccessors getDriver() {
            return vaccForOrgMongodbMongodbDriverVersionAccessors;
        }

    }

    public static class OrgMongodbMongodbDriverVersionAccessors extends VersionFactory  {

        public OrgMongodbMongodbDriverVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.mongodb.mongodb.driver.sync</b> with value <b>4.10.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSync() { return getVersion("org.mongodb.mongodb.driver.sync"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
