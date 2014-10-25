import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Joern Buitink
 * Date: 17.06.14
 * Time: 06:46
 * http://www.steinchenwelt.de
 */
public class AndroidXMLWorker {

    public static final String PATH = "/Users/joensi/Developer/buiali/translate_test/res";
    private static final boolean DRYRUN = false;

    public static void main(String[] args) throws Exception {
        final Translator translator = new Translator();
        translator.obtainAccessToken();
        try {
            File directory = new File(PATH);
            if (directory.exists()) {
                System.out.println("found project resource directory!");
                directory = new File(PATH + "/values");
                if (directory.exists()) {
                    System.out.println("found values directory!");
                    File strings = new File(directory.getPath() + "/strings.xml");
                    InputStream in = new FileInputStream(strings);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    StringBuilder bb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        bb.append(line);
                    }

                    final String stringSource = bb.toString();


                    File arrays = new File(directory.getPath() + "/arrays.xml");
                    in = new FileInputStream(arrays);
                    reader = new BufferedReader(new InputStreamReader(in));
                    bb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        bb.append(line);
                    }

                    final String arraySource = bb.toString();

                    System.out.println(stringSource);

                    System.out.println(arraySource);

                    for (final Languages lang : Languages.values()) {
                        if ((DRYRUN && lang.equals(Languages.ENGLISH)) || !DRYRUN) {
                            new Thread() {
                                public void run() {
                                    try {
                                        String dest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n";
                                        String pattern = "<string name=\"([a-zA-Z0-9_]*)\">(.*?)<\\/string>";
                                        Pattern r = Pattern.compile(pattern, Pattern.DOTALL);
                                        Matcher m = r.matcher(stringSource);

                                        File langDirectory = new File(PATH + "/values-" + lang.getGoogleName());
                                        if (!langDirectory.mkdirs()) {
                                            System.out.println("Language directory already exists!");
                                            System.out.println("Skipping language: " + lang.toString());
                                            return;
                                        }
                                        File stringFile = new File(langDirectory.getPath() + "/strings.xml");

                                        //System.out.print("\nTranslating");
                                        while (m.find()) {
                                            if (m.groupCount() == 2) {
                                                String replString = m.group(2);
                                                String translString = translator.translate(replString, "de", lang.getMsName());
                                                dest += String.format("<string name=\"%s\">%s</string>\n", m.group(1), translString);
                                                //System.out.print(".");
                                                if (DRYRUN) {
                                                    System.out.println(replString + " => " + translString);
                                                }
                                            }
                                        }

                                        dest += "</resources>";

                                        FileOutputStream stream = new FileOutputStream(stringFile);
                                        stream.write(dest.getBytes());
                                        stream.close();

                                        dest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n";
                                        pattern = "<string-array name=\"([a-zA-Z0-9_]*)\">(.*?)<\\/string-array>";
                                        r = Pattern.compile(pattern, Pattern.DOTALL);
                                        m = r.matcher(arraySource);

                                        File arrayFile = new File(langDirectory.getPath() + "/arrays.xml");

                                        //System.out.print("\nTranslating");
                                        while (m.find()) {
                                            if (m.groupCount() == 2) {

                                                dest += String.format("<string-array name=\"%s\">\n", m.group(1));

                                                Pattern itemPattern = Pattern.compile("<item>([a-zA-Z0-9_]*?)<\\/item>");
                                                Matcher itemMatcher = itemPattern.matcher(m.group(2));

                                                while (itemMatcher.find()) {

                                                    String translString = translator.translate(itemMatcher.group(1), "de", lang.getMsName());
                                                    dest += String.format("<item>%s</item>\n", translString);
                                                    //System.out.print(".");
                                                    if (DRYRUN) {
                                                        System.out.println(itemMatcher.group(1) + " => " + translString);
                                                    }
                                                }
                                                dest += "</string-array>\n";
                                            }
                                        }



                                        dest += "</resources>";
                                        stream = new FileOutputStream(arrayFile);
                                        stream.write(dest.getBytes());
                                        stream.close();

                                        System.out.println("Wrote strings.xml for language " + lang.toString());
                                        } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }.start();
                        }
                    }
                    //System.out.println(dest);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
