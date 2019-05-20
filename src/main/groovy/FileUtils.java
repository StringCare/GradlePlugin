
import java.io.*;

public class FileUtils {

    final static int maxToShow = 15;
    private static String projectPath = "";

    private FileUtils() {
        // nothing to do here
    }

    public static void defineProjectPath(String path) {
        projectPath = path;
    }

    private static String getTextFromFilePath(String path) {
        if (path == null) {
            return "";
        }

        String message = "";

        File file = new File(path);
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
        return projectPath + File.separator + module + File.separator;
    }

    private static String getString(BufferedReader br) {
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
    public static void backupStringResources(String module, Config config, boolean debug) {
        String path = getCurrentPath(module);
        for (String folder : config.getSrcFolders()) {
            String currentPath = path + folder + File.separator + "res" + File.separator;

            File file = new File(currentPath);
            String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
            if (directories != null) {
                for (String dir : directories) {
                    String pathToCopy = currentPath + dir + File.separator;
                    String pathToCheck = getCurrentPath(module) + "resbackup" + File.separator + dir + File.separator;
                    for (String sFile : config.getStringFiles()) {
                        try {
                            File toCopy = new File(pathToCopy + sFile);
                            File toCheck = new File(pathToCheck + sFile);
                            if (toCheck.exists()) {
                                toCheck.delete();
                            }
                            if (toCopy.exists()) {
                                PrintUtils.print(module, "- " + toCopy.getParentFile().getName() + File.separator + toCopy.getName(), true);
                                copyFile(toCopy, toCheck);
                                if (debug) {
                                    PrintUtils.print(module, "backuping file: " + toCopy.getPath(), true);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            // TODO HANDLE ERROR
                        }
                    }
                }
            } else {
                if (debug) {
                    PrintUtils.print(module, "source folder not found: " + currentPath, true);
                } else {
                    PrintUtils.print(module, "source folder not found: " + folder, true);
                }
            }
        }
    }

    public static void encryptStringResources(String module, Config config, String key, boolean debug) {
        String path = getCurrentPath(module);
        for (String folder : config.getSrcFolders()) {
            String currentPath = path + folder + File.separator + "res" + File.separator;
            File file = new File(currentPath);
            String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
            if (directories != null) {
                for (String dir : directories) {
                    String pathToEncrypt = currentPath + dir + File.separator;
                    for (String sFile : config.getStringFiles()) {
                        File toEncrypt = new File(pathToEncrypt + sFile);
                        if (toEncrypt.exists()) {
                            PrintUtils.print(module, "- " + toEncrypt.getParentFile().getName() + File.separator + toEncrypt.getName(), true);
                            String encrypted = find(module, getTextFromFilePath(toEncrypt.getAbsolutePath()), key, debug);
                            writeFile(toEncrypt, encrypted);
                            if (debug) {
                                PrintUtils.print(module, "writing file: " + toEncrypt.getPath(), true);
                            }
                        } else if (debug) {
                            PrintUtils.print(module, "source file not exist: " + pathToEncrypt + sFile, true);
                        }
                    }
                }
            } else {
                if (debug) {
                    PrintUtils.print(module, "source folder not found: " + currentPath, true);
                } else {
                    PrintUtils.print(module, "source folder not found: " + folder, true);
                }
            }
        }
    }

    public static void restoreStringResources(String module, Config config, boolean debug) {
        String currentPath = getCurrentPath(module) + "resbackup" + File.separator;
        File file = new File(currentPath);
        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
        if (directories != null) {
            File toRestore;
            for (String dir : directories) {
                String pathToRestore = currentPath + dir + File.separator;
                for (String folder : config.getSrcFolders()) {
                    String pathRes = getCurrentPath(module) + folder + File.separator + "res" + File.separator + dir + File.separator;

                    for (String sFile : config.getStringFiles()) {
                        toRestore = new File(pathToRestore + sFile);
                        File toCheck = new File(pathRes + sFile);
                        if (toRestore.exists()) {
                            try {
                                PrintUtils.print(module,"- " + toCheck.getParentFile().getName() + File.separator + toCheck.getName(), true);
                                copyFile(toRestore, toCheck);
                                if (debug) {
                                    PrintUtils.print(module, "restoring: " + toRestore.getPath(), true);
                                }
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
                if (debug) {
                    PrintUtils.print(module, "temp source folder removed: " + file.getPath(), true);
                }
            }
        } else {
            if (debug) {
                PrintUtils.print(module, "restore folder not found: " + currentPath, true);
            } else {
                PrintUtils.print(module, "restore folder not found", true);
            }
        }
    }


    private static String find(String module, String xmlO, String key, boolean debug) {
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

                encrypted = jniObfuscate(key, result);
                toShow = result;
                content = content.replace(">" + result + "<", ">" + encrypted + "<");

                toShow = toShow.length() > maxToShow ? toShow.substring(0, maxToShow) + ".." : toShow;
                encrypted = encrypted.length() > maxToShow ? encrypted.substring(0, maxToShow) + ".." : encrypted;
                PrintUtils.print(module, "\t[" + toShow + "] - [" + encrypted + "]" + (hasExtra ? extra : ""), true);
            } catch (Exception e) {
                PrintUtils.print(module, "error on " + result, true);
                e.printStackTrace();
            }

            xml1 = toAnalyze.substring(toAnalyze.indexOf(result + "</string>"), (int)(toAnalyze.length()));

            if (xml1.indexOf(toFind1)  <= 0) break;
        }

        if (debug) {
            PrintUtils.print(module, content, true);
        }

        return content;
    }

    private static void writeFile(File file, String xml) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()),"UTF-8"));
            writer.write(xml);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                //
            }
        }
    }

    private static String extrac(String val) {
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

    public static native String jniObfuscate(String key, String value);

    static {
        try {
            if (OS.isWindows()) {
                loadLib("libsignKey.dll");
            } else {
                loadLib("libsignKey.dylib");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads library
     * @param name Library name
     * @throws IOException Exception
     */
    private static void loadLib(String name) throws IOException {
        InputStream in = FileUtils.class.getResourceAsStream(name);
        byte[] buffer = new byte[1024];
        int read = -1;
        File temp = File.createTempFile(name, "");
        FileOutputStream fos = new FileOutputStream(temp);

        while((read = in.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        in.close();

        System.load(temp.getAbsolutePath());
    }


}
