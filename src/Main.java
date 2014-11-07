import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by PACKAGE_NAME on 07/11/14.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Path fastaPath = Paths.get("/tmp/in.fasta");
        Path outDir = Paths.get("/tmp");
        Path fastaBase = fastaPath.getFileName();
        Multimap<String, String> dataBySample = ArrayListMultimap.create();
        Pattern samplePattern = Pattern.compile("sample=([^ ]+)");
        Matcher matcher;
        int nLine = 0;

        try(BufferedReader reader = Files.newBufferedReader(fastaPath, Charset.forName("US-ASCII"))) {
            String line = null;
            String sampleName;
            while((line = reader.readLine()) != null) {
                nLine++;
                if(nLine % 100 == 0) System.out.printf("Processing line %d...\n", nLine);
                matcher = samplePattern.matcher(line);
                if(matcher.find()) {
                    dataBySample.put(matcher.group(1), line);
                    dataBySample.put(matcher.group(1), reader.readLine());
                    nLine++;
                }
                else {
                    System.err.println(String.format("Error: No sample section in line %d\n", nLine));
                    reader.readLine();
                    nLine++;
                }
            }
        }


        for(Object key : dataBySample.keys()) {
            Path sampleOutputPath = outDir.resolve(key + ".fasta");
            try (BufferedWriter writer = Files.newBufferedWriter(sampleOutputPath)) {
                writer.write(Joiner.on("\n").join(dataBySample.asMap().get(key)));
            }
        }


    }
}
