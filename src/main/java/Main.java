import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main {

    private boolean distsFlag = false;

    public static void main(String[] args) {

        Main main = new Main();

        //TODO : Expand for whole gradle cache repository
        final File folder = new File("C:\\Programs\\gradle_repository");
        main.listFilesForFolder(folder);

    }

    public void listFilesForFolder(final File folder) {

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
                listFilesForFolder(fileEntry);

            } else {

                File outFolder = new File("C:\\Caches");
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

                            //TODO : If multiple gradle distribution folders for same gradle distribution (due to wrapper file hash mismatch), then add zip file in first one, only the blank hash directory in others
//                    System.out.println(fileEntry.getPath());
//                    System.out.println(fileEntry.getPath()+".lck");
//                    System.out.println(fileEntry.getPath()+".ok");

                            String[] commandWithArguments = new String[]{"winrar", "u", "C:\\Caches\\gradle_repository.rar", fileEntry.getPath()};


                            Process process = new ProcessBuilder(commandWithArguments).start();
                            process.waitFor();

                            commandWithArguments[3] = fileEntry.getPath() + ".lck";
                            process = new ProcessBuilder(commandWithArguments).start();
                            process.waitFor();

                            commandWithArguments[3] = fileEntry.getPath() + ".ok";
                            process = new ProcessBuilder(commandWithArguments).start();
                            process.waitFor();
                        }
                    } else {
                        String[] commandWithArguments = new String[]{"winrar", "u", "C:\\Caches\\gradle_repository.rar", fileEntry.getPath()};
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
