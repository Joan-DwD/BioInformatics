package BioInfo;

import java.io.*;
import java.util.*;

public class Lab7 {

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

    static List<Integer> getCuts(String dna, String site, int cutPos) {
        List<Integer> cuts = new ArrayList<>();
        int idx = 0;
        while ((idx = dna.indexOf(site, idx)) != -1) {
            cuts.add(idx + cutPos);
            idx++;
        }
        return cuts;
    }

    static List<Integer> getFragments(int dnaLen, List<Integer> cuts) {
        List<Integer> sorted = new ArrayList<>(new TreeSet<>(cuts));
        List<Integer> fragments = new ArrayList<>();
        int prev = 0;
        for (int cut : sorted) {
            fragments.add(cut - prev);
            prev = cut;
        }
        fragments.add(dnaLen - prev);
        return fragments;
    }

    static void printGel(List<Integer> fragments) {
        List<Integer> sorted = new ArrayList<>(fragments);
        Collections.sort(sorted);

        int gelWidth = 40;
        int minLen = sorted.get(0);
        int maxLen = sorted.get(sorted.size() - 1);

        System.out.println("\nGel Electrophoresis:");
        System.out.println("Well" + " ".repeat(gelWidth - 8) + "Anode");

        for (int i = 0; i < sorted.size(); i++) {
            int len = sorted.get(i);
            int col = (maxLen == minLen) ? gelWidth / 2
                    : (int) Math.round((double) (maxLen - len) / (maxLen - minLen) * (gelWidth - 1));
            col = Math.max(0, Math.min(gelWidth - 1, col));

            StringBuilder line = new StringBuilder("-".repeat(gelWidth));
            line.setCharAt(col, '|');
            System.out.println(line + " " + len + " bp");
        }
        System.out.println();
    }

    static void exercise1() throws IOException {
        System.out.println("Exercise 1");
        String path = "src/main/java/BioInfo/GCF_003575465.1_ASM357546v1_genomic.fna";
        String dna = readFasta(path);

        String[] names = {"EcoRI", "BamHI", "HindIII", "TaqI", "HaeIII"};
        String[] sites = {"GAATTC", "GGATCC", "AAGCTT", "TCGA", "GGCC"};
        int[] cutOffsets = {1, 1, 1, 1, 2};

        Scanner sc = new Scanner(System.in);
        System.out.print("Number of restriction enzymes (0 for default 5): ");
        int n = sc.nextInt();

        if (n > 0) {
            names = new String[n];
            sites = new String[n];
            cutOffsets = new int[n];
            for (int i = 0; i < n; i++) {
                System.out.print("Enzyme " + (i + 1) + " (sequence cutPos): ");
                sites[i] = sc.next().toUpperCase();
                cutOffsets[i] = sc.nextInt();
                names[i] = sites[i];
            }
        } else {
            n = 5;
        }

        System.out.println("\nGenome length: " + dna.length() + " bp\n");

        List<Integer> combinedCuts = new ArrayList<>();
        String comboName = "";

        for (int i = 0; i < n; i++) {
            List<Integer> cuts = getCuts(dna, sites[i], cutOffsets[i]);
            List<Integer> fragments = getFragments(dna.length(), cuts);

            System.out.println(names[i] + " (" + sites[i] + ", cut at " + cutOffsets[i] + "):");
            System.out.println("  Cleavages: " + cuts.size());
            System.out.println("  Positions: " + cuts);
            System.out.println("  Fragment lengths: " + fragments);
            printGel(fragments);

            combinedCuts.addAll(cuts);
            comboName = comboName.isEmpty() ? names[i] : comboName + " + " + names[i];

            if (i > 0) {
                List<Integer> comboFragments = getFragments(dna.length(), combinedCuts);
                System.out.println("Combined (" + (i + 1) + " enzymes - " + comboName + "):");
                System.out.println("  Total cleavages: " + new TreeSet<>(combinedCuts).size());
                System.out.println("  Positions: " + new TreeSet<>(combinedCuts));
                System.out.println("  Fragment lengths: " + comboFragments);
                printGel(comboFragments);
            }
        }
    }

    static void exercise2() {
        System.out.println("Exercise 2");
        Random rnd = new Random(42);
        char[] bases = {'A', 'C', 'G', 'T'};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            sb.append(bases[rnd.nextInt(4)]);
        }

        int irLen = 8;
        String[] leftIR = {"CTGACGTA", "GACTCAGT", "ATCGGCTA"};
        String[] rightIR = new String[leftIR.length];
        for (int i = 0; i < leftIR.length; i++) {
            rightIR[i] = reverseComplement(leftIR[i]);
        }

        sb.replace(10, 10 + irLen, leftIR[0]);
        sb.replace(30, 30 + irLen, rightIR[0]);

        sb.replace(50, 50 + irLen, leftIR[1]);
        sb.replace(140, 140 + irLen, rightIR[1]);

        sb.replace(75, 75 + irLen, leftIR[2]);
        sb.replace(100, 100 + irLen, rightIR[2]);

        String dna = sb.toString();
        System.out.println("Generated DNA (" + dna.length() + " bp):");
        System.out.println(dna + "\n");

        System.out.println("Expected transposomes:");
        System.out.println("  T1 (normal): pos 10 - 37, length 28 bp");
        System.out.println("  T2 (normal, outer): pos 50 - 147, length 98 bp");
        System.out.println("  T3 (nested inside T2): pos 75 - 107, length 33 bp\n");

        System.out.println("Detected transposomes:");
        List<int[]> found = new ArrayList<>();

        for (String l : leftIR) {
            String r = reverseComplement(l);
            int start = dna.indexOf(l);
            while (start != -1) {
                int rPos = dna.indexOf(r, start + irLen + 10);
                while (rPos != -1) {
                    int end = rPos + irLen - 1;
                    int len = end - start + 1;
                    int dist = rPos - (start + irLen);
                    found.add(new int[]{start, end, len, dist});
                    System.out.println("  Pos " + start + " - " + end + ", length: " + len + " bp (IR: " + l + " ... " + r + ", distance: " + dist + " bp)");
                    rPos = dna.indexOf(r, rPos + 1);
                }
                start = dna.indexOf(l, start + 1);
            }
        }

        System.out.println("\nCases found:");
        for (int i = 0; i < found.size(); i++) {
            for (int j = i + 1; j < found.size(); j++) {
                int[] a = found.get(i);
                int[] b = found.get(j);
                if (a[0] < b[0] && a[1] > b[1]) {
                    System.out.println("  Nested: (" + b[0] + "-" + b[1] + ") is nested inside (" + a[0] + "-" + a[1] + ")");
                } else if (a[1] < b[0]) {
                    System.out.println("  One after another: (" + a[0] + "-" + a[1] + ") followed by (" + b[0] + "-" + b[1] + ")");
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        exercise1();
        exercise2();
    }
}
