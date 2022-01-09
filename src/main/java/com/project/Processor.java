package com.project;

import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Processor {
    private static final String SPLIT_REGEX = ",";

    private final TreeMap<Integer, Integer> bid = new TreeMap();
    private final TreeMap<Integer, Integer> ask = new TreeMap();

    void process(Supplier<String> source, Consumer<String> drain) {
        while (true) {
            String line = source.get();
            if (Objects.isNull(line)) {
                break;
            }
            char commandType = line.charAt(0);
            switch (commandType) {
                case 'q':
                    drain.accept(queryProcess(line));
                    break;
                case 'u':
                    updateProcess(line);
                    break;
                case 'o':
                    orderProcess(line);
                    break;
            }
        }
    }

    private void updateProcess(String line) {
        String[] words = line.split(SPLIT_REGEX);
        int price = Integer.parseInt(words[1]);
        int size = Integer.parseInt(words[2]);
        if (words[3].equals("bid")) {
            if (size == 0) {
                bid.remove(price);
            } else {
                bid.put(price, size);
            }
        } else if (words[3].equals("ask")) {
            if (size == 0) {
                ask.remove(price);
            } else {
                ask.put(price, size);
            }
        }
    }

    private String queryProcess(String line) {
        StringBuilder sb = new StringBuilder();
        String[] words = line.split(SPLIT_REGEX);
        switch (words[1]) {
            case "best_bid":
                sb.append(bid.lastKey())
                        .append(",")
                        .append(bid.getOrDefault(bid.lastKey(), 0));
                break;
            case "best_ask":
                sb.append(ask.firstKey())
                        .append(",")
                        .append(ask.getOrDefault(ask.firstKey(), 0));
                break;
            case "size":
                int size = Integer.parseInt(words[2]);
                if (Objects.nonNull(bid.get(size))) {
                    sb.append(bid.get(size));
                }
                if (Objects.nonNull(ask.get(size))) {
                    sb.append(ask.get(size));
                }
                break;
        }
        return sb.toString();
    }

    private void orderProcess(String line) {
        String[] splitLine = line.split(SPLIT_REGEX);
        if (splitLine[1].equals("sell")) {
            sell(splitLine);
        } else if (splitLine[1].equals("buy")) {
            buy(splitLine);
        }
    }

    private void sell(String[] arrays) {
        int sizeShares = Integer.parseInt(arrays[2]);
        while (sizeShares != 0) {
            int lastKey = bid.lastKey();
            if (bid.get(lastKey) <= sizeShares) {
                sizeShares -= bid.get(lastKey);
                bid.remove(lastKey);
            } else {
                bid.put(lastKey, bid.get(lastKey) - sizeShares);
                sizeShares = 0;
            }
        }
    }

    private void buy(String[] arrays) {
        int sizeShares = Integer.parseInt(arrays[2]);
        while (sizeShares != 0) {
            int firstKey = ask.firstKey();
            if (ask.get(firstKey) <= sizeShares) {
                sizeShares -= ask.get(firstKey);
                ask.remove(firstKey);
            } else {
                ask.put(firstKey, ask.get(firstKey) - sizeShares);
                sizeShares = 0;
            }
        }
    }


}
