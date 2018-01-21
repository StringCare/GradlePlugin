
import java.io.*;

public class FileUtils {

    final static int maxToShow = 15;

    private FileUtils() {
        // nothing to do here
    }

    public static String getTextFromFilePath(String path) {
        if (path == null) {
            return "";
        }
        String xml = "";
        String inputFilePath = path;

        String message = "";

        File file = new File(inputFilePath);
        try {
            FileInputStream stream = new FileInputStream(file);
            message = getString(new BufferedReader(new InputStreamReader(stream,"UTF-8")));
            stream.close();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return message;
    }

    private static String getCurrentPath(String module) {
        String path = new File(".").getAbsolutePath().replace(".", "").replace(module + File.separator,"");
        return path;
    }

    public static String getString(BufferedReader br) {
        StringBuilder builder = new StringBuilder();

        try {
            String aux = "";

            while ((aux = br.readLine()) != null) {
                builder.append(aux);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    // detect multiple sourceSet res.srcDirs
    public static void backupStringResources(String module, Config config) {
        String currentPath = getCurrentPath(module);
        for (String folder : config.getSrcFolders()) {
            currentPath += module + File.separator + folder + File.separator + "res" + File.separator;
            File file = new File(currentPath);
            String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
            if (directories != null) {
                for (String dir : directories) {
                    String pathToCopy = currentPath + dir + File.separator;
                    String pathToCheck = getCurrentPath(module) + module + File.separator + "resbackup" + File.separator + dir + File.separator;

                    for (String sFile : config.getStringFiles()) {
                        try {
                            File toCopy = new File(pathToCopy + sFile);
                            File toCheck = new File(pathToCheck + sFile);
                            if (toCheck.exists()) {
                                toCheck.delete();
                            }
                            if (toCopy.exists()) {
                                PrintUtils.print("- " + toCopy.getParentFile().getName() + File.separator + toCopy.getName(), true);
                                copyFile(toCopy, toCheck);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            // TODO HANDLE ERROR
                        }
                    }
                }
            } else {
                PrintUtils.print(module, "source folder not found: " + folder, true);
            }
        }
    }

    public static void encryptStringResources(String module, Config config, String key) {
        String currentPath = getCurrentPath(module);
        for (String folder : config.getSrcFolders()) {
            currentPath += module + File.separator + folder + File.separator + "res" + File.separator;
            File file = new File(currentPath);
            String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
            if (directories != null) {
                for (String dir : directories) {
                    String pathToEncrypt = currentPath + dir + File.separator;
                    for (String sFile : config.getStringFiles()) {
                        File toEncrypt = new File(pathToEncrypt + sFile);
                        if (toEncrypt.exists()) {
                            PrintUtils.print("- " + toEncrypt.getParentFile().getName() + File.separator + toEncrypt.getName(), true);
                            String encrypted = find(getTextFromFilePath(toEncrypt.getAbsolutePath()), key);
                            writeFile(toEncrypt, encrypted);
                        }
                    }
                }
            } else {
                PrintUtils.print(module, "source folder not found: " + folder, true);
            }
        }
    }

    public static void restoreStringResources(String module, Config config) {
        String currentPath = getCurrentPath(module) + module + File.separator + "resbackup" + File.separator;
        File file = new File(currentPath);
        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
        if (directories != null) {
            File toRestore;
            for (String dir : directories) {
                String pathToRestore = currentPath + dir + File.separator;
                for (String folder : config.getSrcFolders()) {
                    String pathRes = getCurrentPath(module) + module + File.separator + folder + File.separator + "res" + File.separator + dir + File.separator;

                    for (String sFile : config.getStringFiles()) {
                        toRestore = new File(pathToRestore + sFile);
                        File toCheck = new File(pathRes + sFile);
                        if (toRestore.exists()) {
                            try {
                                PrintUtils.print("- " + toCheck.getParentFile().getName() + File.separator + toCheck.getName(), true);
                                copyFile(toRestore, toCheck);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            toRestore.delete();
                            toRestore.getParentFile().delete();
                        }
                    }
                }
            }
            if (file.isDirectory()) {
                file.delete();
            }
        } else {
            PrintUtils.print(module, "restore folder not found", true);
        }
    }


    public static boolean isEncrypted(String value, String key) {
        boolean encrypted = true;

        try {
            if (AES.decrypt(value, key).equals(value))	// not encrypted value
                encrypted = false;
            else
                encrypted = true;
        } catch (Exception e) {
            encrypted = false;
        }

        return encrypted;
    }

    public static String find(String xmlO, String key) {
        String content = xmlO;
        String toFind1 = "hidden=\"true\"";

        String xml1 = content;
        while (xml1.indexOf(toFind1) > 0) {
            String toAnalyze = xml1.substring(xml1.indexOf(toFind1), (int)(xml1.length()));

            String result = extrac(toAnalyze);

            try {
                String encrypted = "";
                String toShow = "";

                String extra = " value_already_encrypted";
                boolean hasExtra = false;

                if (isEncrypted(result, key)) {
                    encrypted = result;
                    toShow = AES.decrypt(result, key);
                    hasExtra = true;
                } else {
                    encrypted = AES.encrypt(result, key);
                    toShow = result;
                    content = content.replaceAll(">" + result + "<", ">" + encrypted + "<");
                }


                toShow = toShow.length() > maxToShow ? toShow.substring(0, maxToShow) + ".." : toShow;
                encrypted = encrypted.length() > maxToShow ? encrypted.substring(0, maxToShow) + ".." : encrypted;
                PrintUtils.print("\t[" + toShow + "] - [" + encrypted + "]" + (hasExtra ? extra : ""), true);
            } catch (Exception e) {
                PrintUtils.print("error on " + result);
                e.printStackTrace();
            }


            xml1 = toAnalyze.substring(toAnalyze.indexOf(result + "</string>"), (int)(toAnalyze.length()));

            if (xml1.indexOf(toFind1)  <= 0) break;
        }

        return content;
    }

    public static void writeFile(File file, String xml) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()),"UTF-8"));
            writer.write(xml);
        } catch (Exception e) {
            if (true) e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                //
            }
        }
    }

    public static String extrac(String val) {

        val = val.substring(val.indexOf('>') + 1, val.length());
        val = val.substring(0, val.indexOf("</string>"));

        return val;
    }

    private static void copyFile(File source, File dest) throws IOException {
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        if (!dest.exists()) {
            dest.createNewFile();
        }
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
            input.close();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
