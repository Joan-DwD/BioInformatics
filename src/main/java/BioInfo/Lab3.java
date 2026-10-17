package BioInfo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Lab3 {

    private static final Map<String, String> codon_Table = new LinkedHashMap<>();
    private static final Map<String, String> amino_Acids = new LinkedHashMap<>();
    private static final String GEMINI_API_KEY = System.getenv().getOrDefault("GEMINI_API_KEY",
            "YOUR_GEMINI_API_KEY_HERE");
    // removed mine to block bots from using quota

    static {
        addCodons("Phe", "UUU", "UUC");
        addCodons("Leu", "UUA", "UUG", "CUU", "CUC", "CUA", "CUG");
        addCodons("Ser", "UCU", "UCC", "UCA", "UCG", "AGU", "AGC");
        addCodons("Tyr", "UAU", "UAC");
        addCodons("Stop", "UAA", "UAG", "UGA");
        addCodons("Cys", "UGU", "UGC");
        addCodons("Trp", "UGG");
        addCodons("Pro", "CCU", "CCC", "CCA", "CCG");
        addCodons("His", "CAU", "CAC");
        addCodons("Gln", "CAA", "CAG");
        addCodons("Arg", "CGU", "CGC", "CGA", "CGG", "AGA", "AGG");
        addCodons("Ile", "AUU", "AUC", "AUA");
        addCodons("Met", "AUG");
        addCodons("Thr", "ACU", "ACC", "ACA", "ACG");
        addCodons("Asn", "AAU", "AAC");
        addCodons("Lys", "AAA", "AAG");
        addCodons("Val", "GUU", "GUC", "GUA", "GUG");
        addCodons("Ala", "GCU", "GCC", "GCA", "GCG");
        addCodons("Asp", "GAU", "GAC");
        addCodons("Glu", "GAA", "GAG");
        addCodons("Gly", "GGU", "GGC", "GGA", "GGG");

        amino_Acids.put("Phe", "Phenylalanine");
        amino_Acids.put("Leu", "Leucine");
        amino_Acids.put("Ser", "Serine");
        amino_Acids.put("Tyr", "Tyrosine");
        amino_Acids.put("Cys", "Cysteine");
        amino_Acids.put("Trp", "Tryptophan");
        amino_Acids.put("Pro", "Proline");
        amino_Acids.put("His", "Histidine");
        amino_Acids.put("Gln", "Glutamine");
        amino_Acids.put("Arg", "Arginine");
        amino_Acids.put("Ile", "Isoleucine");
        amino_Acids.put("Met", "Methionine");
        amino_Acids.put("Thr", "Threonine");
        amino_Acids.put("Asn", "Asparagine");
        amino_Acids.put("Lys", "Lysine");
        amino_Acids.put("Val", "Valine");
        amino_Acids.put("Ala", "Alanine");
        amino_Acids.put("Asp", "Aspartic acid");
        amino_Acids.put("Glu", "Glutamic acid");
        amino_Acids.put("Gly", "Glycine");
        amino_Acids.put("Stop", "Stop codon");
    }

    private static void addCodons(String aminoAcid, String... codons) {
        for (String codon : codons) {
            codon_Table.put(codon, aminoAcid);
        }
    }

    public static void main(String[] args) {
        String defaultPath = "src/main/java/BioInfo/GCF_003575465.1_ASM357546v1_genomic.fna";
        String filePath = args.length > 0 ? args[0] : defaultPath;
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        System.out.println("  File: " + file.getName());

        String dnaSequence = readFastaFile(file);
        if (dnaSequence == null || dnaSequence.isEmpty()) {
            return;
        }
        System.out.println("  Genome length: " + dnaSequence.length());
        System.out.println("  First 80 bases: " + dnaSequence.substring(0, Math.min(80, dnaSequence.length())));
        System.out.println();

        String rnaSequence = dnaSequence.toUpperCase().replace('T', 'U');
        System.out.println("  RNA length: " + rnaSequence.length());
        System.out.println("  First 80 bases: " + rnaSequence.substring(0, Math.min(80, rnaSequence.length())));
        System.out.println();

        Map<String, Integer> codonCounts = countCodons(rnaSequence);
        int totalCodons = codonCounts.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("  Total codons counted: " + totalCodons);
        System.out.println("  Unique codons found: " + codonCounts.size());
        System.out.println();

        System.out.println("TOP 10 MOST FREQUENT CODONS");
        displayTop10(codonCounts);
        System.out.println();

        System.out.println("ALL 64 CODON FREQUENCIES");
        displayFullCodonTable(codonCounts);
        System.out.println();

        System.out.println("AMINO ACID USAGE PROFILE");
        Map<String, Integer> aminoAcidCounts = getAminoAcidCounts(codonCounts);
        displayAminoAcidProfile(aminoAcidCounts);
        System.out.println();

        System.out.println("TOP AMINO ACIDS THE VIRUS/BACTERIA RELIES ON");
        List<String> topAminoAcids = getTopAminoAcids(aminoAcidCounts, 10);
        for (int i = 0; i < topAminoAcids.size(); i++) {
            String aa = topAminoAcids.get(i);
            String fullName = amino_Acids.getOrDefault(aa, aa);
            System.out.println((i + 1) + ". " + fullName + " (" + aa + ") - count: " + aminoAcidCounts.get(aa));
        }
        System.out.println();

        System.out.println("Foods lacking the phage's top amino acids: ");
        queryAIForFoods(topAminoAcids);
    }

    private static String readFastaFile(File file) {
        StringBuilder sequence = new StringBuilder();
        String headerLine = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String l;
            while ((l = reader.readLine()) != null) {
                l = l.trim();
                if (l.startsWith(">")) {
                    if (headerLine == null) {
                        headerLine = l;
                        System.out.println("  Header: " + headerLine);
                    }
                    continue;
                }

                String cleaned = l.toUpperCase().replaceAll("[^ACGT]", "");
                sequence.append(cleaned);
            }
        } catch (IOException e) {
            System.err.println("Error" + e.getMessage());
            return null;
        }

        return sequence.toString();
    }

    private static Map<String, Integer> countCodons(String rna) {
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (int i = 0; i + 3 <= rna.length(); i++) {
            String codon = rna.substring(i, i + 3);
            counts.put(codon, counts.getOrDefault(codon, 0) + 1);
        }

        return counts;
    }

    private static void displayTop10(Map<String, Integer> codonCounts) {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(codonCounts.entrySet());

        sorted.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        sorted = sorted.subList(0, Math.min(10, sorted.size()));

        int rank = 1;
        for (Map.Entry<String, Integer> entry : sorted) {
            String codon = entry.getKey();
            int count = entry.getValue();
            String aminoAcid = codon_Table.getOrDefault(codon, "???");
            System.out.println(rank + ". " + codon + " (" + aminoAcid + "): " + count);
            rank++;
        }
    }

    private static void displayFullCodonTable(Map<String, Integer> codonCounts) {
        for (Map.Entry<String, Integer> entry : codonCounts.entrySet()) {
            String codon = entry.getKey();
            int count = entry.getValue();
            String aa = codon_Table.getOrDefault(codon, "???");
            System.out.println(codon + " (" + aa + "): " + count);
        }
    }

    private static Map<String, Integer> getAminoAcidCounts(Map<String, Integer> codonCounts) {
        Map<String, Integer> aaCounts = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : codonCounts.entrySet()) {
            String aa = codon_Table.getOrDefault(entry.getKey(), "???");
            aaCounts.merge(aa, entry.getValue(), Integer::sum);
        }
        return aaCounts;
    }

    private static void displayAminoAcidProfile(Map<String, Integer> aaCounts) {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(aaCounts.entrySet());

        sorted.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        for (Map.Entry<String, Integer> entry : sorted) {
            String aa = entry.getKey();
            int count = entry.getValue();
            String fullName = amino_Acids.getOrDefault(aa, aa);
            System.out.println(fullName + " (" + aa + "): " + count);
        }
    }

    private static List<String> getTopAminoAcids(Map<String, Integer> aaCounts, int n) {
        return aaCounts.entrySet().stream()
                .filter(e -> !e.getKey().equals("Stop"))
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private static void queryAIForFoods(List<String> topAminoAcids) {
        List<String> aaFullNames = new ArrayList<>();

        for (String aa : topAminoAcids) {
            aaFullNames.add(amino_Acids.getOrDefault(aa, aa));
        }

        String aminoAcidList = String.join(", ", aaFullNames);

        String prompt = "Please list common everyday foods that are naturally low or lacking in the following amino acids: "
                + aminoAcidList + ". " +
                "For each food, explain which of these amino acids it lacks and why. " +
                "Focus on practical, everyday foods. Format your answer clearly with numbered items.";

        System.out.println("  Finding foods that lack: " + aminoAcidList);
        System.out.println();

        try {
            String response = askAI(prompt);
            response = response.replace('–', '-').replace('—', '-').replace('’', '\'').replace('“', '"').replace('”',
                    '"');
            String[] lines = response.split("\n");
            for (String line : lines) {
                System.out.println("  " + line);
            }
        } catch (Exception e) {
            System.err.println("Error querying AI: " + e.getMessage());
        }
    }

    private static String askAI(String prompt) throws Exception {

        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key="
                + GEMINI_API_KEY;
        URL url = URI.create(endpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JsonObject payload = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject contentObj = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject partObj = new JsonObject();
        partObj.addProperty("text", prompt);
        parts.add(partObj);
        contentObj.add("parts", parts);
        contents.add(contentObj);
        payload.add("contents", contents);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
        }

        int statusCode = conn.getResponseCode();
        if (statusCode != 200) {
            try (BufferedReader errBr = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                String errResponse = errBr.lines().collect(Collectors.joining("\n"));
                throw new RuntimeException("API error (HTTP " + statusCode + "): " + errResponse);
            }
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        conn.disconnect();

        JsonObject responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();
        if (responseJson.has("candidates") && responseJson.getAsJsonArray("candidates").size() > 0) {
            JsonObject candidate = responseJson.getAsJsonArray("candidates").get(0).getAsJsonObject();
            if (candidate.has("content") && candidate.getAsJsonObject("content").has("parts")) {
                JsonArray partsArr = candidate.getAsJsonObject("content").getAsJsonArray("parts");
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < partsArr.size(); i++) {
                    JsonObject p = partsArr.get(i).getAsJsonObject();
                    if (p.has("text")) {
                        result.append(p.get("text").getAsString());
                    }
                }
                return result.toString();
            }
        }
        return response.toString();
    }

}
