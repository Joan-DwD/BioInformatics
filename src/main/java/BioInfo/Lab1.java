package BioInfo;

import java.util.*;

public class Lab1 {
    public static void main(String[] args) {

        String s = "ACGTTGCATTACGTTGCAGT";

        HashMap<Character, Integer> res = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == 'A') {
                res.put('A', res.getOrDefault('A', 0) + 1);
            } else if (s.charAt(i) == 'C') {
                res.put('C', res.getOrDefault('C', 0) + 1);
            }

            else if (s.charAt(i) == 'G') {
                res.put('G', res.getOrDefault('G', 0) + 1);
            }

            else if (s.charAt(i) == 'T') {
                res.put('T', res.getOrDefault('T', 0) + 1);
            }
        }

        for (Map.Entry<Character, Integer> entry : res.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }

    }
}
