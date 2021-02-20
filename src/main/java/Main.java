import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {

    private boolean inWrapperFolder = false;
    private boolean inCachesFolder = false;
    private boolean inDistsFolder = false;
    private boolean inJdksFolder = false;

    List<String> distributions = new ArrayList<>();
    List<String> jdks = new ArrayList<>();

    public static void main(String[] args) {

        Main main = new Main();

        String cachesFolder, gradleRepositoryFolder;

        //TODO : Move to CMD argument utils, use interface for 0,1,2 argument actions, use exit_status & exit_message pair return
		
        /*
        Checking for commandline arguments
        First : Path of Gradle Repository Folder
        Second : Path of Caches Folder
        */
        if (args.length == 1) {

            gradleRepositoryFolder = args[0];
            cachesFolder = "C:\\Caches_C";

        } else if (args.length == 2) {

            gradleRepositoryFolder = args[0];
            cachesFolder = args[1];

        } else {

            gradleRepositoryFolder = "C:\\Programs\\gradle_repository";
            cachesFolder = "C:\\Caches_C";
        }

        System.out.println("Gradle Repository Folder : " + gradleRepositoryFolder);
        System.out.println("Caches Folder : " + cachesFolder);

        //TODO : Expand for whole gradle cache repository, if gradle is supported
        final File folder = new File(gradleRepositoryFolder);
        main.listFilesForFolder(folder, cachesFolder);
    }

    //TODO : Move to folder utils, use interface for additional operations
    public void listFilesForFolder(final File folder, String cachesFolder) {

        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {

            String currentFileEntryName = fileEntry.getName();

            if (!inWrapperFolder && !inCachesFolder && !inJdksFolder && fileEntry.isDirectory()) {

//                System.out.println("Current folder = " + currentFileEntryName);

                //TODO : Move to folder utils, use interface for folder specific operations - folder_name & action pairs
                switch (fileEntry.getName()) {

                    case "caches" -> {

                        inCachesFolder = true;
                        inJdksFolder = false;
                        inWrapperFolder = false;
                    }
                    case "jdks" -> {

                        inCachesFolder = false;
                        inJdksFolder = true;
                        inWrapperFolder = false;
                    }
                    case "wrapper" -> {

                        inCachesFolder = false;
                        inJdksFolder = false;
                        inWrapperFolder = true;
                    }
                    default -> {

                        // Remove on feature (Expand for whole gradle cache repository) implementation
                        if (!(inWrapperFolder || inCachesFolder || inJdksFolder)) {

                            continue;
                        }
                    }
                }
//                System.out.println("inWrapperFolder = " + inWrapperFolder);
//                System.out.println("inCachesFolder = " + inCachesFolder);
                System.out.println("Traversing folder " + currentFileEntryName);
                listFilesForFolder(fileEntry, cachesFolder);
                inCachesFolder = inWrapperFolder = inJdksFolder = false;

            } else {

//                System.out.println("inWrapperFolder = " + inWrapperFolder);
//                System.out.println("inCachesFolder = " + inCachesFolder);

//                System.out.println("Current file = " + currentFileEntryName);

                File outFolder = new File(cachesFolder);
                if (!outFolder.exists()) {
                    try {
                        if (!outFolder.mkdirs()) {

                            System.out.println("Can't Create outFolder…");
                        }
                    } catch (SecurityException e) {

                        System.out.println("Exception : Can't Create outFolder.\nMessage : " + e.getLocalizedMessage());
                    }
                }

                //TODO : Move to flag action utils, use flag & action pairs for interfaces
                if (inCachesFolder && currentFileEntryName.equals("modules-2")) {

                    System.out.println("Adding modules-2 directory to cache. ");
                    executeCmdCommandWithWait(new String[]{"winrar", "u", cachesFolder + "\\gradle_repository.rar", fileEntry.getPath()});

                } else if(inJdksFolder) {

                    if (currentFileEntryName.contains("jdk") && FilenameUtils.getExtension(currentFileEntryName).equals("zip")) {

                            System.out.println("Current JDKs : " + jdks.toString());

                            //TODO : Move to file utils, use interface for list contains file & not contains file
                            if (jdks.contains(currentFileEntryName)) {

                                System.out.println("JDK distribution " + currentFileEntryName + " already available in the cache.");

                            } else {

                                String jdkDistributionFullPath = fileEntry.getPath();
                                System.out.println("Adding JDK distribution " + currentFileEntryName + " to cache.");
                                System.out.println("Adding " + jdkDistributionFullPath);
                                executeCmdCommandWithWait(new String[]{"winrar", "u", cachesFolder + "\\gradle_repository.rar", jdkDistributionFullPath, jdkDistributionFullPath + ".lock"});

                                jdks.add(fileEntry.getName());
                            }
                    }

                } else if (inWrapperFolder) {

                    if (inDistsFolder) {

                        //TODO : Move to folder utils, use interface for folder contains file & not contains file
                        if (currentFileEntryName.contains("gradle-") && FilenameUtils.getExtension(currentFileEntryName).equals("zip")) {

                            System.out.println("Current Distributions : " + distributions.toString());

                            //TODO : Move to file utils, use interface for list contains file & not contains file
                            if (distributions.contains(currentFileEntryName)) {

                                System.out.println("Gradle distribution " + currentFileEntryName + " already available in the cache.");
                                System.out.println("Adding base directory : " + fileEntry.getParent());
                                executeCmdCommandWithWait(new String[]{"winrar", "u", "-x" + fileEntry.getParent() + "\\*", cachesFolder + "\\gradle_repository.rar", fileEntry.getParent()});

                            } else {

                                String gradleDistributionFullPath = fileEntry.getPath();
                                System.out.println("Adding Gradle distribution " + currentFileEntryName + " to cache.");
                                System.out.println("Adding " + gradleDistributionFullPath);
                                executeCmdCommandWithWait(new String[]{"winrar", "u", cachesFolder + "\\gradle_repository.rar", gradleDistributionFullPath, gradleDistributionFullPath + ".lck"});

                                distributions.add(fileEntry.getName());
                            }
                        } else {

                            if (fileEntry.isDirectory() && ((currentFileEntryName.contains("all") || currentFileEntryName.contains("bin")) || !currentFileEntryName.contains("gradle"))) {

//                                    System.out.println("Traversing folder " + currentFileEntryName);
                                listFilesForFolder(fileEntry, cachesFolder);                           
							}
                        }

                    } else if (currentFileEntryName.equals("dists")) {

                        inDistsFolder = true;
                        System.out.println("Traversing folder " + currentFileEntryName);
                        listFilesForFolder(fileEntry, cachesFolder);
                        inDistsFolder = false;
                    }
                }
            }
        }
    }

    //TODO : Move to console utils, use exit_status & exit_message pair return
    public void executeCmdCommandWithWait(String[] commandWithArguments) {

        System.out.println("CMD Command : " + Arrays.toString(commandWithArguments));
        try {

            Process process = new ProcessBuilder(commandWithArguments).start();
            process.waitFor();

        } catch (InterruptedException e) {

            System.out.println("Exception : The current thread is interrupted while waiting.\nMessage : " + e.getLocalizedMessage());

        } catch (IOException e) {

            System.out.println("Exception : The command I/O Error.\nMessage : " + e.getLocalizedMessage());
        }
    }
}
