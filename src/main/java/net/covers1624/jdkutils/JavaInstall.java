package net.covers1624.jdkutils;

import net.covers1624.quack.platform.Architecture;
import net.covers1624.quack.platform.OperatingSystem;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Defines properties and helpers for specific Java installations.
 * <p>
 * A {@link JavaInstall} can be extracted form a specific known Java installation using
 * the {@link JavaLocator#parseInstall(Path)}.
 * <p>
 * One can extract all known installed Java installations off a given System using {@link JavaLocator} and the
 * associated Builder.
 * <p>
 * Concepts:<br/>
 * Installation Directory - A Java installation directory refers to the root directory of the java installation.
 * This directory usually has the java version within its name. However, is not guaranteed.
 * <p>
 * Java Home - Java home in this context is defined as the location in which the <code>bin</code> folder
 * containing Java executables can be found. On Linux and Windows, this is generally the same as the
 * Installation Directory, However on macOs this is usually the <code>Contents/Home/</code> folder within
 * the Installation Directory.
 * <p>
 * Created by covers1624 on 30/10/21.
 */
public class JavaInstall {

    public final JavaVersion langVersion;
    public final Path javaHome;
    public final String vendor;
    public final String implName;
    public final String implVersion;
    public final String runtimeName;
    public final String runtimeVersion;
    public final Architecture architecture;
    public final boolean isOpenJ9;
    public final boolean hasCompiler;

    public JavaInstall(Path javaHome, String vendor, String implName, String implVersion, String runtimeName, String runtimeVersion, Architecture architecture) {
        langVersion = requireNonNull(JavaVersion.parse(implVersion), "Unable to parse java version: " + implVersion);
        this.javaHome = javaHome;
        this.vendor = vendor;
        this.implName = implName;
        this.implVersion = implVersion;
        this.runtimeName = runtimeName;
        this.runtimeVersion = runtimeVersion;
        this.architecture = architecture;
        isOpenJ9 = implName.contains("J9");
        // If the installation has javac, It's highly likely it's a full JDK.
        hasCompiler = Files.exists(getExecutable(javaHome, "javac"));
    }

    /**
     * Gets the bin directory for a given java installation.
     *
     * @param installationDir The installation directory.
     * @return The bin directory.
     */
    public static Path getBinDirectory(Path installationDir) {
        return getHomeDirectory(installationDir).resolve("bin");
    }

    /**
     * Gets the Java home directory from a specific Installation.
     * This method uses the detected currently running Operating System to
     * determine how to transform the given Installation Directory into a
     * Java Home Directory and is not stable across Operating Systems.
     *
     * @param installationDir The installation directory.
     * @return The Java home.
     */
    public static Path getHomeDirectory(Path installationDir) {
        if (OperatingSystem.current().isMacos()) {
            return installationDir.resolve("Contents/Home");
        }
        return installationDir;
    }

    /**
     * Gets the Java executable for a given home directory.
     *
     * @param homeDir  The java home directory.
     * @param useJavaw If <code>javaw</code> should be used on Windows instead of <code>java</code>.
     * @return The executable.
     * @see JavaInstall Root javadoc contains definitions for <code>homeDir</code>
     */
    public static Path getJavaExecutable(Path homeDir, boolean useJavaw) {
        OperatingSystem os = OperatingSystem.current();
        return getExecutable(homeDir, os.isWindows() && useJavaw ? "javaw" : "java");
    }

    /**
     * Gets the given executable within the given java home directory.
     *
     * @param homeDir    The home directory.
     * @param executable The executable name.
     * @return The executable path.
     */
    public static Path getExecutable(Path homeDir, String executable) {
        return homeDir.resolve("bin").resolve(OperatingSystem.current().exeSuffix(executable));
    }

    @Override
    public String toString() {
        return "JavaInstall{" +
                "langVersion=" + langVersion +
                ", javaHome=" + javaHome +
                ", vendor='" + vendor + '\'' +
                ", implName='" + implName + '\'' +
                ", implVersion='" + implVersion + '\'' +
                ", runtimeName='" + runtimeName + '\'' +
                ", runtimeVersion='" + runtimeVersion + '\'' +
                ", architecture=" + architecture +
                ", isOpenJ9=" + isOpenJ9 +
                ", hasCompiler=" + hasCompiler +
                '}';
    }
}
