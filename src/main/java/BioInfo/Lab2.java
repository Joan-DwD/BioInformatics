package BioInfo;

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Lab2 {

    static HashMap<String, String> table = new HashMap<>();

    static {
        add("Phe", "UUU", "UUC");
        add("Leu", "UUA", "UUG", "CUU", "CUC", "CUA", "CUG");
        add("Ser", "UCU", "UCC", "UCA", "UCG", "AGU", "AGC");
        add("Tyr", "UAU", "UAC");
        add("Stop", "UAA", "UAG", "UGA");
        add("Cys", "UGU", "UGC");
        add("Trp", "UGG");
        add("Pro", "CCU", "CCC", "CCA", "CCG");
        add("His", "CAU", "CAC");
        add("Gln", "CAA", "CAG");
        add("Arg", "CGU", "CGC", "CGA", "CGG", "AGA", "AGG");
        add("Ile", "AUU", "AUC", "AUA");
        add("Met", "AUG");
        add("Thr", "ACU", "ACC", "ACA", "ACG");
        add("Asn", "AAU", "AAC");
        add("Lys", "AAA", "AAG");
        add("Val", "GUU", "GUC", "GUA", "GUG");
        add("Ala", "GCU", "GCC", "GCA", "GCG");
        add("Asp", "GAU", "GAC");
        add("Glu", "GAA", "GAG");
        add("Gly", "GGU", "GGC", "GGA", "GGG");
    }

    public static void add(String aminoAcid, String... codons) {
        for (String c : codons) {
            table.put(c, aminoAcid);
        }
    }

    public static String translate(String dna) {
        String rna = dna.replaceAll("\\s+", "").toUpperCase().replace('T', 'U');
        return rna;
    }

    public static void main(String[] args) {
        exercise1();

        System.out.println();

        exercise2();

        System.out.println();

        exercise3();
    }

    private static void exercise1() {
        String s = "AUGACGAGGGCC";
        int start = -1;
        for (int i = 0; i + 3 <= s.length(); i++) {
            if (s.substring(i, i + 3).equals("AUG")) {
                start = i;
                break;
            }
        }
    
        String protein = "";
        if (start != -1) {
            for (int i = start; i + 3 <= s.length(); i += 3) {
                String aa = table.get(s.substring(i, i + 3));
                if (aa.equals("Stop")) {
                    break;
                }
                protein += aa + " ";
            }
        }
        System.out.println(protein.trim());
}

    private static void exercise2() {
        String s = "AGCAAAAGCAGGAGTTCAAAATGAATCCAAATCAGAAAATAATAACCATTGGGTCAATCTGTATGGGGAT\n" +
                "CGGAATAATCAGCCTAATATTACAAATTGGAAACATAATCTCAATGTGGGTTAGTCATTCAATTCAGACT\n" +
                "GAAAATCAAAATCACCATGAAGCATGCAACCCAAGCATTGCTGGACAGGATGCAGCTTCAGTGGCACTAG\n" +
                "CAGGCAATTCCTCTCTTTGTCCCATTAGTGGGTGGGCTATATACAGTAAAGACAATGGTATAAGAATTGG\n" +
                "CTCCAAAGGAGACGTATTTGTCATAAGAGAGCCATTTATTTCATGCTCTCACTTGGAATGCAGGACCTTT\n" +
                "TTTCTGACTCAGGGCGCCTTGTTGAATGACAAACACTCAAATGGAACCGTTAAAGACAGAAGCCCTTATA\n" +
                "GAACCTTGATGAGCTGTCCTGTTGGTGAAGCTCCTTCTCCCTACAATTCAAGGTTCGTGTCGGTTGCATG\n" +
                "GTCAGCAAGTGCCTGCCATGATGGCATGGGTTGGCTAACAATCGGAATTTCTGGTCCAGATAATGGAGCA\n" +
                "GTGGCTGTATTAAAATACAATGGTATAATAACAGACACCATCAAAAGTTGGAAAAATAACATATTGAGAA\n" +
                "CGCAAGAGTCTGAATGTGCCTGTATAAATGGTTCATGTTTCACTATAATGACCGATGGCCCAAGTAATGG\n" +
                "GCAGGCCTCGTACAAAATTTTCAAGATAGAAAAGGGGAAGGTAGTCAAATCAAGTGAATTAAATGCACCT\n" +
                "AATTACCACTACGAGGAATGTTCTTGTTATCCTGATGCAGGTGAAGTAATGTGTGTATGCAGGGATAATT\n" +
                "GGCATGGTTCGAATCGACCATGGGTGTCTTTCAATAAAAACCTTGATTATCAAATAGGTTACATCTGCAG\n" +
                "TGGGGTTTTTGGTGATAATCCACGACCCAATGATGGAACAGGCAGCTGTGGTCCAGTGTCTTCTAATGGA\n" +
                "GCATATGGGATAAAGGGGTTCTCATTTAAGTATGGTAATGGTGTTTGGATAGGGAGAACCAAAAGCACTA\n" +
                "GTTCCAGAAGCGGGTTTGAGATGATTTGGGATCCCAATGGATGGACAGAAACTGACAGCAGTTTCTCTGT\n" +
                "GAAGCAAGATATTGTAGCAATAACTGATTGGTCGGGATATAGCGGGAGTTTCGTCCAACACCCTGAATTA\n" +
                "ACAGGGTTGGACTGCATGAGGCCTTGTTTCTGGGTTGAACTGATCAGGGGACGGCCCAACCACAATACGA\n" +
                "TCTGGACTAGTGGGAGCAGCATTTCCTTCTGTGGTGTGAACAGCGATACTGTAGGTTGGTCTTGGCCAGA\n" +
                "CGGTGCTGAGTTGCCATTCACCATTGACAAGTAGTTTGTTCAAAAAAACTCCTTGTTTCTACT";
                String rna = translate(s);
                int count = 0;
                for (int frame = 0; frame < 3; frame++) {
                    for (int i = frame; i + 3 <= rna.length(); i += 3) {
                        if (rna.startsWith("AUG", i)) {
                            int j = i;
                            while (j + 3 <= rna.length() && !table.get(rna.substring(j, j + 3)).equals("Stop")) {
                                j += 3;
                            }
                            if (j + 3 <= rna.length()) {  
                                count++;
                                i = j;                   
                            }
                        }
                    }
                }
                System.out.println("Valid sequences: " + count);
    }

    private static void exercise3() {
        String seq = "AGCAAAAGCAGGAGTTCAAAATGAATCCAAATCAGAAAATAATAACCATTGGGTCAATCTGTATGGGGAT\n" +
                "CGGAATAATCAGCCTAATATTACAAATTGGAAACATAATCTCAATGTGGGTTAGTCATTCAATTCAGACT\n" +
                "GAAAATCAAAATCACCATGAAGCATGCAACCCAAGCATTGCTGGACAGGATGCAGCTTCAGTGGCACTAG\n" +
                "CAGGCAATTCCTCTCTTTGTCCCATTAGTGGGTGGGCTATATACAGTAAAGACAATGGTATAAGAATTGG\n" +
                "CTCCAAAGGAGACGTATTTGTCATAAGAGAGCCATTTATTTCATGCTCTCACTTGGAATGCAGGACCTTT\n" +
                "TTTCTGACTCAGGGCGCCTTGTTGAATGACAAACACTCAAATGGAACCGTTAAAGACAGAAGCCCTTATA\n" +
                "GAACCTTGATGAGCTGTCCTGTTGGTGAAGCTCCTTCTCCCTACAATTCAAGGTTCGTGTCGGTTGCATG\n" +
                "GTCAGCAAGTGCCTGCCATGATGGCATGGGTTGGCTAACAATCGGAATTTCTGGTCCAGATAATGGAGCA\n" +
                "GTGGCTGTATTAAAATACAATGGTATAATAACAGACACCATCAAAAGTTGGAAAAATAACATATTGAGAA\n" +
                "CGCAAGAGTCTGAATGTGCCTGTATAAATGGTTCATGTTTCACTATAATGACCGATGGCCCAAGTAATGG\n" +
                "GCAGGCCTCGTACAAAATTTTCAAGATAGAAAAGGGGAAGGTAGTCAAATCAAGTGAATTAAATGCACCT\n" +
                "AATTACCACTACGAGGAATGTTCTTGTTATCCTGATGCAGGTGAAGTAATGTGTGTATGCAGGGATAATT\n" +
                "GGCATGGTTCGAATCGACCATGGGTGTCTTTCAATAAAAACCTTGATTATCAAATAGGTTACATCTGCAG\n" +
                "TGGGGTTTTTGGTGATAATCCACGACCCAATGATGGAACAGGCAGCTGTGGTCCAGTGTCTTCTAATGGA\n" +
                "GCATATGGGATAAAGGGGTTCTCATTTAAGTATGGTAATGGTGTTTGGATAGGGAGAACCAAAAGCACTA\n" +
                "GTTCCAGAAGCGGGTTTGAGATGATTTGGGATCCCAATGGATGGACAGAAACTGACAGCAGTTTCTCTGT\n" +
                "GAAGCAAGATATTGTAGCAATAACTGATTGGTCGGGATATAGCGGGAGTTTCGTCCAACACCCTGAATTA\n" +
                "ACAGGGTTGGACTGCATGAGGCCTTGTTTCTGGGTTGAACTGATCAGGGGACGGCCCAACCACAATACGA\n" +
                "TCTGGACTAGTGGGAGCAGCATTTCCTTCTGTGGTGTGAACAGCGATACTGTAGGTTGGTCTTGGCCAGA\n" +
                "CGGTGCTGAGTTGCCATTCACCATTGACAAGTAGTTTGTTCAAAAAAACTCCTTGTTTCTACT";
        String rna = translate(seq);
        int[] counts = new int[3];
            for (int frame = 0; frame < 3; frame++) {
                int i = frame;
                while (i + 3 <= rna.length()) {
                    if (rna.startsWith("AUG", i)) {
                        while (i + 3 <= rna.length() && !table.get(rna.substring(i, i + 3)).equals("Stop")) {
                            i += 3;
                        }
                        if (i + 3 <= rna.length()) counts[frame]++;
                    }
                    i += 3;
                }
                System.out.println("Frame " + (frame + 1) + ": " + counts[frame]);
            }

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int max = Math.max(1, Math.max(counts[0], Math.max(counts[1], counts[2])));
                int barWidth = 80, gap = 40, base = getHeight() - 40;
                for (int f = 0; f < 3; f++) {
                    int h = counts[f] * (base - 40) / max;
                    int x = 40 + f * (barWidth + gap);
                    g.setColor(new Color(55, 138, 221));
                    g.fillRect(x, base - h, barWidth, h);
                    g.setColor(Color.BLACK);
                    g.drawString(String.valueOf(counts[f]), x + barWidth / 2 - 5, base - h - 5);
                    g.drawString("Frame " + (f + 1), x + 15, base + 20);
                }
            }
        };

        JFrame window = new JFrame("ORFs per reading frame");
        window.add(chart);
        window.setSize(450, 350);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
