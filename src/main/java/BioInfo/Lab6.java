package BioInfo;

import java.io.*;
import java.util.*;

public class Lab6 {

    static String readFasta(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith(">")) sb.append(line.trim());
        }
        br.close();
        return sb.toString().toUpperCase();
    }

    static String reverseComplement(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.append(switch (s.charAt(i)) {
                case 'A' -> 'T';
                case 'T' -> 'A';
                case 'C' -> 'G';
                case 'G' -> 'C';
                default -> 'N';
            });
        }
        return sb.toString();
    }

    static final String[] RBS_PATTERNS = {
        "AGGAGG",
        "AGGAG",
        "GGAGG",
        "AGGA",
        "GAGG",
        "AAGGAG",
        "AGGAGG"
    };

    static List<int[]> searchPattern(String genome, String pattern) {
        List<int[]> hits = new ArrayList<>();
        int idx = 0;
        while ((idx = genome.indexOf(pattern, idx)) != -1) {
            hits.add(new int[]{idx, idx + pattern.length() - 1});
            idx++;
        }
        return hits;
    }

    public static void main(String[] args) throws IOException {
        String path = "src/main/java/BioInfo/GCF_003575465.1_ASM357546v1_genomic.fna";
        if (args.length > 0) path = args[0];

        String genome = readFasta(path);
        String rcGenome = reverseComplement(genome);

        System.out.println("Genome length: " + genome.length() + " bp");
        System.out.println("Searching for Ribosome Binding Sites (Shine-Dalgarno sequences)...\n");

        Set<String> seen = new LinkedHashSet<>(Arrays.asList(RBS_PATTERNS));

        int totalHits = 0;
        for (String pattern : seen) {
            List<int[]> fwd = searchPattern(genome, pattern);
            List<int[]> rev = searchPattern(rcGenome, pattern);
            totalHits += fwd.size() + rev.size();

            System.out.println("Pattern: " + pattern);
            System.out.println("  + strand hits: " + fwd.size());
            System.out.println("  - strand hits: " + rev.size());

            int show = Math.min(5, fwd.size());
            if (show > 0) {
                System.out.print("  + strand positions: ");
                for (int i = 0; i < show; i++) {
                    System.out.print(fwd.get(i)[0]);
                    if (i < show - 1) System.out.print(", ");
                }
                if (fwd.size() > 5) System.out.print(" ...");
                System.out.println();
            }

            show = Math.min(5, rev.size());
            if (show > 0) {
                System.out.print("  - strand positions: ");
                for (int i = 0; i < show; i++) {
                    System.out.print(rev.get(i)[0]);
                    if (i < show - 1) System.out.print(", ");
                }
                if (rev.size() > 5) System.out.print(" ...");
                System.out.println();
            }
            System.out.println();
        }

        System.out.println("Total RBS hits across both strands: " + totalHits);
    }
}
