import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {

    private boolean distsFlag = false;
    List<String> distributions = new ArrayList<String>();

    public static void main(String[] args) {

        Main main = new Main();
		
        String cachesFolder,gradleRepositoryFolder;
        
        //Checking for commandline arguments
		//First : Path of Gradle Repository Folder
        //Second : Path of Caches Folder
		if(args.length==1) {
            
            gradleRepositoryFolder=args[0];
            cachesFolder="C:\\Caches_C";
		}
        else if(args.length==2) {
            
            gradleRepositoryFolder=args[0];
            cachesFolder=args[1];
        }
		else {
        
            gradleRepositoryFolder="C:\\Programs\\gradle_repository";
            cachesFolder="C:\\Caches_C";
		}

		System.out.println("Gradle Repository Folder : "+gradleRepositoryFolder);        
		System.out.println("Caches Folder : "+cachesFolder);
        
        //TODO : Expand for whole gradle cache repository
        final File folder = new File(gradleRepositoryFolder);
        main.listFilesForFolder(folder,cachesFolder);

    }

    public void listFilesForFolder(final File folder,String cachesFolder) {

        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {

            if (fileEntry.isDirectory()) {

                if (fileEntry.getName().equals("wrapper")) {
                    distsFlag = true;
                }
                // Remove on feature (Expand for whole gradle cache repository) implementation
                else {
                    if (!distsFlag) {
                        continue;
                    }
                }
                listFilesForFolder(fileEntry,cachesFolder);

            } else {

                File outFolder = new File(cachesFolder);
                if (!outFolder.exists()) {
                    try {
                        if (!outFolder.mkdirs()) {
                            System.out.println("Cant Create outFolder...");
                        }
                    } catch (SecurityException e) {
                        System.out.println("Cant Create outFolder, Error : " + e.getLocalizedMessage());
                    }
                }

                try {
                    if (distsFlag) {


                        if (fileEntry.getName().contains("gradle-") && FilenameUtils.getExtension(fileEntry.getName()).equals("zip")) {

                            System.out.println("Current Distributions : " + distributions.toString());

                            if (distributions.contains(fileEntry.getName())) {

                                System.out.println("Gradle distribution " + fileEntry.getName() + " already available in cache.");
                                System.out.println("Adding base directory : " + fileEntry.getParent());

                                // winrar u -xTo_Enquire\* To_Enquire.rar To_Enquire
                                String[] commandWithArguments = new String[]{"winrar", "u", "-x" + fileEntry.getParent() + "\\*", cachesFolder+"\\gradle_repository.rar", fileEntry.getParent()};
                                Process process = new ProcessBuilder(commandWithArguments).start();
                                process.waitFor();
                            } else {

                                String gradleDistributionFullPath=fileEntry.getPath();
                                System.out.println("Adding Gradle distribution " + fileEntry.getName() + " to cache.");
                                System.out.println("Adding " + gradleDistributionFullPath);
                                String[] commandWithArguments = new String[]{"winrar", "u", cachesFolder+"\\gradle_repository.rar", gradleDistributionFullPath,gradleDistributionFullPath + ".lck"};
                                Process process = new ProcessBuilder(commandWithArguments).start();
                                process.waitFor();

                                // System.out.println("Adding " + fileEntry.getPath() + ".lck");
                                // commandWithArguments[3] = fileEntry.getPath() + ".lck";
                                // process = new ProcessBuilder(commandWithArguments).start();
                                // process.waitFor();

                                // System.out.println("Adding " + fileEntry.getPath() + ".ok");
                                // commandWithArguments[3] = fileEntry.getPath() + ".ok";
                                // process = new ProcessBuilder(commandWithArguments).start();
                                // process.waitFor();

                                distributions.add(fileEntry.getName());
                            }
                        }
                    } else {
                        String[] commandWithArguments = new String[]{"winrar", "u", cachesFolder+"\\gradle_repository.rar", fileEntry.getPath()};
                        Process process = new ProcessBuilder(commandWithArguments).start();
                        process.waitFor();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
