import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {

    private boolean isDryRun = true;

    private String gradleRepositoryFolder = "C:\\Programs\\gradle_repository";
    private String cachesFolder = "C:\\Caches_C";

    private boolean weatherCacheModules2Folder = false;

    private boolean inWrapperFolder = false;
    private boolean inCachesFolder = false;
    private boolean inDistsFolder = false;
    private boolean inJdksFolder = false;
    private boolean inModules2Folder = false;

    private List<String> distributions = new ArrayList<>();

    public static void main(String[] args) {

        Main main = new Main();

        //TODO : Stop all gradle deamons

        main.checkCommandLineArguments(args);

        System.out.println("Is Dry Run : " + main.isDryRun);
        System.out.println("Gradle Repository Folder : " + main.gradleRepositoryFolder);
        System.out.println("Caches Folder : " + main.cachesFolder);
        System.out.println("Weather cache modules-2 folder : " + main.weatherCacheModules2Folder);

        //TODO : Expand for whole gradle cache repository, if gradle is supported
        final File folder = new File(main.gradleRepositoryFolder);
        main.listFilesForFolder(folder, main.cachesFolder);
    }

    public void checkCommandLineArguments(String[] args) {

        //TODO : Move to CMD argument utils, use interface for 0,1,2 argument actions, use exit_status & exit_message pair return

        /*
        Checking for commandline arguments
        First : Dry run or not; false for not dry run, anyother to dry run, default is dry run
        Second : Weather cache modules-2 or not; true to cache, anyother to skip caching of modules-2 folder, default is skip caching of modules-2 folder
        Third : Path of Gradle User Home Folder
        Fourth : Path of Gradle User Home Compressed Archieve File Parent Folder
        */
        if (args.length >= 1) {

            if(args[0].equals("false")) {

                isDryRun = false;
            }
            if (args.length >= 2) {

                if(args[1].equals("true")) {

                    weatherCacheModules2Folder = true;
                }
                if (args.length >= 3) {

                    //TODO : Check for folder existence
                    gradleRepositoryFolder = args[2];

                    if(args.length >= 4) {

                        cachesFolder = args[3];
                    }
                }
            }
        }
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

                        if(weatherCacheModules2Folder) {

                            inCachesFolder = true;
                            inJdksFolder = false;
                            inWrapperFolder = false;
                        }
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
                if (inCachesFolder) {

                    if(inModules2Folder){

                        //TODO : Sync modules-2 files
                        //TODO : Expand for files version 2.1 & greater
                        if(currentFileEntryName.equals("files-2.1")) {

                            System.out.println("Adding files-2.1 directory to cache. ");
                            // executeCmdCommandWithWait(new String[]{"winrar", "u", cachesFolder + "\\gradle_repository.rar", fileEntry.getPath()});
                            executeCmdCommandWithWait(syncFolderIncludingAbsolutePathsWith7zArchieve(cachesFolder + "\\gradle_repository.7z",fileEntry.getPath()));

                        } else if(currentFileEntryName.contains("metadata-")) {

                            if(Float.parseFloat(currentFileEntryName.replace("metadata-","")) >= 2.90)
                            {
                                System.out.println("Adding "+currentFileEntryName+" directory to cache. ");
                                // executeCmdCommandWithWait(new String[]{"winrar", "u", cachesFolder + "\\gradle_repository.rar", fileEntry.getPath()});
                                executeCmdCommandWithWait(syncFolderIncludingAbsolutePathsWith7zArchieve(cachesFolder + "\\gradle_repository.7z",fileEntry.getPath()));
                            }
                        }

                    }
                    //TODO : Expand for modules version 2 & greater
                    else if (currentFileEntryName.equals("modules-2")) {

                        inModules2Folder = true;
                        System.out.println("Traversing folder " + currentFileEntryName);
                        listFilesForFolder(fileEntry, cachesFolder);
                        inModules2Folder = false;
                    }

                } else if(inJdksFolder) {

                    if (currentFileEntryName.contains("jdk") && FilenameUtils.getExtension(currentFileEntryName).equals("zip")) {

						String jdkDistributionFullPath = fileEntry.getPath();
						System.out.println("Adding JDK distribution " + currentFileEntryName + " to cache.");
						System.out.println("Adding " + jdkDistributionFullPath);
						// executeCmdCommandWithWait(new String[]{"winrar", "u", cachesFolder + "\\gradle_repository.rar", jdkDistributionFullPath, jdkDistributionFullPath + ".lock"});
                        executeCmdCommandWithWait(updateAndAddFilesIncludingAbsolutePathsSpecifiedViaSpaceSeperatedFileListWith7zArchieve(cachesFolder + "\\gradle_repository.7z",jdkDistributionFullPath+" "+jdkDistributionFullPath + ".lock"));
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
                                // executeCmdCommandWithWait(new String[]{"winrar", "u", "-x" + fileEntry.getParent() + "\\*", cachesFolder + "\\gradle_repository.rar", fileEntry.getParent()});
                                executeCmdCommandWithWait(updateAndAddFolderIncludingAbsolutePathsExcludingFolderContentsWith7zArchieve(cachesFolder + "\\gradle_repository.7z",fileEntry.getParent()));

                            } else {

                                String gradleDistributionFullPath = fileEntry.getPath();
                                System.out.println("Adding Gradle distribution " + currentFileEntryName + " to cache.");
                                System.out.println("Adding " + gradleDistributionFullPath);
                                // executeCmdCommandWithWait(new String[]{"winrar", "u", cachesFolder + "\\gradle_repository.rar", gradleDistributionFullPath, gradleDistributionFullPath + ".lck"});
                                executeCmdCommandWithWait(updateAndAddFilesIncludingAbsolutePathsSpecifiedViaSpaceSeperatedFileListWith7zArchieve(cachesFolder + "\\gradle_repository.7z",gradleDistributionFullPath+" "+gradleDistributionFullPath + ".lock"));

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

        if(!isDryRun) {

            try {

                Process process = new ProcessBuilder(commandWithArguments).inheritIO().start();
                process.waitFor();

            } catch (InterruptedException e) {

                System.out.println("Exception : The current thread is interrupted while waiting.\nMessage : " + e.getLocalizedMessage());

            } catch (IOException e) {

                System.out.println("Exception : The command I/O Error.\nMessage : " + e.getLocalizedMessage());
            }
        }
    }

    //7z u -t7z msys2-x86_64-20210105-mingw_w64-boost-qt-cache.7z -up1q0r2x1y2z1w2 -spf C:\msys64\var\cache\pacman\pkg\ -mx=9
    public String[] syncFolderIncludingAbsolutePathsWith7zArchieve(String archieveFileFullPath, String folderToSyncFullPath) {

        return new String[]{"7z", "u", "-t7z", archieveFileFullPath, "-up1q0r2x1y2z1w2", "-spf", folderToSyncFullPath+"\\", "-mx=9"};
    }

    //7z u -t7z archive.zip -up1q1r2x1y2z1w2 -spf file1.txt file2.txt -mx=9
    public String[] updateAndAddFilesIncludingAbsolutePathsSpecifiedViaSpaceSeperatedFileListWith7zArchieve(String archieveFileFullPath, String spaceSeperatedFileListWithFullPaths) {
    
        return new String[]{"7z", "u", "-t7z", archieveFileFullPath, "-up1q1r2x1y2z1w2", "-spf", spaceSeperatedFileListWithFullPaths, "-mx=9"};
    }

    //7z u -t7z archive.zip -up1q1r2x1y2z1w2 -spf folder\ -x!folder\* -mx=9
    public String[] updateAndAddFolderIncludingAbsolutePathsExcludingFolderContentsWith7zArchieve(String archieveFileFullPath, String folderToUpdateAndAddFullPath) {
    
        return new String[]{"7z", "u", "-t7z", archieveFileFullPath, "-up1q1r2x1y2z1w2", "-spf", folderToUpdateAndAddFullPath+"\\", "-x!"+folderToUpdateAndAddFullPath+"\\*", "-mx=9"};
    }
}