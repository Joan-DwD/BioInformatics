package BioInfo;

import java.util.*;

public class Lab5 {

    static final String DNA = "TTGAGAGTCATTAAGTGAAAATTAATGCTTGACTCAATGTTCCACCTAAAGGATACTGAGGTCGCTCCACCACAAACCAC" +
            "TAACCTAAAGGATTCACATCATGCTGAACAAACGTTACAACCTCAACGCTCGTGTATTAGGCAACACCGCACAGGCTTAC" +
            "GCTGACCGCCTGAAGACAGACAAGAAGTATACCATCGCTCAGACCCTGAAGAGCTGGAACATGATTGTTGAGCGTGAAGG" +
            "CAAGTCCGGTACTTACAGCTTCCCTACAACTGGTTATACTCGTAAGGCTCAACTGATTGCTGACCTTGAGCGTTGTGCAG" +
            "AGCAGTTGCGTGAGCTGGCAGAGTGTAAGGATACAGTGTATGACGTTGAGCAGGCTCACAAAGAGGCCCTGCTGGCTAAC" +
            "ATGCCTTTTGATGGTACATGCTCTGGAGTACAACTGGCTAAGCTAACAGATAATGTGGTTGCCACTGCTCAAGCACACAA" +
            "TAAGACTCACTTAGACACTATCCTGAATGATGCTCGTAAAGCTCTTGCCGCTGTGAATGCAGCTATCTCTGAACATATGG" +
            "CAGGCTATGACGTCCGCACCATGGAGCTTGCTGCTAAAGCTCAAGGCCCTAATCGTGACTCAATGAACTCTATCATCCAG" +
            "TGCCTGTGTCGTGACTCACGTAGTCGTCTGAATGACCTCACTAAGCTGCGTGTGAAGTTCTCAGAAGACCTACGTGACCT" +
            "TGAAGCTCGTATTGTTCCGGCTCAGGTGGTCGCTACCGCTCCAGAACACAACAAGACTCACTTTGTGACTGTCACTGTAG" +
            "GCTCATCAAGAGTCAAGGTTAAGGCGGACTTTCAGACTCGCCTCTATCCTCGTGATATGTCTAAGAGCCGCCTTGAGGAG" +
            "TCTGTGAGCTGTCTCAATGAGTTCATTAAGAACGAGTTCAAGTTCACTGTAGTTAGCGTGGTGACAACCATGGGAGATAC" +
            "TCATGGTGACCTTAAAGCAATCGTGACCACTGAATAACTATTGACACCTAATGAGCCTTGAGGCATCCTTGAGGCTCATC" +
            "AACCGGAGAGACTGAAATGGCTACATTAAAACGTATCGGCAAAGGTGCATTCACCACTGCTTATCTTGATGAACAGACTC" +
            "AGCGTGTCACACTGCATTCATGTGACCCTTACAAAGAGGCTATGGCGTGGGGCTGGTTCCCTGAGTCACCTTTGTTCCCT" +
            "GCTGTGGAGTTCATTGAGCTGGGTGTGTATGAGATGGACTACATGCCAGCCACAAGAGGCCTGAAGTCTGCTCTTCAGCC" +
            "TGACCAGTGGGACCTCTATCAGTGTCTGAGAGCTGTAGCGGATATCTGGCGCTCTAACATGGGCAGTAACAACTGGCGTC" +
            "ATAGTGATTCATTTAGCCTGCTCCATGCGGCTCTTGATAAATCAGTAGCGGCAACTCTGTATCCTGAGGCTGTTCAGACC" +
            "ATCAAGGAGGCTGTAGACGCTTGCGCTAACTTCGGCTCTGATGTGGGCTTTGAGATATCGCCTCGTAACGTTCGTGCAAA" +
            "CAGTGCTGGACAATTAGTTCTGATGGACGTATTCTTCAGCCTCAACAAGTTAATTGAGGTCAGGAAATGAAACAGGTCAC" +
            "CATGCGCATCACCCTTAAGGACTCTTGTGGCACAGTCTGCACGCCAAATGGCTTCGCTCTTAAGGCCCGCTGTGAGCGCC" +
            "TGAAGCGTGAGCTTAAAGCTAACCAATCGTCATCCCTTTACTCTGTAGGAGGTTTTAACCATGCGTAAACTTATTACTGC" +
            "TGCTCTTCTGGTTCTCTCACTGTCTGCCTGTACCTCTCATGTGGTGGTCCATGTGGACCGGTCACACAACGGCGACACTG" +
            "TGACCACTGTGGAAGTCACAAAGACCTTCTTAAAGTAGTTGACAGGCTCTTAAACAGTGGGCTACATTGATGGCTCACTC" +
            "ATTAAGATTCTCTCATCATCAACCTAAGGCAATCATCATGAACGTATCTGAATTAGCGGTAACTCTGGCTCAACTGGTCT";

    static void exercise1() {
        System.out.println("Exercise 1");
        Random rnd = new Random(42);
        int n = DNA.length();
        String[] samples = new String[10];

        for (int i = 0; i < 10; i++) {
            int len = 100 + rnd.nextInt(Math.min(3000, n) - 100 + 1);
            int start = rnd.nextInt(n - len + 1);
            samples[i] = DNA.substring(start, start + len);
            System.out.println("Sample " + (i + 1) + ": " + samples[i].length() + " bp (pos " + start + "-"
                    + (start + len - 1) + ")");
        }

        String[] sorted = samples.clone();
        Arrays.sort(sorted, Comparator.comparingInt(String::length));

        int gelWidth = 40;
        int minLen = sorted[0].length();
        int maxLen = sorted[sorted.length - 1].length();

        System.out.println("\nGel Electrophoresis:");
        System.out.println("Well" + " ".repeat(gelWidth - 8) + "Anode");

        for (int i = 0; i < sorted.length; i++) {
            int len = sorted[i].length();
            int col = (maxLen == minLen) ? gelWidth / 2
                    : (int) Math.round((double) (maxLen - len) / (maxLen - minLen) * (gelWidth - 1));
            col = Math.max(0, Math.min(gelWidth - 1, col));

            StringBuilder line = new StringBuilder("-".repeat(gelWidth));
            line.setCharAt(col, '|');
            System.out.println(line + " " + len + " bp");
        }
        System.out.println();
    }
    static boolean isPrimitive(String u) {
        for (int d = 1; d < u.length(); d++) {
            if (u.length() % d == 0 && u.equals(u.substring(0, d).repeat(u.length() / d))) {
                return false;
            }
        }
        return true;
    }

    static void exercise2() {
        System.out.println("Exercise 2");
        List<String> results = new ArrayList<>();
        int n = DNA.length();

        for (int unitLen = 3; unitLen <= 5; unitLen++) {
            for (int start = 0; start + 2 * unitLen <= n; start++) {
                if (start > 0 && DNA.charAt(start - 1) == DNA.charAt(start - 1 + unitLen)) {
                    continue;
                }

                String unit = DNA.substring(start, start + unitLen);
                if (!isPrimitive(unit)) {
                    continue;
                }

                int end = start + unitLen;
                while (end < n && DNA.charAt(end) == DNA.charAt(end - unitLen)) {
                    end++;
                }

                int total = end - start;
                int copies = total / unitLen;
                if (copies >= 2 && total >= 6 && total <= 10) {
                    results.add("Pos " + start + ", unit: " + unit + " (" + unitLen + " bp), copies: " + copies
                            + ", total: " + total + " bp -> " + DNA.substring(start, end));
                }
            }
        }

        if (results.isEmpty()) {
            System.out.println("No repeats found.");
        } else {
            System.out.println("Repeats found: " + results.size());
            for (String r : results) {
                System.out.println(r);
            }
        }
    }
    public static void main(String[] args) {
        exercise1();
        exercise2();
    }
}
