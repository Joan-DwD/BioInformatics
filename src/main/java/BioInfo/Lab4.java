package BioInfo;

import java.io.*;
import java.util.*;

public class Lab4 {
    static final int NUM_READS = 20000;
    static final int MIN_LEN = 100;
    static final int MAX_LEN = 150;
    static final int MIN_OVERLAP = 10;

    public static void main(String[] args) throws IOException {
        File file = new File("src/main/java/BioInfo/GCF_003575465.1_ASM357546v1_genomic.fna");
        if (!file.exists())
            file = new File("GCF_003575465.1_ASM357546v1_genomic.fna");

        String genome = loadGenome(file);

        String[] reads = makeReads(genome);
        int total = Arrays.stream(reads).mapToInt(String::length).sum();
        System.out.println("Reads: " + reads.length);
        System.out.printf("Coverage: %.2fx%n", (double) total / genome.length());

        long start = System.currentTimeMillis();
        List<String> contigs = assemble(reads);
        System.out.println("Assembled in " + (System.currentTimeMillis() - start) + " ms");

        contigs.sort((a, b) -> b.length() - a.length());
        System.out.println("Contigs: " + contigs.size());
        for (int i = 0; i < Math.min(5, contigs.size()); i++) {
            System.out.println("Contig " + (i + 1) + ": " + contigs.get(i).length() + " bp");
        }

        check(genome, contigs);
    }

    static String loadGenome(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null && !line.startsWith(">")) {
                sb.append(line.trim().toUpperCase());
            }
        }
        return sb.toString();
    }

    static String[] makeReads(String genome) {
        Random rand = new Random(42);
        String[] reads = new String[NUM_READS];
        for (int i = 0; i < NUM_READS; i++) {
            int len = MIN_LEN + rand.nextInt(MAX_LEN - MIN_LEN + 1);
            int start = rand.nextInt(genome.length() - len + 1);
            reads[i] = genome.substring(start, start + len);
        }
        return reads;
    }

    static List<String> assemble(String[] reads) {
        int n = reads.length;
        String[] flipped = new String[n];
        for (int i = 0; i < n; i++) {
            flipped[i] = new StringBuilder(reads[i]).reverse().toString();
        }

        Map<String, List<Integer>> starts = index(reads);
        Map<String, List<Integer>> flippedStarts = index(flipped);
        boolean[] used = new boolean[n];

        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++)
            order[i] = i;
        Arrays.sort(order, (a, b) -> reads[b].length() - reads[a].length());

        List<String> contigs = new ArrayList<>();
        for (int i : order) {
            if (used[i])
                continue;
            used[i] = true;

            StringBuilder contig = new StringBuilder(reads[i]);
            growRight(contig, reads, starts, used);
            contig.reverse();
            growRight(contig, flipped, flippedStarts, used);
            contig.reverse();

            dropInside(contig.toString(), reads, starts, used);
            contigs.add(contig.toString());
        }
        return contigs;
    }

    static Map<String, List<Integer>> index(String[] reads) {
        Map<String, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < reads.length; i++) {
            map.computeIfAbsent(reads[i].substring(0, MIN_OVERLAP), k -> new ArrayList<>()).add(i);
        }
        return map;
    }

    static void growRight(StringBuilder contig, String[] reads, Map<String, List<Integer>> starts, boolean[] used) {
        while (true) {
            String tail = contig.substring(Math.max(0, contig.length() - MAX_LEN));
            int best = -1;
            int bestOverlap = 0;

            for (int pos = 0; pos + MIN_OVERLAP <= tail.length(); pos++) {
                List<Integer> matches = starts.get(tail.substring(pos, pos + MIN_OVERLAP));
                if (matches == null)
                    continue;
                int overlap = tail.length() - pos;

                for (int i : matches) {
                    if (used[i])
                        continue;
                    String r = reads[i];
                    if (r.length() <= overlap) {
                        if (tail.startsWith(r, pos))
                            used[i] = true;
                    } else if (overlap > bestOverlap && r.regionMatches(0, tail, pos, overlap)) {
                        best = i;
                        bestOverlap = overlap;
                    }
                }
            }

            if (best == -1)
                return;
            used[best] = true;
            contig.append(reads[best], bestOverlap, reads[best].length());
        }
    }

    static void dropInside(String contig, String[] reads, Map<String, List<Integer>> starts, boolean[] used) {
        for (int p = 0; p + MIN_OVERLAP <= contig.length(); p++) {
            List<Integer> matches = starts.get(contig.substring(p, p + MIN_OVERLAP));
            if (matches == null)
                continue;
            for (int i : matches) {
                if (!used[i] && contig.startsWith(reads[i], p))
                    used[i] = true;
            }
        }
    }

    static void check(String genome, List<String> contigs) {
        boolean[] covered = new boolean[genome.length()];
        int wrong = 0;
        for (String c : contigs) {
            int pos = genome.indexOf(c);
            if (pos == -1)
                wrong++;
            else
                Arrays.fill(covered, pos, pos + c.length(), true);
        }

        int rebuilt = 0;
        for (boolean b : covered)
            if (b)
                rebuilt++;

        System.out.println("Wrong joins: " + wrong);
        System.out.printf("Genome rebuilt: %.2f%%%n", 100.0 * rebuilt / genome.length());
        if (contigs.size() == 1 && contigs.get(0).equals(genome)) {
            System.out.println("Identical to the original!");
        }
    }
}