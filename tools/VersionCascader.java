///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.dom4j:dom4j:2.1.4
//DEPS info.picocli:picocli:4.7.5
//DEPS jaxen:jaxen:2.0.0

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(name = "VersionCascader", mixinStandardHelpOptions = true, version = "VersionCascader 1.0",
        description = "Cascading version updater for multi-module Maven projects")
public class VersionCascader implements Callable<Integer> {

    @Option(names = {"-p", "--path"}, description = "Path to the project root (default: current directory)")
    private String projectPath = ".";

    @Option(names = {"-d", "--dry-run"}, description = "Show what would change without applying")
    private boolean dryRun = false;

    @Option(names = {"-v", "--verbose"}, description = "Verbose output")
    private boolean verbose = false;

    private Map<String, ModuleInfo> modules = new HashMap<>();
    private Map<String, Set<String>> dependencyGraph = new HashMap<>();
    private Set<String> updatedModules = new HashSet<>();
    private int updatedModulesCount = 0;
    private int updatedDependenciesCount = 0;

    public static void main(String... args) {
        int exitCode = new CommandLine(new VersionCascader()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            Path projectRoot = Paths.get(projectPath).toAbsolutePath().normalize();
            
            // Check if directory exists
            if (!Files.exists(projectRoot)) {
                System.err.println("[ERROR] Directory does not exist: " + projectRoot);
                return 1;
            }
            
            if (!Files.isDirectory(projectRoot)) {
                System.err.println("[ERROR] Path is not a directory: " + projectRoot);
                return 1;
            }
            
            System.out.println("[INFO] Scanning project at: " + projectRoot);

            // Step 1: Scan all pom.xml files
            List<File> pomFiles = scanPomFiles(projectRoot);
            System.out.println("[INFO] Found " + pomFiles.size() + " modules");

            if (pomFiles.isEmpty()) {
                System.err.println("[ERROR] No pom.xml files found in the project");
                return 1;
            }

            // Step 2: Parse all POM files and build module registry
            System.out.println("[INFO] Building dependency graph...");
            for (File pomFile : pomFiles) {
                parseAndRegisterModule(pomFile);
            }

            // Step 3: Build dependency graph
            buildDependencyGraph();

            // Step 4: Detect version mismatches and update
            List<VersionMismatch> mismatches = detectVersionMismatches();
            
            if (mismatches.isEmpty()) {
                System.out.println("[INFO] No version mismatches found. All dependencies are up to date.");
                return 0;
            }

            System.out.println("[INFO]");
            System.out.println("[INFO] Detected version mismatches:");
            displayMismatches(mismatches);

            // Step 5: Perform cascading updates
            System.out.println("[INFO]");
            System.out.println("[INFO] Updating dependencies:");
            performCascadingUpdates(mismatches);

            // Step 6: Display summary
            System.out.println("[INFO]");
            System.out.println("[INFO] Summary: Updated " + updatedModulesCount + 
                             " modules, " + updatedDependenciesCount + " dependency references");
            
            if (dryRun) {
                System.out.println("[INFO] Dry-run mode: No changes were written to disk");
            }

            return 0;
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    private List<File> scanPomFiles(Path root) throws IOException {
        List<File> pomFiles = new ArrayList<>();
        Files.walk(root)
                .filter(path -> path.getFileName().toString().equals("pom.xml"))
                .filter(path -> !path.toString().contains("target")) // Skip build directories
                .forEach(path -> pomFiles.add(path.toFile()));
        return pomFiles;
    }

    private void parseAndRegisterModule(File pomFile) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(pomFile);
        Element root = document.getRootElement();

        String groupId = getElementText(root, "groupId");
        String artifactId = getElementText(root, "artifactId");
        String version = getElementText(root, "version");

        // Handle parent groupId/version inheritance
        if (groupId == null) {
            Element parent = root.element("parent");
            if (parent != null) {
                groupId = getElementText(parent, "groupId");
            }
        }
        
        if (version == null) {
            Element parent = root.element("parent");
            if (parent != null) {
                version = getElementText(parent, "version");
            }
        }

        if (artifactId == null) {
            System.err.println("[WARN] Skipping POM without artifactId: " + pomFile);
            return;
        }

        String moduleKey = groupId + ":" + artifactId;
        
        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.groupId = groupId;
        moduleInfo.artifactId = artifactId;
        moduleInfo.version = version;
        moduleInfo.pomFile = pomFile;
        moduleInfo.document = document;
        moduleInfo.dependencies = parseDependencies(root);

        modules.put(moduleKey, moduleInfo);

        if (verbose) {
            System.out.println("[DEBUG] Registered module: " + moduleKey + " v" + version);
        }
    }

    private String getElementText(Element parent, String elementName) {
        Element element = parent.element(elementName);
        return element != null ? element.getTextTrim() : null;
    }

    private List<DependencyInfo> parseDependencies(Element root) {
        List<DependencyInfo> dependencies = new ArrayList<>();
        Element dependenciesElement = root.element("dependencies");
        
        if (dependenciesElement != null) {
            List<Element> depElements = dependenciesElement.elements("dependency");
            for (Element dep : depElements) {
                String groupId = getElementText(dep, "groupId");
                String artifactId = getElementText(dep, "artifactId");
                String version = getElementText(dep, "version");

                // Skip dependencies without version (managed dependencies)
                if (version != null && !version.startsWith("${")) {
                    DependencyInfo depInfo = new DependencyInfo();
                    depInfo.groupId = groupId;
                    depInfo.artifactId = artifactId;
                    depInfo.version = version;
                    depInfo.element = dep;
                    dependencies.add(depInfo);
                }
            }
        }
        
        return dependencies;
    }

    private void buildDependencyGraph() {
        for (Map.Entry<String, ModuleInfo> entry : modules.entrySet()) {
            String moduleKey = entry.getKey();
            ModuleInfo moduleInfo = entry.getValue();

            for (DependencyInfo dep : moduleInfo.dependencies) {
                String depKey = dep.groupId + ":" + dep.artifactId;
                
                // Only track internal dependencies
                if (modules.containsKey(depKey)) {
                    dependencyGraph.computeIfAbsent(depKey, k -> new HashSet<>()).add(moduleKey);
                }
            }
        }
    }

    private List<VersionMismatch> detectVersionMismatches() {
        List<VersionMismatch> mismatches = new ArrayList<>();

        for (Map.Entry<String, ModuleInfo> entry : modules.entrySet()) {
            String moduleKey = entry.getKey();
            ModuleInfo moduleInfo = entry.getValue();
            String currentVersion = moduleInfo.version;

            // Find all modules that depend on this module
            for (Map.Entry<String, ModuleInfo> dependentEntry : modules.entrySet()) {
                ModuleInfo dependentModule = dependentEntry.getValue();
                
                for (DependencyInfo dep : dependentModule.dependencies) {
                    String depKey = dep.groupId + ":" + dep.artifactId;
                    
                    if (depKey.equals(moduleKey) && !dep.version.equals(currentVersion)) {
                        VersionMismatch mismatch = new VersionMismatch();
                        mismatch.moduleKey = moduleKey;
                        mismatch.currentVersion = currentVersion;
                        mismatch.oldVersion = dep.version;
                        mismatch.dependentModuleKey = dependentEntry.getKey();
                        mismatch.dependentModule = dependentModule;
                        mismatch.dependency = dep;
                        mismatches.add(mismatch);
                    }
                }
            }
        }

        return mismatches;
    }

    private void displayMismatches(List<VersionMismatch> mismatches) {
        Map<String, List<String>> mismatchMap = new HashMap<>();
        
        for (VersionMismatch mismatch : mismatches) {
            String key = mismatch.moduleKey + " " + mismatch.currentVersion + 
                        " (was " + mismatch.oldVersion + " in: ";
            mismatchMap.computeIfAbsent(key, k -> new ArrayList<>())
                      .add(modules.get(mismatch.dependentModuleKey).artifactId);
        }

        for (Map.Entry<String, List<String>> entry : mismatchMap.entrySet()) {
            System.out.println("[INFO]   " + entry.getKey() + 
                             String.join(", ", entry.getValue()) + ")");
        }
    }

    private void performCascadingUpdates(List<VersionMismatch> mismatches) throws Exception {
        Queue<String> updateQueue = new LinkedList<>();
        
        // First pass: Update direct mismatches
        for (VersionMismatch mismatch : mismatches) {
            updateDependencyVersion(mismatch);
            updateQueue.add(mismatch.dependentModuleKey);
        }

        // Cascade updates
        Set<String> processed = new HashSet<>();
        
        if (!updateQueue.isEmpty()) {
            System.out.println("[INFO]");
            System.out.println("[INFO] Cascade updates:");
        }

        while (!updateQueue.isEmpty()) {
            String moduleKey = updateQueue.poll();
            
            if (processed.contains(moduleKey)) {
                continue;
            }
            processed.add(moduleKey);

            // Get modules that depend on this updated module
            Set<String> dependents = dependencyGraph.get(moduleKey);
            if (dependents != null) {
                for (String dependentKey : dependents) {
                    ModuleInfo dependentModule = modules.get(dependentKey);
                    ModuleInfo updatedModule = modules.get(moduleKey);

                    // Check if the dependent module needs updating
                    for (DependencyInfo dep : dependentModule.dependencies) {
                        String depKey = dep.groupId + ":" + dep.artifactId;
                        
                        if (depKey.equals(moduleKey) && !dep.version.equals(updatedModule.version)) {
                            String oldVersion = dep.version;
                            String oldModuleVersion = dependentModule.version;
                            
                            updateDependencyInModule(dependentModule, dep, updatedModule.version);
                            
                            System.out.println("[INFO]   " + 
                                getRelativePath(dependentModule.pomFile) + ": " +
                                updatedModule.artifactId + " " + oldVersion + " → " + 
                                updatedModule.version);
                            
                            incrementPatchVersion(dependentModule, oldModuleVersion);
                            
                            updateQueue.add(dependentKey);
                            updatedDependenciesCount++;
                        }
                    }
                }
            }
        }

        // Write all changes to disk
        if (!dryRun) {
            for (String moduleKey : updatedModules) {
                ModuleInfo moduleInfo = modules.get(moduleKey);
                savePomFile(moduleInfo);
            }
        }
    }

    private void updateDependencyVersion(VersionMismatch mismatch) throws Exception {
        String oldModuleVersion = mismatch.dependentModule.version;
        
        updateDependencyInModule(mismatch.dependentModule, mismatch.dependency, 
                                mismatch.currentVersion);
        
        System.out.println("[INFO]   " + getRelativePath(mismatch.dependentModule.pomFile) + 
                         ": " + modules.get(mismatch.moduleKey).artifactId + 
                         " " + mismatch.oldVersion + " → " + mismatch.currentVersion);
        
        incrementPatchVersion(mismatch.dependentModule, oldModuleVersion);
        
        updatedDependenciesCount++;
    }

    private void updateDependencyInModule(ModuleInfo module, DependencyInfo dep, 
                                         String newVersion) {
        Element versionElement = dep.element.element("version");
        if (versionElement != null) {
            versionElement.setText(newVersion);
            dep.version = newVersion;
            updatedModules.add(module.groupId + ":" + module.artifactId);
        }
    }

    private void incrementPatchVersion(ModuleInfo module, String oldVersion) {
        String newVersion = incrementVersion(module.version);
        
        // Update version in the document
        Element root = module.document.getRootElement();
        Element versionElement = root.element("version");
        
        if (versionElement != null) {
            versionElement.setText(newVersion);
            module.version = newVersion;
            
            System.out.println("[INFO]   " + getRelativePath(module.pomFile) + 
                             ": version " + oldVersion + " → " + newVersion + 
                             " (patch increment)");
            
            updatedModulesCount++;
        }
    }

    private String incrementVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length >= 3) {
            try {
                int patch = Integer.parseInt(parts[2]);
                parts[2] = String.valueOf(patch + 1);
                return String.join(".", parts);
            } catch (NumberFormatException e) {
                // If patch is not a number, append .1
                return version + ".1";
            }
        } else if (parts.length == 2) {
            return version + ".1";
        } else {
            return version + ".0.1";
        }
    }

    private void savePomFile(ModuleInfo module) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setIndentSize(4);
        format.setEncoding("UTF-8");
        
        try (FileWriter writer = new FileWriter(module.pomFile)) {
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(module.document);
            xmlWriter.close();
        }
        
        if (verbose) {
            System.out.println("[DEBUG] Saved: " + module.pomFile);
        }
    }

    private String getRelativePath(File file) {
        Path projectRoot = Paths.get(projectPath).toAbsolutePath().normalize();
        Path filePath = file.toPath().toAbsolutePath().normalize();
        try {
            return projectRoot.relativize(filePath).toString();
        } catch (IllegalArgumentException e) {
            return file.getPath();
        }
    }

    // Data classes
    static class ModuleInfo {
        String groupId;
        String artifactId;
        String version;
        File pomFile;
        Document document;
        List<DependencyInfo> dependencies;
    }

    static class DependencyInfo {
        String groupId;
        String artifactId;
        String version;
        Element element;
    }

    static class VersionMismatch {
        String moduleKey;
        String currentVersion;
        String oldVersion;
        String dependentModuleKey;
        ModuleInfo dependentModule;
        DependencyInfo dependency;
    }
}
