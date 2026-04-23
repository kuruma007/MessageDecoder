package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class SecretMessageDecoder {

    static class Cell {
        int x, y;
        char ch;

        Cell(int x, char ch, int y) {
            this.x = x;
            this.y = y;
            this.ch = ch;
        }
    }

    public static void decodeMessage(String urlString) {
        List<Cell> cells = new ArrayList<>();

        try {
            // Fetch + parse HTML directly
            Document doc = Jsoup.connect(urlString)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            // Google Docs data is inside table rows
            Elements rows = doc.select("table tr");

            boolean isHeader = true;

            for (Element row : rows) {
                Elements cols = row.select("td");

                if (cols.size() < 3) continue;

                // Skip header
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                try {
                    int x = Integer.parseInt(cols.get(0).text().trim());
                    char ch = cols.get(1).text().trim().charAt(0);
                    int y = Integer.parseInt(cols.get(2).text().trim());

                    cells.add(new Cell(x, ch, y));
                } catch (Exception ignored) {
                    // skip malformed rows
                    ignored.printStackTrace();;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reading URL: " + e.getMessage());
            return;
        }

        // Step 2: Determine grid size
        int maxX = 0, maxY = 0;
        for (Cell c : cells) {
            maxX = Math.max(maxX, c.x);
            maxY = Math.max(maxY, c.y);
        }

        // Step 3: Initialize grid
        char[][] grid = new char[maxY + 1][maxX + 1];
        for (int i = 0; i <= maxY; i++) {
            Arrays.fill(grid[i], ' ');
        }

        // Step 4: Fill grid
        for (Cell c : cells) {
            grid[c.y][c.x] = c.ch;
        }

        // Step 5: Print grid
        for (int i = 0; i <= maxY; i++) {
            for (int j = 0; j <= maxX; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        String url = "https://docs.google.com/document/d/e/2PACX-1vSvM5gDlNvt7npYHhp_XfsJvuntUhq184By5xO_pA4b_gCWeXb6dM6ZxwN8rE6S4ghUsCj2VKR21oEP/pub";
        decodeMessage(url);
    }
}